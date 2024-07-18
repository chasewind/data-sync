package org.example.common;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class ParseDataType {
    public static Object parseDataByMysql(String fieldType, Object value) {
        if (value == null) {
            return null;
        }
        switch (fieldType.toLowerCase()) {
            case "text":
                break;
            case "longtext":
                break;
            case "mediumtext":
                break;
            case "varchar":
                break;
            case "binary":
                break;
            case "char":
                break;
            case "smallint":
                break;
            case "int":
                break;
            case "bigint":
                break;
            case "float":
                break;
            case "double":
                break;
            case "decimal":
                BigDecimal bigDecimal = (BigDecimal) value;
                return bigDecimal.doubleValue();
            case "bit":
                return "true".equalsIgnoreCase(String.valueOf(value))?1:0;
            case "time":
                break;
            case "datetime":
            case "timestamp":
                Timestamp timestamp = (Timestamp) value;
                return timestamp.getTime();
            default:
                break;
        }
        return value;
    }
}
