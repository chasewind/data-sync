package org.example.common.dao;

import lombok.extern.slf4j.Slf4j;
import org.apache.empire.data.DataType;
import org.apache.empire.db.DBTable;
import org.apache.empire.db.DBTableColumn;
import org.apache.empire.dbms.DBMSHandler;
import org.example.common.BaseDb;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据表
 */
@Slf4j
public class BaseDao extends DBTable {
    /**自增id*/
    public final DBTableColumn ID;
    public final DBTableColumn CREATED_TIME;

    public final DBTableColumn UPDATED_TIME;

    private BaseDb baseDb;

    public BaseDao(String name, BaseDb db) {
        super(name, db);
        this.baseDb = db;
        ID = addColumn("id", DataType.AUTOINC, 0, true);
        CREATED_TIME=addColumn("created_time",DataType.DATETIME,0,false);
        UPDATED_TIME=addColumn("updated_time",DataType.DATETIME,0,false);
        setPrimaryKey(ID);
    }

    public Connection getConnection() throws SQLException {
        return baseDb.getConnection();
    }

    public DBMSHandler getDBMSHandler(  ) {
         return baseDb.getDBMSHandler();
    }

}
