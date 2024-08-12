package org.example.common.dao;

import org.apache.empire.data.DataType;
import org.apache.empire.db.DBTableColumn;
import org.example.common.BaseDb;

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
}
