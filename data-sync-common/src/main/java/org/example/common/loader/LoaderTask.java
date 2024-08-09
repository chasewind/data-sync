package org.example.common.loader;

import lombok.extern.slf4j.Slf4j;
import org.example.common.BaseDb;
import org.example.common.DbStarter;
import org.example.common.model.SchemaSyncTable;
import org.example.common.reader.MySqlReader;
import org.example.common.reader.ReaderSegment;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class LoaderTask implements Callable<String> {
    private CountDownLatch latch;
    private ReaderSegment parentSegment;
    private DbStarter dbStarter;
    public LoaderTask(DbStarter dbStarter,CountDownLatch latch, ReaderSegment parentSegment) {
        this.latch=latch;
        this.parentSegment=parentSegment;
        this.dbStarter=dbStarter;
    }

    @Override
    public String call() throws Exception {

        SchemaSyncTable schemaSyncTable = parentSegment.getSchemaSyncTable();
        BaseDb bizDb =  MySqlReader.getBaseDb(dbStarter,schemaSyncTable);
        //加载当前分段数据
        List<Map<String, Object>>parentTableData=MySqlReader.readAndConvert(bizDb,parentSegment);

        log.info("table:{},size:{}",parentSegment.getRealTableName(),parentTableData.size());
        //加载子节点对应数据

        return null;
    }
}
