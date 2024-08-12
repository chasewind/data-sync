package org.example.common.model;

import lombok.Data;

@Data
public class SearchSchema extends BaseModel {

    /**方案名称*/
    private String name;
    /**ES索引*/
    private String code;
    /**状态，开启还是禁用，默认开启*/
    private Integer status;
    /**备注信息*/
    private String note;
    /**ES索引别名*/
    private String alias;
    /**分表路由策略*/
    private Integer shardRouteFlag;


}
