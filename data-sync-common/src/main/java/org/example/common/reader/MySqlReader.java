package org.example.common.reader;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.common.BaseDb;
import org.example.common.DbStarter;
import org.example.common.dao.JdbcInfoDao;
import org.example.common.loader.WhereClause;
import org.example.common.model.JdbcInfo;
import org.example.common.model.MySqlColumnInfo;
import org.example.common.model.SchemaSyncTable;

import java.sql.SQLException;
import java.util.*;

@Slf4j
public class MySqlReader {

    public static    BaseDb getBaseDb(DbStarter dbStarter, SchemaSyncTable syncTable) throws SQLException {
        JdbcInfoDao jdbcInfoTable = new JdbcInfoDao("",dbStarter.getDefaultDb());
        List<JdbcInfo> jdbcList = jdbcInfoTable.queryAll();
        if(CollectionUtils.isNotEmpty(jdbcList)) {
            JdbcInfo firstJdbcInfo = jdbcList.stream().filter(e -> Objects.equals(e.getId(),
                    syncTable.getDatasourceId())).findFirst().get();
            dbStarter.init(firstJdbcInfo);
            //利用数据库读取数据
           return dbStarter.getBizDb(firstJdbcInfo.getJdbcName());
        }
        return null;
    }

    public static List<ReaderSegment> readParentTableSegmentInfo(BaseDb baseDb, SchemaSyncTable parentTable) throws SQLException {
        List<ReaderSegment> segmentList = new ArrayList<>();
        Map<String,String> querySqlMap = new HashMap<>();
        if(StringUtils.isEmpty(parentTable.getTableSuffix())){
            //不分表
            String sql = "select "+parentTable.getPkField() + " from "+parentTable.getTableName()+" order by "+parentTable.getPkField();
            querySqlMap.put(parentTable.getTableName(),sql);
        }else{
            //分表
          String[]suffixArray =  StringUtils.split(parentTable.getTableSuffix(),",");
          for(String suffix:suffixArray){
              String sql =
                      "select "+parentTable.getPkField() + " from "+parentTable.getTableName()+suffix+" order by "+parentTable.getPkField();
              querySqlMap.put(parentTable.getTableName()+suffix,sql);
          }
        }
        //使用流式数据读取，手动截断
        for(Map.Entry<String,String> entry:querySqlMap.entrySet()){
            try {
                List<ReaderSegment> resultData =   baseDb.executeSegmentQuery(entry.getValue(),500);
                for(ReaderSegment segment:resultData){
                    segment.setRealTableName(entry.getKey());
                    segment.setSchemaSyncTable(parentTable);
                    segmentList.add(segment);
                }

            }catch (Exception e){
                log.error("ignore",e);
            }


        }


        return segmentList;

    }
    public static List<MySqlColumnInfo> readColumnMetaFromDb(JdbcInfo jdbcInfo, BaseDb baseDb,
                                                             SchemaSyncTable syncTable) throws SQLException {
        String dbName =jdbcInfo.getJdbcName();
        String tableName = "";
        if(StringUtils.isEmpty(syncTable.getTableSuffix())){
            tableName = syncTable.getTableName();
        }else{
            String[]suffixArray =  StringUtils.split(syncTable.getTableSuffix(),",");
            tableName = syncTable.getTableName()+suffixArray[0];
        }
       return  baseDb.queryMetaInfoWithParam("select COLUMN_NAME,DATA_TYPE,COLUMN_KEY,COLUMN_COMMENT from " +
                "information_schema" +
                ".columns where TABLE_SCHEMA=? and  TABLE_NAME=?", new Object[]{dbName,tableName});
    }

    public static List<Map<String, Object>> readAndConvert( BaseDb baseDb, ReaderSegment readerSegment) throws SQLException{

        String sql =
                "select * from "+readerSegment.getRealTableName() + " where "
                        +readerSegment.getSchemaSyncTable().getPkField() +" >= ? and " +readerSegment.getSchemaSyncTable().getPkField()+"< ?" ;

        return baseDb.executeQueryWithParam(sql,new Object[]{readerSegment.getStartPkPos(),readerSegment.getEndPkPos()});

    }

    public static List<Map<String, Object>> readAndConvertChildData(BaseDb bizDb,SchemaSyncTable childTable,
                                                                    List<WhereClause> whereClauseList) throws SQLException {
        List<Map<String, Object>>childDataList = new ArrayList<>();
        Map<String,String> querySqlMap = new HashMap<>();
        //遍历条件,拼接查询片段
        StringBuilder whereQuery = new StringBuilder(" where ");
        for(WhereClause whereClause:whereClauseList){
            //加入波浪符号，避免因为关键字冲突导致语法错误
            whereQuery.append(" `").append(whereClause.getColumnName()).append("` in (");
            //拼接数据
            whereQuery.append(StringUtils.join(whereClause.getColumnValue(),","));
            whereQuery.append(") and");
        }
        // 删除最后一个 "and"
        if (whereQuery.length() > 3) {
            whereQuery.setLength(whereQuery.length() - 3);
        }
        String whereCondition = whereQuery.toString();

        if(StringUtils.isEmpty(childTable.getTableSuffix())){
            //不分表
            String sql = "select * from "+childTable.getTableName()+" "+whereCondition;
            querySqlMap.put(childTable.getTableName(),sql);
        }else{
            //分表
            String[]suffixArray =  StringUtils.split(childTable.getTableSuffix(),",");
            for(String suffix:suffixArray){
                String sql =
                        "select * from "+childTable.getTableName()+suffix+" "+whereCondition;
                querySqlMap.put(childTable.getTableName()+suffix,sql);
            }
        }
        for(Map.Entry<String,String> entry:querySqlMap.entrySet()){
            //
            List<Map<String, Object>> list =bizDb.executeQuery(entry.getValue());
            log.info("table:{},sql:{},data size: {}",entry.getKey(),entry.getValue(),list.size());
            if(CollectionUtils.isNotEmpty(list)){
                childDataList.addAll(list);
            }
        }
        return childDataList;
    }
}
