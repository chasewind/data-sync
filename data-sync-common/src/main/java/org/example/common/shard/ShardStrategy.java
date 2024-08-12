package org.example.common.shard;

public interface ShardStrategy {

    // 定义位常量
    /**是否填充数字0*/
       int FILL_ZERO_BIT = 1 << 0; // 1
    /**是否使用hash取模 */
       int USE_HASH_BIT = 1 << 1;  // 2
    /**是否使用字符串截断 */
      int USE_CUT_BIT = 1 << 2;   // 4


    String name();

    String description();

    String getShardIndex(String shardKey);


}
