package org.example.common.loader;

import lombok.Data;

import java.util.List;

@Data
public class WhereClause {

    private String columnName;
    /**
     * 只支持数字和字符串，数字转换为字符串
     */
    private List<String> columnValue;
}
