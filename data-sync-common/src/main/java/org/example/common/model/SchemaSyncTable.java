package org.example.common.model;

import lombok.Data;

import java.util.List;

@Data
public class SchemaSyncTable extends BaseModel {

    /**方案ID ,主键关联*/
    private Integer schemeId;
    /**来源于哪个数据源*/
    private Integer datasourceId;
    /**当前表名*/
    private String tableName;
    /**在ES中表名*/
    private String tableAliaName;

    /*** 唯一业务字段 确保能够有效查询*/
    private String uniqueField;
    /**
     * 自增id，可能和唯一业务字段相同，用来流式查询数据,例如分库分表的时候，每个表各自递增，同时还有唯一业务字段
     *
     * */
    private String pkField;
    /**
     * 表的类型，1 普通表，2 视图，3 函数
     */
    private Integer tableType;
    /**父级表id，如果为0则表示是根节点*/
    private Integer parentId;
    /**
     * 和父表关系，1 父表，2 一对一，3 一对多
     */
    private Integer relation;
    /**
     * 父子表关联字段以及映射关系 ，比如 在父表中记录为 itemId,siteCode在子表中对应的是id,siteCode,这种需要成对存在
     *
     */
    private List<Pair<String, String>> columnRelation;

    /**
     * 表名后缀，比如订单表分了 16个表，从0-15，就按照真实数字填充上来，无论是 00,01,02--15还是0,1,2,3--15
     * */
    private String tableSuffix;


    /**子节点*/
    private List<SchemaSyncTable> children;

}
