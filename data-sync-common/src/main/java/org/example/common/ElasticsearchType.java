package org.example.common;

import lombok.Getter;

@Getter
public enum ElasticsearchType {
    /**不分词的精确查找*/
    KEYWORD("keyword"),
    /**支持分词的字符串*/
    TEXT("text"),
    /**带符号的64位整数*/
    LONG("long"),
    /**带符号的32位整数*/
    INTEGER("integer"),
    /**带符号的16位整数*/
    SHORT("short"),
    /**带符号的8位整数*/
    BYTE("byte"),
    /**双精度64位IEEE754浮点数*/
    DOUBLE("double"),
    /**单精度32位IEEE754浮点数*/
    FLOAT("float"),
    /**日期类型*/
    DATE("date"),
    /**布尔类型*/
    BOOLEAN("boolean"),
    /**BASE64编码的字符串，不可以被搜索*/
    BINARY("binary");

    final String primaryValue;

    ElasticsearchType(String primaryValue) {
        this.primaryValue = primaryValue;
    }

}