package com.suning.zhongtai.wf.tool;

import com.suning.zhongtai.wf.cfg.WFConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

/**
 * 流程引擎线程池统一管理类<br/>
 * @author: 18040994
 * @date: 2018/12/10 16:03
 */
public class ThreadPoolHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolHolder.class);
    
    private static Map<String, ExecutorService> poolMap = new ConcurrentHashMap<>();
    private static String eopfPool = "eopfPool";
    private static String ropPool = "ropPool";
    private static String cndPool = "cndPool";
    
    // 线程池监控定时器
    protected static Timer timer;
    
    // 线程池监控间隔时间，单位ms
    private final static int TIMER_DELAY = 60000;

    private ThreadPoolHolder() {
        throw new IllegalAccessError("Utility class");
    }

    /**
    * 获取回滚线程池
    *
    * @author: 18040994
    * @date: 2018/12/10 16:19
    */
    public static ExecutorService getEopfThreadPool() {
        if(poolMap.isEmpty()) {
            initCachePool();
        }
        return poolMap.get(eopfPool);
    }

    /**
    * 获取提前返回异步处理线程池
     *
    * @author: 18040994
    * @date: 2018/12/10 16:19
    */
    public static ExecutorService getRopThreadPool() {
        if(poolMap.isEmpty()) {
            initCachePool();
        }
        return poolMap.get(ropPool);
    }

    /**
    *  获取组合节点并行处理线程池
    *
    * @author: 18040994
    * @date: 2018/12/10 16:20
    */
    public static ExecutorService getCndThreadPool() {
        if(poolMap.isEmpty()) {
            initCachePool();
        }
        return poolMap.get(cndPool);
    }

    /**
     * 初始化线程池
     */
    public static synchronized void initCachePool() {
        if(!poolMap.isEmpty()) {
            return;
        }
        WFConfigService wfConfigService = WFApplicationContextUtil.getBean(WFConfigService.class);
        int eopfAsynThreadCount = wfConfigService.getConfig().getEopfAsynThreadCount();
        ExecutorService eopfThreadPool = Executors.newFixedThreadPool(eopfAsynThreadCount);
        int cndAsynThreadCount = wfConfigService.getConfig().getCndAsynThreadCount();
        ExecutorService cndThreadPool = Executors.newFixedThreadPool(cndAsynThreadCount);
        int ropAsynThreadCount = wfConfigService.getConfig().getRopAsynThreadCount();
        ExecutorService ropThreadPool = Executors.newFixedThreadPool(ropAsynThreadCount);
        poolMap.put(eopfPool, eopfThreadPool);
        poolMap.put(cndPool, cndThreadPool);
        poolMap.put(ropPool, ropThreadPool);
        
        timer = new Timer("Workflow pool check timer",true);
        timer.schedule(new TimerTask() {
            public void run() {
                logPoolInfo(cndPool, (ThreadPoolExecutor)getCndThreadPool());
                logPoolInfo(ropPool, (ThreadPoolExecutor)getRopThreadPool());
                logPoolInfo(eopfPool, (ThreadPoolExecutor)getEopfThreadPool());
            }

            private void logPoolInfo(String poolName, ThreadPoolExecutor threadPool) {
                if (null == threadPool) {
                    return;
                }
                LOGGER.info(String.format(poolName
                        + "-monitor: PoolSize: %d, CorePoolSize: %d, Active: %d, Completed: %d, Task: %d, Queue: %d, LargestPoolSize: %d, MaximumPoolSize: %d,KeepAliveTime: %d, isShutdown: %s, isTerminated: %s",
                        threadPool.getPoolSize(), threadPool.getCorePoolSize(), threadPool.getActiveCount(),
                        threadPool.getCompletedTaskCount(), threadPool.getTaskCount(), threadPool.getQueue().size(),
                        threadPool.getLargestPoolSize(), threadPool.getMaximumPoolSize(),
                        threadPool.getKeepAliveTime(TimeUnit.MILLISECONDS), threadPool.isShutdown(),
                        threadPool.isTerminated()));
            }
        }, TIMER_DELAY, TIMER_DELAY);
    }

    /**
     * 修改回滚异步处理线程数
     * @param eopfAsynThreadCount
     */
    public static synchronized void changeEopfAsynThreadCount(int eopfAsynThreadCount) {
        ExecutorService cachePoolNew = Executors.newFixedThreadPool(eopfAsynThreadCount);
        ExecutorService cachePoolOld =  poolMap.get(eopfPool);
        poolMap.put(eopfPool, cachePoolNew);
        if(cachePoolOld!=null) {
            cachePoolOld.shutdown();
        }
    }

    /**
     * 修改组合节点并行异步处理线程数
     * @param cndAsynThreadCount
     */
    public static synchronized void changeCndAsynThreadCount(int cndAsynThreadCount) {
        ExecutorService cachePoolNew = Executors.newFixedThreadPool(cndAsynThreadCount);
        ExecutorService cachePoolOld =  poolMap.get(cndPool);
        poolMap.put(cndPool, cachePoolNew);
        if(cachePoolOld!=null) {
            cachePoolOld.shutdown();
        }
    }

    /**
     * 修改提前返回异步处理线程数
     * @param ropAsynThreadCount
     */
    public static synchronized void changeRopAsynThreadCount(int ropAsynThreadCount) {
        ExecutorService cachePoolNew = Executors.newFixedThreadPool(ropAsynThreadCount);
        ExecutorService cachePoolOld =  poolMap.get(ropPool);
        poolMap.put(ropPool, cachePoolNew);
        if(cachePoolOld!=null) {
            cachePoolOld.shutdown();
        }
    }
}
