package com.suning.zhongtai.wf.core;

import com.suning.zhongtai.wf.tool.NamingThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * 流程引擎线程池
 * @author: 18040994
 * @date: 2019/5/27 19:39
 */
public class WorkflowThreadPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowThreadPool.class);

    //池1线程数
    private int pool1ThreadCount;

    //池2线程数
    private  int pool2ThreadCount;

    //池1
    private ThreadPoolExecutor pool1;
    //池2
    private ThreadPoolExecutor pool2;

   /**
   * @param poolMsCount 线程池最小线程数
   * @param poolMxCount 线程池最大线程数
    * @param poolName 线程池名称
   * @return
   * @author 18040994
   * @date  2019/5/28 16:07
   */
    public WorkflowThreadPool(int poolMsCount, int poolMxCount, String poolName){
        this.pool1ThreadCount = poolMsCount;
        this.pool2ThreadCount = poolMxCount < 0 ? poolMxCount : poolMxCount - poolMsCount;
        this.pool1 = new ThreadPoolExecutor(poolMsCount, poolMsCount,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), new NamingThreadFactory(poolName));
        this.pool2 = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), new NamingThreadFactory(poolName));
    }

    /**
     * 线程池任务提交
     * 任务优先提交到pool1,
     * 当pool1中的线程数达到coresize时，将任务提交到pool2,
     * 当pool2中的coresize数达到SCM上的配置的最大值时，新任务提交到pool1
     */
    public <T> Future<T> submit(Callable<T> task) {
        if(pool2ThreadCount < 0 || pool1.getPoolSize() < pool1ThreadCount || pool2.getPoolSize() > pool2ThreadCount){
            LOGGER.debug("pool1 current size is {}, pool2 current size is {}, task submit by pool1", pool1.getPoolSize(), pool2.getPoolSize());
            return pool1.submit(task);
        }
        else{
            LOGGER.debug("pool1 current size is {}, pool2 current size is {}, task submit by pool2", pool1.getPoolSize(), pool2.getPoolSize());
            return pool2.submit(task);
        }
    }

    public Future<?> submit(Runnable task) {
        if(pool2ThreadCount < 0 || pool1.getPoolSize() < pool1ThreadCount || pool2.getPoolSize() > pool2ThreadCount){
            return pool1.submit(task);
        }
        else{
            return pool2.submit(task);
        }
    }

    public void execute(Runnable command) {
        if(pool2ThreadCount < 0 || pool1.getPoolSize() < pool1ThreadCount || pool2.getPoolSize() > pool2ThreadCount){
            pool1.execute(command);
        }
        else{
            pool2.execute(command);
        }
    }

    /**
    *返回池中当前线程数
    */
    public int getPoolSize() {
        return pool1.getPoolSize()+pool2.getPoolSize();
    }

    /**
     * 返回主动执行任务的近似线程数
     */
    public int getActiveCount() {
        return pool1.getActiveCount()+pool2.getActiveCount();
    }

    /**
     * 返回已完成执行的近似任务总数
     */
    public long getCompletedTaskCount() {
        return pool1.getCompletedTaskCount()+pool2.getCompletedTaskCount();
    }

    /**
     * 返回曾计划执行的近似任务总数。
     */
    public long getTaskCount() {
        return pool1.getTaskCount()+pool2.getTaskCount();
    }

    /**
     * 返回此执行程序使用的任务队列
     */
    public BlockingQueue<Runnable> getQueue() {
        return pool1.getQueue();
    }

    /**
     * 返回曾经同时位于池中的最大线程数
     */
    public int getLargestPoolSize() {
        return pool1.getLargestPoolSize()+pool2.getLargestPoolSize();
    }

    /**
     * 返回曾经同时位于池中的核心线程数
     */
    public int getCorePoolSize() {
        return pool1.getCorePoolSize()+pool2.getCorePoolSize();
    }

    /**
     * 返回允许的最大线程数。
     */
    public int getMaximumPoolSize() {
        return pool1.getMaximumPoolSize()+pool2.getLargestPoolSize();
    }

    /**
     * 如果此执行程序已关闭，则返回 true。
     */
    public boolean isShutdown() {
        return pool1.isShutdown()&&pool2.isShutdown();
    }

    /**
     * 如果关闭后所有任务都已完成，则返回 true。
     */
    public boolean isTerminated() {
        return pool1.isTerminated()&&pool2.isTerminated();
    }

    /**
     * 按过去执行已提交任务的顺序发起一个有序的关闭，但是不接受新任务
     */
    public void shutdown() {
        try {
            pool1.shutdown();
        } finally {
            pool2.shutdown();
        }
    }

}
