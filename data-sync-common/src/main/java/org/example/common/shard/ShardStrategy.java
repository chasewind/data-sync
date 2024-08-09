package org.example.common.shard;

public interface ShardStrategy {

    String name();

    String description();

    String getShardIndex(String shardKey);


}
