package org.example.common.shard;

import java.util.Objects;

/**
 * <pre>
 * 普通取模分片,支持两种形式
 * 1、直接按照数字进行取模运算 按照hash进行取模
 * 2、特定字段取值后，特定位置截取一段然后数字取模
 * </pre>
 */
public class NormalModShard implements ShardStrategy{

    /**
     * 分表大小
     */
    private int tableSize;

    /**
     * 是否填充数字0
     */
    private boolean fillZero;

    /**
     * 是否使用hash取模
     */
    private boolean useHash;

    /**
     * 是否使用字符串截断
     */
    private boolean useCut;

    /**
     * 表名后缀长度
     */
    private int suffixLength;
    private int start;
    private int end;

    public NormalModShard(int tableSize,boolean fillZero,int suffixLength,boolean useHash,boolean useCut,int start,
                          int end){
        this.tableSize =tableSize;
        this.fillZero=fillZero;
        this.suffixLength=suffixLength;
        this.useHash=useHash;
        this.useCut=useCut;
        this.start= start;
        this.end=end;
    }

    @Override
    public String name() {
        return "NormalModShard";
    }

    @Override
    public String description() {
        return "数字取模运算,是否使用hash(useHash) 是否补零 (fillZero)一共多长(suffixLength)对字符串截断再运算(useCut 开始位置start 结束位置end)";
    }

    @Override
    public String getShardIndex(String shardKey) {
        int baseValue;
        if(useCut){
            shardKey=shardKey.substring(start,end+1);
        }
        if(useHash){
            int hashCode = Objects.hashCode(shardKey);
            baseValue = Math.abs(hashCode) % tableSize;
        }else{
              baseValue = (int)(Long.parseLong(shardKey) % tableSize);
        }
        if(fillZero){
            return String.format("%0"+suffixLength+"d",baseValue);
        }
        return String.valueOf(baseValue);
    }
}
