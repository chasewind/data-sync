package org.example.common.web;

import com.google.gson.Gson;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.common.BaseDb;
import org.example.common.DbStarter;
import org.example.common.dao.SchemeSyncTableDao;
import org.example.common.dao.SearchSchemaDao;
import org.example.common.model.MySqlColumnInfo;
import org.example.common.model.SchemaSyncTable;
import org.example.common.model.SearchSchema;
import org.example.common.dao.JdbcInfoDao;
import org.example.common.model.JdbcInfo;
import org.example.common.reader.MySqlReader;
import org.example.common.reader.ReaderSegment;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ConfigController {
    @Resource
    private DbStarter dbStarter ;

    @RequestMapping("/getAllJdbcInfo")
    public List<JdbcInfo> queryAllJdbc() throws SQLException {
        JdbcInfoDao jdbcInfoTable = new JdbcInfoDao("",dbStarter.getDefaultDb());
        return jdbcInfoTable.queryAll();
    }
    @RequestMapping("/getAllSchema")
    public List<SearchSchema>queryAllSchema() throws SQLException {
        SearchSchemaDao searchSchemaTable= new SearchSchemaDao("",dbStarter.getDefaultDb());
        return searchSchemaTable.queryAll();
    }

    @RequestMapping("/loadData")
    public String loadData() throws SQLException {

        JdbcInfoDao jdbcInfoTable = new JdbcInfoDao("",dbStarter.getDefaultDb());
        List<JdbcInfo> jdbcList = jdbcInfoTable.queryAll();
        if(CollectionUtils.isNotEmpty(jdbcList)){
            JdbcInfo firstJdbcInfo= jdbcList.get(0);
            dbStarter.init(firstJdbcInfo);
            //利用数据库读取数据
            BaseDb bizDb = dbStarter.getBizDb(firstJdbcInfo.getJdbcName());
            if(bizDb!=null){
                String sql ="select * from order_info_1";
                try {
                    List<Map<String, Object>> list =bizDb.executeQuery(sql);
                    list.forEach(e->System.out.println(new Gson().toJson(e)));
                } catch (SQLException e) {
                    throw new RuntimeException("数据查询异常啦"+sql,e);
                }
            }
        }
        return "ok";
    }

    @RequestMapping(method = RequestMethod.GET,value = "/loadOneSchema")
    public  List<SchemaSyncTable> loadOneSchema(@RequestParam("schemaId") Integer schemaId) throws SQLException {
        SchemeSyncTableDao schemeSyncTableDao = new SchemeSyncTableDao("",dbStarter.getDefaultDb());
       return  schemeSyncTableDao.queryBySchemaId(schemaId);
    }


    @RequestMapping(method = RequestMethod.GET,value = "/loadSegmentInfo")
    public  List<ReaderSegment> loadSegmentInfo(@RequestParam("schemaId") Integer schemaId) throws SQLException {
        SchemeSyncTableDao schemeSyncTableDao = new SchemeSyncTableDao("",dbStarter.getDefaultDb());
        List<SchemaSyncTable> syncTableList= schemeSyncTableDao.queryBySchemaId(schemaId);
        List<SchemaSyncTable> parentTableList = syncTableList.stream().filter(e -> e.getParentId() == 0).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(parentTableList)){
            return new ArrayList<>();
        }
        SchemaSyncTable parentTable = parentTableList.get(0);
        JdbcInfoDao jdbcInfoTable = new JdbcInfoDao("",dbStarter.getDefaultDb());
        List<JdbcInfo> jdbcList = jdbcInfoTable.queryAll();
        if(CollectionUtils.isNotEmpty(jdbcList)){
            JdbcInfo firstJdbcInfo= jdbcList.stream().filter(e-> Objects.equals(e.getId(),
                    parentTable.getDatasourceId())).findFirst().get();
            dbStarter.init(firstJdbcInfo);
            //利用数据库读取数据
            BaseDb bizDb = dbStarter.getBizDb(firstJdbcInfo.getJdbcName());
            if(bizDb!=null){
               return MySqlReader.readParentTableSegmentInfo(bizDb,parentTable);
            }
        }
        return new ArrayList<>();
    }

    @RequestMapping(method = RequestMethod.GET,value = "/loadColumnMetaInfo")
    public  List<MySqlColumnInfo> loadColumnMetaInfo(@RequestParam("schemaId") Integer schemaId) throws SQLException {
        SchemeSyncTableDao schemeSyncTableDao = new SchemeSyncTableDao("",dbStarter.getDefaultDb());
        List<SchemaSyncTable> syncTableList= schemeSyncTableDao.queryBySchemaId(schemaId);
        List<SchemaSyncTable> parentTableList = syncTableList.stream().filter(e -> e.getParentId() == 0).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(parentTableList)){
            return new ArrayList<>();
        }
        SchemaSyncTable parentTable = parentTableList.get(0);
        JdbcInfoDao jdbcInfoTable = new JdbcInfoDao("",dbStarter.getDefaultDb());
        List<JdbcInfo> jdbcList = jdbcInfoTable.queryAll();
        if(CollectionUtils.isNotEmpty(jdbcList)){
            JdbcInfo firstJdbcInfo= jdbcList.stream().filter(e-> Objects.equals(e.getId(),
                    parentTable.getDatasourceId())).findFirst().get();
            dbStarter.init(firstJdbcInfo);
            //利用数据库读取数据
            BaseDb bizDb = dbStarter.getBizDb(firstJdbcInfo.getJdbcName());
            if(bizDb!=null){
                return MySqlReader.readColumnMetaFromDb(firstJdbcInfo,bizDb,parentTable);
            }
        }
        return new ArrayList<>();
    }


    @RequestMapping(method = RequestMethod.GET,value = "/loadTableData")
    public  List<Map<String, Object>> loadTableData(@RequestParam("schemaId") Integer schemaId) throws SQLException {
        SchemeSyncTableDao schemeSyncTableDao = new SchemeSyncTableDao("",dbStarter.getDefaultDb());
        List<SchemaSyncTable> syncTableList= schemeSyncTableDao.queryBySchemaId(schemaId);
        List<SchemaSyncTable> parentTableList = syncTableList.stream().filter(e -> e.getParentId() == 0).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(parentTableList)){
            return new ArrayList<>();
        }
        SchemaSyncTable parentTable = parentTableList.get(0);
        JdbcInfoDao jdbcInfoTable = new JdbcInfoDao("",dbStarter.getDefaultDb());
        List<JdbcInfo> jdbcList = jdbcInfoTable.queryAll();
        if(CollectionUtils.isNotEmpty(jdbcList)){
            JdbcInfo firstJdbcInfo= jdbcList.stream().filter(e-> Objects.equals(e.getId(),
                    parentTable.getDatasourceId())).findFirst().get();
            dbStarter.init(firstJdbcInfo);
            //利用数据库读取数据
            BaseDb bizDb = dbStarter.getBizDb(firstJdbcInfo.getJdbcName());
            if(bizDb!=null){
                ReaderSegment readerSegment = new ReaderSegment();
                readerSegment.setSchemaSyncTable(parentTable);
                readerSegment.setStartPkPos(1L);
                readerSegment.setEndPkPos(30L);
                readerSegment.setRealTableName("order_info_0");
                return MySqlReader.readAndConvert(bizDb,readerSegment);
            }
        }
        return new ArrayList<>();
    }
}
