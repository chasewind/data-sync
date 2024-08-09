package org.example.common.loader;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.common.BaseDb;
import org.example.common.DbStarter;
import org.example.common.model.Pair;
import org.example.common.model.SchemaSyncTable;
import org.example.common.reader.MySqlReader;
import org.example.common.reader.ReaderSegment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class LoaderTask implements Callable<List<Map<String, Object>>> {
    private CountDownLatch latch;
    private ReaderSegment parentSegment;
    private DbStarter dbStarter;

    public LoaderTask(DbStarter dbStarter, CountDownLatch latch, ReaderSegment parentSegment) {
        this.latch = latch;
        this.parentSegment = parentSegment;
        this.dbStarter = dbStarter;
    }

    public static List<Map<String, Object>> queryChildTableData(DbStarter dbStarter, SchemaSyncTable childTable,
                                                                List<WhereClause> whereClauseList) throws SQLException {
        List<Map<String, Object>> childTableData = new ArrayList<>();
        BaseDb bizDb = MySqlReader.getBaseDb(dbStarter, childTable);
        if (bizDb != null) {

            return MySqlReader.readAndConvertChildData(bizDb, childTable,
                    whereClauseList);
        }
        return childTableData;
    }

    @Override
    public List<Map<String, Object>> call() throws Exception {
        try {
            SchemaSyncTable schemaSyncTable = parentSegment.getSchemaSyncTable();
            BaseDb bizDb = MySqlReader.getBaseDb(dbStarter, schemaSyncTable);
            if (bizDb == null) {
                latch.countDown();
                return new ArrayList<>();
            }
            //加载当前分段数据
            List<Map<String, Object>> parentTableData = MySqlReader.readAndConvert(bizDb, parentSegment);

            log.info("table:{},size:{}", parentSegment.getRealTableName(), parentTableData.size());
            if(CollectionUtils.isEmpty(parentTableData)){
                return new ArrayList<>();
            }
            //加载子节点对应数据
            //目前只接受一层子节点
            List<SchemaSyncTable> children = schemaSyncTable.getChildren();
            loadChildrenData(children, parentTableData);
            latch.countDown();
            return parentTableData;

        } catch (Exception e) {
            log.error("load fail ", e);
            throw new RuntimeException(e);
        }
    }

    private void loadChildrenData(List<SchemaSyncTable> children, List<Map<String, Object>> parentTableData) throws SQLException {
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        //子节点数据遍历涉及到分库分表需要多表查询，涉及到单表是相对比较简单的
        //拿出父子表对应的字段和值
        //比如订单主表是id，子表为记为p_id,子表查询拼接sql 就是where p_id in (id1,id2,id3)
        //如果是多个对应关系的话，主表 c1,c2，子表是 cx1和cx2，sql拼接就是 where cx1 in (....) and cx2 in (....)
        //这里确保外层循环次数最少，所以先用表关系做外循环
        for (SchemaSyncTable child : children) {
            List<Pair<String, String>> columnRelation = child.getColumnRelation();
            List<WhereClause> whereClauseList = new ArrayList<>();
            List<String> childRouteKeys = new ArrayList<>();
            List<String> parentRouteKeys = new ArrayList<>();
            for (Pair<String, String> pair : columnRelation) {
                String parentKey = pair.getKey();
                String childKey = pair.getValue();
                parentRouteKeys.add(parentKey);
                childRouteKeys.add(childKey);
                //拼接条件
                WhereClause whereClause = new WhereClause();
                whereClause.setColumnName(childKey);
                whereClause.setColumnValue(new ArrayList<>());
                for (Map<String, Object> parentRow : parentTableData) {
                    Object parentValue = parentRow.get(parentKey);
                    whereClause.getColumnValue().add(String.valueOf(parentValue));
                }
                whereClauseList.add(whereClause);
            }

            List<Map<String, Object>> childTableData = queryChildTableData(dbStarter, child,
                    whereClauseList);
            //组织为两层结构的数据
            buildDataStructure(child, parentTableData, parentRouteKeys, childTableData, childRouteKeys);

        }
    }

    private void buildDataStructure(SchemaSyncTable child, List<Map<String, Object>> parentTableData,
                                    List<String> parentRouteKeys, List<Map<String, Object>> childTableData,
                                    List<String> childRouteKeys) {
        //拼接数据,需要先记录唯一key然后父子关联
        //当前对象转换如果是一对一，就是key--->map 如果是一对多，就是key--->list<map>
        Map<String, Map<String, Object>> parentDataMap = convertForOneToOne(parentTableData, parentRouteKeys);

        if (child.getRelation() == 2) {
            //一对一
            Map<String, Map<String, Object>> oneToOneChildMap = convertForOneToOne(childTableData, childRouteKeys);
            //放映射关系
            parentDataMap.forEach((k, v) -> {
                //取子表数据
                Map<String, Object> childMap = oneToOneChildMap.get(k);
                if (childMap != null) {
                    v.put(child.getTableAliaName(), childMap);
                }
            });
        } else if (child.getRelation() == 3) {
            //一对多
            Map<String, List<Map<String, Object>>> oneToManyChildMap = convertForOneToMany(childTableData,
                    childRouteKeys);
            //放映射关系
            parentDataMap.forEach((k, v) -> {
                List<Map<String, Object>> childMap = oneToManyChildMap.get(k);
                if (CollectionUtils.isNotEmpty(childMap)) {
                    v.put(child.getTableAliaName(), childMap);
                }
            });
        } else {
            log.info("you should not go here,may be something wrong !!!");
        }
    }

    private Map<String, List<Map<String, Object>>> convertForOneToMany(List<Map<String, Object>> childTableData,
                                                                       List<String> childRouteKeys) {
        Map<String, List<Map<String, Object>>> mapData = new HashMap<>();
        for (Map<String, Object> childRow : childTableData) {
            //这里为了让过程更清晰，没有使用stream形式
            List<String> keyData = new ArrayList<>();
            for (String oneKey : childRouteKeys) {
                keyData.add(String.valueOf(childRow.get(oneKey)));
            }
            String key = StringUtils.join(keyData, "_");
            mapData.computeIfAbsent(key, k -> new ArrayList<>());
            mapData.get(key).add(childRow);
        }
        return mapData;
    }

    private Map<String, Map<String, Object>> convertForOneToOne(List<Map<String, Object>> childTableData,
                                                                List<String> childRouteKeys) {
        Map<String, Map<String, Object>> mapData = new HashMap<>();
        for (Map<String, Object> childRow : childTableData) {
            //这里为了让过程更清晰，没有使用stream形式
            List<String> keyData = new ArrayList<>();
            for (String oneKey : childRouteKeys) {
                keyData.add(String.valueOf(childRow.get(oneKey)));
            }
            String key = StringUtils.join(keyData, "_");
            mapData.put(key, childRow);
        }
        return mapData;
    }
}
