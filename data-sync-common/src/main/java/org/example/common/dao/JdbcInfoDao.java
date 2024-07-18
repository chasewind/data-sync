package org.example.common.dao;

import lombok.extern.slf4j.Slf4j;
import org.apache.empire.data.DataType;
import org.apache.empire.db.DBCommand;
import org.apache.empire.db.DBReader;
import org.apache.empire.db.DBTableColumn;
import org.apache.empire.db.context.DBContextStatic;
import org.apache.empire.dbms.DBMSHandler;
import org.example.common.BaseDb;
import org.example.common.model.JdbcInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JdbcInfoDao extends BaseDao {

    /**
     * 数据源名称
     */
    public final DBTableColumn NAME;

    public final DBTableColumn JDBC_URL;

    public final DBTableColumn JDBC_USERNAME;
    public final DBTableColumn JDBC_PASSWORD;


    public JdbcInfoDao(String name, BaseDb db) {
        super("jdbc_info", db);

        NAME = addColumn("jdbc_name", DataType.VARCHAR, 64, true);

        JDBC_URL = addColumn("jdbc_url", DataType.VARCHAR, 300, true);

        JDBC_USERNAME = addColumn("jdbc_username", DataType.VARCHAR, 300, true);

        JDBC_PASSWORD = addColumn("jdbc_password", DataType.VARCHAR, 300, true);

    }

    public List<JdbcInfo> queryAll() throws SQLException {
        List<JdbcInfo> resultList = new ArrayList<>();
        Connection connection = getConnection();
        DBMSHandler dbms = getDBMSHandler();
        DBContextStatic context = new DBContextStatic(dbms, connection, true);
        db.open(context);
        try (
                DBReader reader = new DBReader(context)
        ) {
            DBCommand cmd = context.createCommand();
            cmd.select(
                    this.ID, this.NAME, this.JDBC_URL, this.JDBC_USERNAME, this.JDBC_PASSWORD, this.CREATED_TIME,
                    this.UPDATED_TIME);
            reader.open(cmd);
            while (reader.moveNext()) {
                JdbcInfo data = new JdbcInfo();
                data.setId(reader.getInt(this.ID));
                data.setJdbcName(reader.getString(this.NAME));
                data.setJdbcUrl(reader.getString(this.JDBC_URL));
                data.setUsername(reader.getString(this.JDBC_USERNAME));
                data.setPassword(reader.getString(this.JDBC_PASSWORD));
                data.setCreatedTime(reader.getLocalDateTime(this.CREATED_TIME));
                data.setUpdatedTime(reader.getLocalDateTime(this.UPDATED_TIME));
                resultList.add(data);
            }
        } catch (Exception e) {
            log.error("fail", e);
        }
        return resultList;
    }
}
