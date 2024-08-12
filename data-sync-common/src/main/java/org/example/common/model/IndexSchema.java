package org.example.common.model;

/**
 * 索引方案
 */
public class IndexSchema extends BaseModel{

    /**方案名*/
    private String schemaName;
    /**索引英文名*/
    private String indexName;
    /**索引别名*/
    private String indexAliasName;
    /**备注信息*/
    private String memo;

    /**分表路由策略*/
    private Integer shardRouteFlag;
    /**1 开启，0 关闭*/
    private Integer state;

}
