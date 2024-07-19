package org.example;

import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class AppTest {
    @Test
    public void testIndexExists() {
        System.out.println(ElasticSearchApp.indexExists("order_01"));
    }

    @Test
    public void testCreateIndex() throws IOException {
        Map<String, Object> mapping = buildMappingInfo();
        ElasticSearchApp.createIndex("shop_rate", "shop_rate_alias", mapping);
    }
    @Test
    public void testCreateIndexAsync() throws IOException {
        Map<String, Object> mapping = buildMappingInfo();
        ElasticSearchApp.createIndexAsync("shop_rate", "shop_rate_alias", mapping);
    }

    private static Map<String, Object> buildMappingInfo() {
        Map<String, Object> mapping = new HashMap<>();
        Map<String, Object> type = new HashMap<>();
        type.put("type", "keyword");

        Map<String, Object> content = new HashMap<>();
        content.put("type", "text");

        Map<String, Object> score = new HashMap<>();
        score.put("type", "integer");

        Map<String, Object> rater_id = new HashMap<>();
        rater_id.put("type", "keyword");

        Map<String, Object> shop_id = new HashMap<>();
        shop_id.put("type", "keyword");

        Map<String, Object> properties = new HashMap<>();
        properties.put("type", type);
        properties.put("content", content);
        properties.put("score", score);
        properties.put("rater_id", rater_id);
        properties.put("shop_id", shop_id);
        mapping.put("properties", properties);
        return mapping;
    }
}
