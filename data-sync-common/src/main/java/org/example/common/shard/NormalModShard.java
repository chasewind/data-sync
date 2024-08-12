package org.example.common.shard;

import lombok.Data;

import java.util.Objects;

/**
 * <pre>
 * 普通取模分片,支持两种形式
 * 1、直接按照数字进行取模运算 按照hash进行取模
 * 2、特定字段取值后，特定位置截取一段然后数字取模
 * </pre>
 */
@Data
public class NormalModShard implements ShardStrategy{

    /**
     * 分表大小
     */
    private int tableSize;

    /**
     * 表名后缀长度
     */
    private int suffixLength;
    private int start;
    private int end;
    // 使用一个整型字段来存储状态
    private int flags;

    // 设置 fillZero
    public void setFillZero(boolean fillZero) {
        if (fillZero) {
            flags |= FILL_ZERO_BIT; // 设置第0位为1
        } else {
            flags &= ~FILL_ZERO_BIT; // 设置第0位为0
        }
    }

    // 获取 fillZero
    public boolean isFillZero() {
        return (flags & FILL_ZERO_BIT) != 0;
    }

    // 设置 useHash
    public void setUseHash(boolean useHash) {
        if (useHash) {
            flags |= USE_HASH_BIT; // 设置第1位为1
        } else {
            flags &= ~USE_HASH_BIT; // 设置第1位为0
        }
    }

    // 获取 useHash
    public boolean isUseHash() {
        return (flags & USE_HASH_BIT) != 0;
    }

    // 设置 useCut
    public void setUseCut(boolean useCut) {
        if (useCut) {
            flags |= USE_CUT_BIT; // 设置第2位为1
        } else {
            flags &= ~USE_CUT_BIT; // 设置第2位为0
        }
    }

    // 获取 useCut
    public boolean isUseCut() {
        return (flags & USE_CUT_BIT) != 0;
    }

    public NormalModShard(int tableSize,int suffixLength,int start, int end){
        this.tableSize =tableSize;
        this.suffixLength=suffixLength;
        this.start= start;
        this.end=end;
    }

    @Override
    public String name() {
        return "NormalModShard";
    }

    @Override
    public String description() {
        return "数字取模运算,是否使用hash(useHash) 是否补零 (fillZero)一共多长(suffixLength)对字符串截断再运算(useCut 起始位置[start，end])";
    }

    @Override
    public String getShardIndex(String shardKey) {
        int baseValue;
        if(isUseCut()){
            shardKey=shardKey.substring(start,end+1);
        }
        if(isUseHash()){
            int hashCode = Objects.hashCode(shardKey);
            baseValue = Math.abs(hashCode) % tableSize;
        }else{
              baseValue = (int)(Long.parseLong(shardKey) % tableSize);
        }
        if(isFillZero()){
            return String.format("%0"+suffixLength+"d",baseValue);
        }
        return String.valueOf(baseValue);
    }
}
