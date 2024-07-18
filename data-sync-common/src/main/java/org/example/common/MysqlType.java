package org.example.common;

import lombok.Getter;

@Getter
public enum  MysqlType{
    TEXT("text"){
        @Override public SearchDataType transformType() {
            return SearchDataType.TEXT;
        }
    },
    LONGTEXT("longtext"){
        @Override public SearchDataType transformType() {
            return SearchDataType.TEXT;
        }
    },
    MEDIUMTEXT("mediumtext"){
        @Override public SearchDataType transformType() {
            return SearchDataType.TEXT;
        }
    },
    TINYTEXT("tinytext"){
        @Override public SearchDataType transformType() {
            return SearchDataType.TEXT;
        }
    },
    VARCHAR("varchar"){
        @Override public SearchDataType transformType() {
            return SearchDataType.TEXT;
        }
    },
    BINARY("binary"){
        @Override public SearchDataType transformType() {
            return SearchDataType.BLOB;
        }
    },
    CHARX("char"){
        @Override public SearchDataType transformType() {
            return SearchDataType.CHAR;
        }
    },
    TINYINT("tinyint"){
        @Override
        public SearchDataType transformType() {
            return SearchDataType.INTEGER;
        }
    },
    SMALLINT("smallint"){
        @Override public SearchDataType transformType() {
            return SearchDataType.INTEGER;
        }
    },
    INTX("int"){
        @Override public SearchDataType transformType() {
            return SearchDataType.INTEGER;
        }
    },
    BIGINT("bigint"){
        @Override public SearchDataType transformType() {
            return SearchDataType.LONG;
        }
    },
    FLOATX("float"){
        @Override public SearchDataType transformType() {
            return SearchDataType.FLOAT;
        }
    },
    DOUBLEX("double"){
        @Override public SearchDataType transformType() {
            return SearchDataType.DOUBLE;
        }
    },
    DECIMAL("decimal"){
        @Override public SearchDataType transformType() {
            return SearchDataType.DECIMAL;
        }
    },
    BIT("bit"){
        @Override public SearchDataType transformType() {
            return SearchDataType.BLOB;
        }
    },
    TIME("time"){
        @Override public SearchDataType transformType() {
            return SearchDataType.DATETIME;
        }
    },
    DATETIME("datetime"){
        @Override public SearchDataType transformType() {
            return SearchDataType.DATETIME;
        }
    },
    TIMESTAMP("timestamp"){
        @Override public SearchDataType transformType() {
            return SearchDataType.LONG;
        }
    };

    final String primaryValue;
    MysqlType(String primaryValue) {
        this.primaryValue = primaryValue;
    }

    public SearchDataType transformType() {
        throw new UnsupportedOperationException("无法进行类型转换；");
    }

    public static MysqlType getType(String type){

        switch (type.toLowerCase()) {
            case "text":
                return TEXT;
            case "longtext":
                return LONGTEXT;
            case "mediumtext":
                return MEDIUMTEXT;
            case "varchar":
                return VARCHAR;
            case "binary":
                return BINARY;
            case "char":
                return CHARX;
            case "tinyint":
                return TINYINT;
            case "smallint":
                return SMALLINT;
            case "int":
                return INTX;
            case "bigint":
                return BIGINT;
            case "float":
                return FLOATX;
            case "double":
                return DOUBLEX;
            case "decimal":
                return DECIMAL;
            case "bit":
                return BIT;
            case "time":
                return TIME;
            case "datetime":
                return DATETIME;
            case "timestamp":
                return TIMESTAMP;
            default:
                break;
        }
        return VARCHAR;

    }
}