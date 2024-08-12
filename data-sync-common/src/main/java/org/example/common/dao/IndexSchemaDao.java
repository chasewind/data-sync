package org.example.common.dao;

import lombok.extern.slf4j.Slf4j;
import org.apache.empire.data.DataType;
import org.apache.empire.db.DBCommand;
import org.apache.empire.db.DBReader;
import org.apache.empire.db.DBTableColumn;
import org.apache.empire.db.context.DBContextStatic;
import org.apache.empire.dbms.DBMSHandler;
import org.example.common.BaseDb;
import org.example.common.model.IndexSchema;
import org.example.common.model.JdbcInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class IndexSchemaDao extends BaseDao {
    public final DBTableColumn SCHEMA_NAME;

    public final DBTableColumn INDEX_NAME;

    public final DBTableColumn INDEX_ALIAS_NAME;

    public final DBTableColumn MEMO;

    public final DBTableColumn SHARD_ROUTE_FLAG;
    public final DBTableColumn STATE;

    public IndexSchemaDao(String name, BaseDb db) {
        super("schema_index", db);
        SCHEMA_NAME = addColumn("schema_name", DataType.VARCHAR, 64, true);
        INDEX_NAME = addColumn("index_name", DataType.VARCHAR, 64, true);
        INDEX_ALIAS_NAME = addColumn("index_alias_name", DataType.VARCHAR, 128, true);
        MEMO = addColumn("memo", DataType.VARCHAR, 200, true);
        SHARD_ROUTE_FLAG = addColumn("shard_route_flag", DataType.INTEGER, 0, true);
        STATE = addColumn("state", DataType.INTEGER, 0, true);
    }
    public List<IndexSchema> queryAll() throws SQLException {
        List<IndexSchema> resultList = new ArrayList<>();
        Connection connection = getConnection();
        DBMSHandler dbms = getDBMSHandler();
        DBContextStatic context = new DBContextStatic(dbms, connection, true);
        db.open(context);
        try (
                DBReader reader = new DBReader(context)
        ) {
            DBCommand cmd = context.createCommand();
            cmd.select(
                    this.ID, this.SCHEMA_NAME, this.INDEX_NAME, this.INDEX_ALIAS_NAME, this.SHARD_ROUTE_FLAG,this.STATE,
                    this.MEMO,this.CREATED_TIME,
                    this.UPDATED_TIME);
            reader.open(cmd);
            while (reader.moveNext()) {
                IndexSchema data = new IndexSchema();
                data.setId(reader.getInt(this.ID));
                data.setSchemaName(reader.getString(this.SCHEMA_NAME));
                data.setIndexName(reader.getString(this.INDEX_NAME));
                data.setIndexAliasName(reader.getString(this.INDEX_ALIAS_NAME));
                data.setShardRouteFlag(reader.getInt(this.SHARD_ROUTE_FLAG));
                data.setState(reader.getInt(this.STATE));
                data.setMemo(reader.getString(this.MEMO));
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
