package org.example.common.loader;

import lombok.extern.slf4j.Slf4j;
import org.example.common.reader.ReaderSegment;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class LoaderTask implements Callable<String> {
    private CountDownLatch latch;
    private ReaderSegment parentSegment;
    public LoaderTask(CountDownLatch latch, ReaderSegment parentSegment) {
        this.latch=latch;
        this.parentSegment=parentSegment;
    }

    @Override
    public String call() throws Exception {
        //加载当前分段数据

        //加载子节点对应数据

        return null;
    }
}
