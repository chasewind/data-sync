package org.debezium;

import io.debezium.connector.mysql.MySqlConnector;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import org.apache.kafka.connect.storage.FileOffsetBackingStore;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataLoadApp {

    public static void main(String[] args) {
        // 0. 配置数据库，添加用户，赋予主从同步的权限

        // 1. 生成配置
        Properties props = genProps();

        // 2. 构建 DebeziumEngine
        // 使用 Json 格式
        DebeziumEngine<ChangeEvent<String, String>> engine = DebeziumEngine.create(Json.class)
                .using(props)
                .notifying(record -> {
                    // record中会有操作的类型（增、删、改）和具体的数据
                    // key是主键
                    System.out.println("record.key() = " + record.key());
                    System.out.println("record.value() = " + record.value());
                })
                .using((success, message, error) -> {
                    // 强烈建议加上此部分的回调代码，方便查看错误信息

                    if (!success && error != null) {
                        // 报错回调
                        System.out.println("----------error------");
                        System.out.println(message);
                    }
                }).build();

        // 3. 正式运行
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(engine);
    }

    private static Properties genProps() {
        // 配置
        Properties props = new Properties();
        props.setProperty("connector.class", MySqlConnector.class.getCanonicalName());
        props.setProperty("name", "engine");
        props.setProperty("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore");
        props.setProperty("offset.storage.file.filename", "/path/to/storage/offsets.dat");
        props.setProperty("offset.flush.interval.ms", "60000");
        /* begin connector properties */
        props.setProperty("database.hostname", "localhost");
        props.setProperty("database.port", "3306");
        props.setProperty("database.user", "mysqluser");
        props.setProperty("database.password", "mysqlpw");
        props.setProperty("database.server.id", "85744");
        props.setProperty("topic.prefix", "my-app-connector");
        props.setProperty("schema.history.internal", "io.debezium.storage.file.history.FileSchemaHistory");
        props.setProperty("schema.history.internal.file.filename", "/path/to/storage/schemahistory.dat");

        return props;
    }
}
