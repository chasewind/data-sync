package org.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.ActiveShardCount;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class ElasticSearchApp {
    private static PooledRestHighLevelClient pooledClient;

    public static synchronized RestHighLevelClient getPooledRestHighLevelClient() {
        if (pooledClient == null) {
            pooledClient = new PooledRestHighLevelClient(
                    new String[]{"127.0.0.1:9200",
                            "127.0.0.1:9201", "127.0.0.1:9202"});

        }
        return pooledClient.getRestHighLevelClient();
    }

    public static boolean indexExists(String indexName) {
        RestHighLevelClient client = getPooledRestHighLevelClient();
        GetIndexRequest request = new GetIndexRequest(indexName);
        try {
            return client.indices().exists(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("fail", e);
            return false;
        } finally {
            try {
                pooledClient.close();
            } catch (Exception e) {
                log.error("ignore", e);
            }
        }
    }

    public static void createIndex(String indexName, String indexAliasName, Map<String, Object> mapping) {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
        createIndexRequest.settings(Settings.builder().put("index.number_of_shards", 5).put("index.number_of_replicas"
                , 1));
        createIndexRequest.mapping(mapping);
        //设置别名
        createIndexRequest.alias(new Alias(indexAliasName));

        // 额外参数
        //设置超时时间
        createIndexRequest.setTimeout(TimeValue.timeValueMinutes(2));
        //设置主节点超时时间
        createIndexRequest.setMasterTimeout(TimeValue.timeValueMinutes(1));
        //在创建索引API返回响应之前等待的活动分片副本的数量，以int形式表示
        createIndexRequest.waitForActiveShards(ActiveShardCount.DEFAULT);
        //
        RestHighLevelClient client = getPooledRestHighLevelClient();
        //操作索引的客户端
        IndicesClient indices = client.indices();
        try {
//        //执行创建索引库
            CreateIndexResponse createIndexResponse = indices.create(createIndexRequest, RequestOptions.DEFAULT);

            //得到响应（全部）
            boolean acknowledged = createIndexResponse.isAcknowledged();
            //得到响应 指示是否在超时前为索引中的每个分片启动了所需数量的碎片副本
            boolean shardsAcknowledged = createIndexResponse.isShardsAcknowledged();

            System.out.println("acknowledged:" + acknowledged);
            System.out.println("shardsAcknowledged:" + shardsAcknowledged);
        } catch (Exception e) {
            log.error("fail", e);
        } finally {
            try {
                pooledClient.close();
            } catch (Exception e) {
                log.error("ignore", e);
            }
        }
    }


    public static void createIndexAsync(String indexName, String indexAliasName, Map<String, Object> mapping) throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
        createIndexRequest.settings(Settings.builder().put("index.number_of_shards", 5).put("index.number_of_replicas"
                , 1));
        createIndexRequest.mapping(mapping);
        //设置别名
        createIndexRequest.alias(new Alias(indexAliasName));

        // 额外参数
        //设置超时时间
        createIndexRequest.setTimeout(TimeValue.timeValueMinutes(2));
        //设置主节点超时时间
        createIndexRequest.setMasterTimeout(TimeValue.timeValueMinutes(1));
        //在创建索引API返回响应之前等待的活动分片副本的数量，以int形式表示
        createIndexRequest.waitForActiveShards(ActiveShardCount.DEFAULT);
        //
        RestHighLevelClient client = getPooledRestHighLevelClient();
        try {
            ActionListener<CreateIndexResponse> listener = new ActionListener<CreateIndexResponse>() {
                @Override
                public void onResponse(CreateIndexResponse createIndexResponse) {
                    //得到响应（全部）
                    boolean acknowledged = createIndexResponse.isAcknowledged();
                    //得到响应 指示是否在超时前为索引中的每个分片启动了所需数量的碎片副本
                    boolean shardsAcknowledged = createIndexResponse.isShardsAcknowledged();

                    System.out.println("acknowledged:" + acknowledged);
                    System.out.println("shardsAcknowledged:" + shardsAcknowledged);
                }

                @Override
                public void onFailure(Exception e) {
                    log.error("fail", e);
                }
            };

            client.indices().createAsync(createIndexRequest, RequestOptions.DEFAULT, listener);
            //强行阻塞
            Thread.sleep(5000);

        } catch (Exception e) {
            log.error("fail", e);
        } finally {
            try {
                pooledClient.close();
            } catch (Exception e) {
                log.error("ignore", e);
            }
        }


    }
/**********************************基础查询*************************************************/
    public static  void existsQuery(String indexName,String field){
        SearchRequest searchRequest = new SearchRequest(indexName);
        QueryBuilder queryBuilder = QueryBuilders.existsQuery(field);
        SearchSourceBuilder searchSourceBuilder= new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        searchRequest.source(searchSourceBuilder);
        RestHighLevelClient client = getPooledRestHighLevelClient();
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            TotalHits totalHits = searchResponse.getHits().getTotalHits();
            log.info("total:{}",totalHits);
            for (SearchHit searchHit : searchResponse.getHits()) {
                log.info("data:{}", searchHit.getSourceAsString());
            }
        }catch (Exception e){
            log.error("fail",e);
        }finally {
            try {
                pooledClient.close();
            } catch (Exception e) {
                log.error("ignore", e);
            }
        }
    }
    public static  void singleTermQuery(String field,Object value){
        QueryBuilder queryBuilder = QueryBuilders.termQuery(field,value);
    }

    public static  void termsQuery(String field,Object... values){
        QueryBuilder queryBuilder = QueryBuilders.termsQuery(field,values);
    }

    public static  void singleMatchQuery(String field,Object value){
        QueryBuilder queryBuilder = QueryBuilders.matchQuery(field, value);
    }
    public static  void rangeGtQuery(String field,Object gtValue){
        QueryBuilder queryBuilder = QueryBuilders.rangeQuery(field).gt(gtValue);
    }
    public static  void rangeGteQuery(String field,Object gteValue){
        QueryBuilder queryBuilder = QueryBuilders.rangeQuery(field).gte(gteValue);
    }
}
