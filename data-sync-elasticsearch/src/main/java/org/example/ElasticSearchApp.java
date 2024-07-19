package org.example;

//import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;

//@Slf4j
public class ElasticSearchApp {


    public static void main(String[] args) {
        PooledRestHighLevelClient pooledClient = new PooledRestHighLevelClient(
                new String[]{"172.28.44.0:9200",
                "172.28.47.41:9200","172.28.43.19:9200"});
        RestHighLevelClient client = pooledClient.getRestHighLevelClient();
        GetIndexRequest request = new GetIndexRequest("n_member_3");
        try {
            boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);

            if (exists) {
                System.out.println("Index exists.");
            } else {
                System.out.println("Index does not exist.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                pooledClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
