package org.example.common;

import com.zaxxer.hikari.HikariConfig;
import lombok.extern.slf4j.Slf4j;
import org.example.common.model.JdbcInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class DbStarter {

    public static final String DEFAULT_DB="inner_default";

    private BaseDb defaultDb;
    private Map<String,BaseDb> bizDbMap = new HashMap<>();

    @Value("${jdbc.default.url}")
    private String default_jdbc_url;
    @Value("${jdbc.default.user}")
    private String default_jdbc_user;
    @Value("${jdbc.default.password}")
    private String default_jdbc_password;

    @PostConstruct
    public void initDefault(){
        log.info("init default");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(default_jdbc_url);
        config.setUsername(default_jdbc_user);
        config.setPassword(default_jdbc_password);
        defaultDb= new BaseDb(DEFAULT_DB,config);
        defaultDb.start();
    }

    public BaseDb getDefaultDb(){
        return defaultDb;
    }

    public void init(JdbcInfo jdbcInfo){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcInfo.getJdbcUrl());
        config.setUsername(jdbcInfo.getUsername());
        config.setPassword(jdbcInfo.getPassword());
        BaseDb bizDb= new BaseDb(jdbcInfo.getJdbcName(),config);
        bizDbMap.put(jdbcInfo.getJdbcName(),bizDb);
    }

    public BaseDb getBizDb(String uniqId){
        return bizDbMap.get(uniqId);
    }
}
