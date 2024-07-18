package org.example.common.model;

import lombok.Data;
import org.example.common.SearchDataType;

@Data
public class MySqlColumnInfo extends BaseModel{
    /**
     * 字段名；
     */
    private String column;
    /**
     * 描述
     */
    private String note;
    /**
     * 类型；
     */
    private SearchDataType type;
    /**
     * 是否主键；
     */
    private boolean isPrk;
}
