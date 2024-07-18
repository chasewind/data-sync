package org.example.common.dao;

import lombok.extern.slf4j.Slf4j;
import org.apache.empire.data.DataType;
import org.apache.empire.db.DBColumn;
import org.apache.empire.db.DBCommand;
import org.apache.empire.db.DBReader;
import org.apache.empire.db.DBTableColumn;
import org.apache.empire.db.context.DBContextStatic;
import org.apache.empire.dbms.DBMSHandler;
import org.example.common.BaseDb;
import org.example.common.model.SearchSchema;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SearchSchemaDao extends BaseDao {

    /**
     * 方案名称
     */
    public final DBTableColumn S_NAME;
    /**
     * ES索引
     */
    public final DBTableColumn S_CODE;
    /**
     * 状态，开启还是禁用，默认开启
     */
    public final DBTableColumn STATUS;
    /**
     * 备注信息
     */
    public final DBTableColumn NOTE;
    /**
     * ES索引别名
     */
    public final DBTableColumn ALIAS;
    /**
     * 路由类型
     */
    public final DBTableColumn ROUTE_TYPE;


    public SearchSchemaDao(String name, BaseDb db) {
        super("search_schema", db);
        S_NAME = addColumn("s_name", DataType.VARCHAR, 64, true);
        S_CODE = addColumn("s_code", DataType.VARCHAR, 64, true);
        ALIAS = addColumn("s_alias", DataType.VARCHAR, 64, true);
        STATUS = addColumn("s_status", DataType.INTEGER, 0, true);
        NOTE = addColumn("note", DataType.VARCHAR, 200, false);
        ROUTE_TYPE = addColumn("route_type", DataType.INTEGER, 0, false);
        addIndex("SCHEMA_NAME_IDX", true, new DBColumn[]{S_NAME});
        addIndex("SCHEMA_CODE_IDX", true, new DBColumn[]{S_CODE});
        addIndex("SCHEMA_ALIAS_IDX", true, new DBColumn[]{ALIAS});
    }

    public List<SearchSchema> queryAll() throws SQLException {
        List<SearchSchema> resultList = new ArrayList<>();
        Connection connection = getConnection();
        DBMSHandler dbms = getDBMSHandler();
        DBContextStatic context = new DBContextStatic(dbms, connection, true);
        db.open(context);
        try (
                DBReader reader = new DBReader(context)
        ) {
            DBCommand cmd = context.createCommand();
            cmd.select(
                    this.ID, this.S_NAME, this.S_CODE, this.ALIAS, this.STATUS, this.NOTE, this.ROUTE_TYPE,
                    this.CREATED_TIME,
                    this.UPDATED_TIME);
            reader.open(cmd);

            while (reader.moveNext()) {
                SearchSchema data = new SearchSchema();
                data.setId(reader.getInt(this.ID));
                data.setName(reader.getString(this.S_NAME));
                data.setCode(reader.getString(this.S_CODE));
                data.setAlias(reader.getString(this.ALIAS));
                data.setStatus(reader.getInt(this.STATUS));
                data.setNote(reader.getString(this.NOTE));
                data.setRouteType(reader.getInt(this.ROUTE_TYPE));
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
