package org.example.common;

import com.google.common.collect.Lists;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.empire.db.DBDatabase;
import org.apache.empire.dbms.DBMSHandler;
import org.apache.empire.dbms.mysql.DBMSHandlerMySQL;
import org.example.common.model.MySqlColumnInfo;
import org.example.common.reader.ReaderSegment;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 数据库,使用连接池相关技术托管连接,由于做数据迁移，会涉及到多个数据源，需要对每个数据源进行唯一区分，最后库和表构成树形关系
 */
@Slf4j
public class BaseDb extends DBDatabase {
    private static final String dbmsHandlerClass = "org.apache.empire.dbms.mysql.DBMSHandlerMySQL";

    private String datasourceId;
    private HikariConfig config;
    private HikariDataSource dataSource;
    private DBMSHandler dbms;

    public BaseDb(String datasourceId, HikariConfig config) {
        this.datasourceId = datasourceId;
        this.config = config;
        //设置默认值
        //
        this.config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        this.config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(30L));
        this.config.setMaximumPoolSize(10);
        this.config.setMinimumIdle(5);
        this.config.setIdleTimeout(TimeUnit.MINUTES.toMillis(10L));
        this.config.setMaxLifetime(TimeUnit.MINUTES.toMillis(20L));
    }

    public synchronized Connection getConnection() throws SQLException {
        //https://www.cnblogs.com/didispace/p/12291832.html
        if (dataSource == null) {
            dataSource = new HikariDataSource(config);
        }

        return dataSource.getConnection();
    }
    public List<ReaderSegment> executeSegmentQuery(String sql, int batchSize) throws SQLException {
        log.info("datasourceId:{},sql:{}",datasourceId,sql);
        try (ResultSet rs = this.getDBMSHandler().executeQuery(sql, new Object[]{}, true, this.getConnection())) {
            return resultSetToHeadAndTail(rs,batchSize);
        } catch (SQLException e) {
            throw new RuntimeException("数据查询异常啦" + sql, e);
        }
    }

    public List<MySqlColumnInfo> queryMetaInfoWithParam(String sql, Object[] sqlParams) throws SQLException {
        log.info("datasourceId:{},sql:{}",datasourceId,sql);
        List<MySqlColumnInfo> metaInfoList = new ArrayList<>();
        try (ResultSet rs = this.getDBMSHandler().executeQuery(sql, sqlParams, true, this.getConnection())) {
            while (rs.next()){
                MySqlColumnInfo columnInfo = new MySqlColumnInfo();
                columnInfo.setColumn(rs.getString("COLUMN_NAME"));
                columnInfo.setNote(rs.getString("COLUMN_COMMENT"));
                columnInfo.setType(MysqlType.getType(rs.getString("DATA_TYPE")).transformType());
                columnInfo.setPrk(rs.getString("COLUMN_KEY") != null && "PRI".equals(rs.getString("COLUMN_KEY")));
                metaInfoList.add(columnInfo);
            }
        } catch (SQLException e) {
            throw new RuntimeException("数据查询异常啦" + sql, e);
        }
        return metaInfoList;
    }


    public List<Map<String, Object>> executeQueryWithParam(String sql, Object[] sqlParams) throws SQLException {
        log.info("datasourceId:{},sql:{}",datasourceId,sql);
        try (ResultSet rs = this.getDBMSHandler().executeQuery(sql, sqlParams, true, this.getConnection())) {
            return resultSetToList(rs);
        } catch (SQLException e) {
            throw new RuntimeException("数据查询异常啦" + sql, e);
        }
    }
    public List<Map<String, Object>> executeQuery(String sql) throws SQLException {
        log.info("datasourceId:{},sql:{}",datasourceId,sql);
        try (ResultSet rs = this.getDBMSHandler().executeQuery(sql, new Object[]{}, true, this.getConnection())) {
            return resultSetToList(rs);
        } catch (SQLException e) {
            throw new RuntimeException("数据查询异常啦" + sql, e);
        }
    }

    public synchronized DBMSHandler getDBMSHandler() {
        try {
            if (dbms == null) {
                dbms = new DBMSHandlerMySQL();
            }
            return dbms;

        } catch (Exception e) {
            log.error("fail", e);
            throw new RuntimeException(e);
        }
    }

    private List<ReaderSegment> resultSetToHeadAndTail(ResultSet rs,int batchSize) throws java.sql.SQLException {
        if (rs == null) {
            return Collections.emptyList();
        }
        List<Long> pkList = new ArrayList<>();
        while (rs.next()) {
            //直接取到值
            pkList.add(rs.getLong(1));
        }
        List<ReaderSegment>resultList = new ArrayList<>();

        List<List<Long>> splitList = Lists.partition(pkList, batchSize);
        splitList.forEach(subList->{
            ReaderSegment segment = new ReaderSegment();
            segment.setStartPkPos(subList.get(0));
            segment.setEndPkPos(subList.get(subList.size()-1));
            segment.setDataSize(subList.size());
            resultList.add(segment);
        });
        return resultList;
    }
    private List<Map<String, Object>> resultSetToList(ResultSet rs) throws java.sql.SQLException {
        if (rs == null) {
            return Collections.emptyList();
        }
        //得到结果集(rs)的结构信息，比如字段数、字段名等
        ResultSetMetaData md = rs.getMetaData();
        //返回此 ResultSet 对象中的列数
        int columnCount = md.getColumnCount();
        List<Map<String, Object>> rows = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> row = new HashMap<>(16);
            for (int i = 1; i <= columnCount; i++) {
                String cName = md.getColumnName(i).toLowerCase();
                String mdType = md.getColumnTypeName(i);
                Object v = rs.getObject(i);
                Object newV = ParseDataType.parseDataByMysql(mdType, v);
                row.put(cName, newV);
            }
            rows.add(row);
        }
        return rows;
    }

    public void start() {
    }
}
