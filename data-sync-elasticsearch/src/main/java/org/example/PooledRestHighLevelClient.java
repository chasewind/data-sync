package org.example;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

public class PooledRestHighLevelClient {

    private final RestHighLevelClient restHighLevelClient;

    public PooledRestHighLevelClient(String[] hosts) {
        HttpHost[] httpHosts = new HttpHost[hosts.length];
        for (int i = 0; i < hosts.length; i++) {
            httpHosts[i] = HttpHost.create(hosts[i]);
        }

        RestClientBuilder restClientBuilder =
                RestClient.builder(httpHosts).setHttpClientConfigCallback(httpAsyncClientBuilder -> {
                    httpAsyncClientBuilder.disableAuthCaching()
                            .setMaxConnTotal(20)
                            .setMaxConnPerRoute(10);
                    return httpAsyncClientBuilder;
                }
        );
//        RestClientBuilder restClientBuilder =RestClient.builder(httpHosts);
        restHighLevelClient = new RestHighLevelClient(restClientBuilder);
    }

    public RestHighLevelClient getRestHighLevelClient() {
        return restHighLevelClient;
    }

    public void close() throws Exception {
        restHighLevelClient.close();
    }

}