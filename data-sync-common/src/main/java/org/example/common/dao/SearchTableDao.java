package org.example.common.dao;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.empire.data.DataType;
import org.apache.empire.db.DBColumn;
import org.apache.empire.db.DBCommand;
import org.apache.empire.db.DBReader;
import org.apache.empire.db.DBTableColumn;
import org.apache.empire.db.context.DBContextStatic;
import org.apache.empire.dbms.DBMSHandler;
import org.example.common.BaseDb;
import org.example.common.model.Pair;
import org.example.common.model.SearchTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 一个schema对应的是一个主表加若干子表，构成一个完整的树形结构
 */
@Slf4j
public class SearchTableDao extends BaseDao {

    /**
     * 方案ID
     */
    public final DBTableColumn SCHEME_ID;
    /**
     * 来源于哪个数据源
     */
    public final DBTableColumn DATASOURCE_ID;
    /**
     * 当前表名
     */
    public final DBTableColumn TABLE_NAME;
    /**
     * 在ES中表名
     */
    public final DBTableColumn TABLE_ALIA_NAME;
    /**
     *  唯一业务字段
     */
    public final DBTableColumn UNIQUE_FIELD;

    /**
     * 自增id，可能和唯一业务字段相同，用来流式查询数据,例如分库分表的时候，每个表各自递增，同时还有唯一业务字段
     *
     * */
    public final DBTableColumn PK_FIELD;
    /**
     * 父级表id
     */
    public final DBTableColumn PARENT_ID;

    /**
     * 父级表对应关联的字段
     */
    public final DBTableColumn PARENT_COLUMN_NAMES;
    /**
     * 当前表和父级表关联对应的字段
     */
    public final DBTableColumn CURRENT_COLUMN_NAMES;

    /**表名后缀*/
    public final DBTableColumn TABLE_SUFFIX;

    /**
     * 和父表关系，1 父表，2 一对一，3 一对多
     */
    public final DBTableColumn TABLE_RELATION;
    public SearchTableDao(String name, BaseDb db) {
        super("schema_sync_table", db);
        SCHEME_ID = addColumn("scheme_id", DataType.INTEGER, 0, true);
        DATASOURCE_ID = addColumn("datasource_id", DataType.INTEGER, 0, true);
        TABLE_NAME =addColumn("table_name",DataType.VARCHAR,64,true);
        TABLE_ALIA_NAME=addColumn("table_alia_name",DataType.VARCHAR,64,true);
        UNIQUE_FIELD=addColumn("unique_field",DataType.VARCHAR,32,true);
        PK_FIELD =addColumn("pk_field",DataType.VARCHAR,32,true);
        PARENT_ID=addColumn("parent_id",DataType.INTEGER,0,true);
        TABLE_RELATION=addColumn("table_relation",DataType.INTEGER,0,true);
        //
        PARENT_COLUMN_NAMES=addColumn("parent_column_names",DataType.VARCHAR,64,true);
        CURRENT_COLUMN_NAMES=addColumn("current_column_names",DataType.VARCHAR,64,true);

        TABLE_SUFFIX=addColumn("table_suffix",DataType.VARCHAR,300,true);



        addIndex("SCHEMA_SYNC_SCHEME_ID_IDX", true, new DBColumn[]{SCHEME_ID,PARENT_ID});
    }

    public List<SearchTable> queryBySchemaId(Integer schemaId) throws SQLException {
        List<SearchTable> resultList = new ArrayList<>();
        Connection connection = getConnection();
        DBMSHandler dbms = getDBMSHandler();
        DBContextStatic context = new DBContextStatic(dbms, connection, true);
        db.open(context);
        try (
                DBReader reader = new DBReader(context)
        ) {
            DBCommand cmd = context.createCommand();
            cmd.where(this.SCHEME_ID.is(schemaId));
            cmd.select(
                    this.ID, this.SCHEME_ID, this.DATASOURCE_ID, this.TABLE_NAME, this.TABLE_ALIA_NAME,
                    this.UNIQUE_FIELD,this.PK_FIELD,
                    this.PARENT_ID,
                    this.TABLE_RELATION,this.PARENT_COLUMN_NAMES,this.CURRENT_COLUMN_NAMES,this.TABLE_SUFFIX,
                    this.CREATED_TIME,
                    this.UPDATED_TIME);
            reader.open(cmd);

            while (reader.moveNext()) {
                SearchTable data = new SearchTable();
                data.setId(reader.getInt(this.ID));
                data.setSchemeId(reader.getInt(this.SCHEME_ID));
                data.setDatasourceId(reader.getInt(this.DATASOURCE_ID));
                data.setTableName(reader.getString(this.TABLE_NAME));
                data.setTableAliaName(reader.getString(this.TABLE_ALIA_NAME));
                data.setUniqueField(reader.getString(this.UNIQUE_FIELD));
                data.setPkField(reader.getString(this.PK_FIELD));
                data.setParentId(reader.getInt(this.PARENT_ID));
                data.setRelation(reader.getInt(this.TABLE_RELATION));
                String parentColumns = reader.getString(this.PARENT_COLUMN_NAMES);
                String currentColumns = reader.getString(this.CURRENT_COLUMN_NAMES);
                String[] parentColumnArray=  StringUtils.split(parentColumns,",");
                String[] currentColumnArray=  StringUtils.split(currentColumns,",");
                List<Pair<String, String>> columnRelation = new ArrayList<>();
                for (int i=0;i<parentColumnArray.length;i++){
                    Pair<String, String> pair = new Pair<>(parentColumnArray[i],currentColumnArray[i]);
                    columnRelation.add(pair);
                }
                data.setColumnRelation(columnRelation);
                data.setTableSuffix(reader.getString(this.TABLE_SUFFIX));
                data.setCreatedTime(reader.getLocalDateTime(this.CREATED_TIME));
                data.setUpdatedTime(reader.getLocalDateTime(this.UPDATED_TIME));
                resultList.add(data);
            }
        } catch (Exception e) {
            log.error("fail", e);
        }
        if(CollectionUtils.isNotEmpty(resultList)){
            //折叠为树形结构
            return resultList.stream()
                    .filter(item -> {
                        item.setChildren(
                                resultList.stream()
                                        .filter(e -> Objects.equals(e.getParentId(), item.getId()))
                                        .collect(Collectors.toList()));
                        return Objects.equals(item.getParentId(), 0);
                    })
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();

    }
}
