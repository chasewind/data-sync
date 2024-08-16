package org.example.common.dao;

import lombok.extern.slf4j.Slf4j;
import org.apache.empire.data.DataType;
import org.apache.empire.db.DBCommand;
import org.apache.empire.db.DBReader;
import org.apache.empire.db.DBTableColumn;
import org.apache.empire.db.context.DBContextStatic;
import org.apache.empire.dbms.DBMSHandler;
import org.example.common.BaseDb;
import org.example.common.model.SearchColumn;
import org.example.common.model.SearchSchema;
import org.example.common.model.SearchTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SearchColumnDao extends BaseDao {
    /**
     * 方案ID
     */
    public final DBTableColumn SCHEME_ID;
    public final DBTableColumn TABLE_ID;
    public final DBTableColumn COLUMN_NAME;

    public final DBTableColumn MYSQL_TYPE;

    public final DBTableColumn ES_TYPE;
    public final DBTableColumn NOTE;

    public final DBTableColumn COLUMN_FLAG;
    public SearchColumnDao(String name, BaseDb db) {
        super("schema_sync_column", db);
        SCHEME_ID = addColumn("scheme_id", DataType.INTEGER, 0, true);
        TABLE_ID = addColumn("table_id", DataType.INTEGER, 0, true);
        COLUMN_NAME=addColumn("column_name",DataType.VARCHAR,64,true);
        MYSQL_TYPE=addColumn("mysql_type",DataType.VARCHAR,64,true);
        ES_TYPE=addColumn("es_type",DataType.VARCHAR,64,true);
        NOTE=addColumn("note",DataType.VARCHAR,128,false);
        COLUMN_FLAG = addColumn("column_flag", DataType.INTEGER, 0, true);
    }

    public List<SearchColumn> queryByTableId(int tableId)throws SQLException {
        List<SearchColumn> resultList = new ArrayList<>();
        Connection connection = getConnection();
        DBMSHandler dbms = getDBMSHandler();
        DBContextStatic context = new DBContextStatic(dbms, connection, true);
        db.open(context);
        try (
                DBReader reader = new DBReader(context)
        ) {
            DBCommand cmd = context.createCommand();
            cmd.where(this.TABLE_ID.is(tableId));
            cmd.select(
                    this.ID, this.SCHEME_ID, this.TABLE_ID, this.COLUMN_NAME, this.MYSQL_TYPE, this.ES_TYPE,
                    this.NOTE,this.COLUMN_FLAG,
                    this.CREATED_TIME,
                    this.UPDATED_TIME);
            reader.open(cmd);
            //这里不用while，只读取一条
            while (reader.moveNext()) {
                SearchColumn data = new SearchColumn();
                data.setId(reader.getInt(this.ID));
                //
                data.setSchemaId(reader.getInt(this.SCHEME_ID));
                data.setTableId(reader.getInt(this.TABLE_ID));
                data.setColumnName(reader.getString(this.COLUMN_NAME));
                data.setMysqlType(reader.getString(this.MYSQL_TYPE));
                data.setEsType(reader.getString(this.ES_TYPE));
                data.setNote(reader.getString(this.NOTE));
                data.setColumnFlag(reader.getInt(this.COLUMN_FLAG));
                //
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
