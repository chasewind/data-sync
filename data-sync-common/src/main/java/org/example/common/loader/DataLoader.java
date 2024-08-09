package org.example.common.loader;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.common.BaseDb;
import org.example.common.DbStarter;
import org.example.common.reader.ReaderSegment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class DataLoader {
    private static RejectedExecutionHandler rejectionHandler = (r, executor) -> {
        try {
            //这里不要判断executor.isShutdown()，如果根据这个状态处理数据会导致超出队列长度的数据被抛弃,而是直接放数据,临门一脚让任务全部处理完，延后shutdown
            //一旦执行pool.shutdown()，信号就被放出来了,告诉大家即将关闭线程池
            executor.getQueue().put(r);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }


    };
    private static ExecutorService threadPool = new ThreadPoolExecutor(4, 4, 0, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1024), rejectionHandler);

    /**
     * 根据父级节点 分段加载整个索引方案对应的数据
     *
     * @param dbStarter               全局数据源
     * @param parentReaderSegments 父节点分段信息
     */
    public static void loadAllDataByParentTableSegmentInfo(DbStarter dbStarter, List<ReaderSegment> parentReaderSegments) {
        String traceId = RandomStringUtils.randomAlphabetic(12);

        CountDownLatch latch = new CountDownLatch(parentReaderSegments.size());
        List<Future<String>> futureList = new ArrayList<>();
        for (ReaderSegment segment : parentReaderSegments) {
            Future<String> future = threadPool.submit(new LoaderTask(dbStarter,latch, segment));
            futureList.add(future);
        }
        try {
            latch.await();
            log.info("------{}:summary info start------",traceId);
            for(Future<String> future:futureList){
                log.info("------{}:{}",traceId,future.get());
            }
            log.info("------{}:summary info end------",traceId);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

    }
}
