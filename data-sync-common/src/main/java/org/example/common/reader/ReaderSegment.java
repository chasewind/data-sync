package org.example.common.reader;

import lombok.Data;
import org.example.common.model.SearchTable;

import java.io.Serializable;

/**
 * 数据表在内存非常大的条件下可以流式全部读取的，内存有限的时候，需要做简单的分页
 */
@Data
public class ReaderSegment implements Serializable {
    /**需要同步的表，原始数据*/
    private SearchTable schemaSyncTable;
    /**如果做了分表，这里要拼接表名*/
    private String realTableName;
    /**分页起始位置id*/
    private Long startPkPos;
    /**分页结束位置id*/
    private Long endPkPos;
    /**当前分页数据真实大小，endPkPos-startPkPos*/
    private Integer dataSize;

}
