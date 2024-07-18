package org.example.common.model;

import lombok.Data;

@Data
public class JdbcInfo extends BaseModel {

    private String jdbcName;

    private String jdbcUrl;
    private String username;
    private String password;
}
