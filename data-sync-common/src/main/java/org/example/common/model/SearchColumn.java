package org.example.common.model;


import lombok.Data;

@Data
public class SearchColumn  extends BaseModel{

    /**
     * 冗余 方案字段
     */
    private Integer schemaId;
    /**
     * 表id
     */
    private Integer tableId;
    /**
     * 字段名；
     */
    private String columnName;
    /**
     * 描述
     */
    private String note;

    /**
     * mysql中列类型
     */
    private String mysqlType;
    /**
     * ES类型；如果是字符串，不做模糊匹配这里要调整为keyword，在外层实现，这里只做标准存储
     */
    private String esType;
    /**
     * 标记字段，是否主键，是否分词，是否数组
     */
    private Integer columnFlag;
}
