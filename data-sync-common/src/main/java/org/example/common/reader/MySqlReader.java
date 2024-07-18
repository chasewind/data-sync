package org.example.common.reader;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.common.BaseDb;
import org.example.common.model.JdbcInfo;
import org.example.common.model.MySqlColumnInfo;
import org.example.common.model.SchemaSyncTable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class MySqlReader {

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
}
