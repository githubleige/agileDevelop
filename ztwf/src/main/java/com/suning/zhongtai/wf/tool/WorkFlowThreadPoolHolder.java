package com.suning.zhongtai.wf.tool;

import com.suning.zhongtai.wf.cfg.WFConfigService;
import com.suning.zhongtai.wf.core.WorkflowThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.*;

/**
 * 流程引擎线程池统一管理类<br/>
 * @author 18040994
 * @date 2019/5/28 15:00
 */
public class WorkFlowThreadPoolHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkFlowThreadPoolHolder.class);

    private static Map<String, WorkflowThreadPool> poolMap = new ConcurrentHashMap<>();
    private static String eopfPool = "eopfPool";
    private static String ropPool = "ropPool";
    private static String cndPool = "cndPool";

    private static final String EOPF_WORKER_NAME = "WF-EOPF-Worker";
    private static final String ROP_WORKER_NAME = "WF-ROP-Worker";
    private static final String CND_WORKER_NAME = "WF-CND-Worker";

    /**
     *线程池监控间隔时间，单位ms
     */
    private static final int INITIALDELAY = 60000;

    static {
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(new TimerTask() {
            @Override
            public void run() {
                logPoolInfo(cndPool, getCndThreadPool());
                logPoolInfo(ropPool, getRopThreadPool());
                logPoolInfo(eopfPool, getEopfThreadPool());
            }

            private void logPoolInfo(String poolName, WorkflowThreadPool threadPool) {
                if (null == threadPool) {
                    return;
                }
                LOGGER.debug(String.format(poolName
                                + "-monitor: PoolSize: %d, CorePoolSize: %d, Active: %d, Completed: %d, Task: %d, Queue: %d, LargestPoolSize: %d, MaximumPoolSize: %d, isShutdown: %s, isTerminated: %s",
                        threadPool.getPoolSize(), threadPool.getCorePoolSize(), threadPool.getActiveCount(),
                        threadPool.getCompletedTaskCount(), threadPool.getTaskCount(), threadPool.getQueue().size(),
                        threadPool.getLargestPoolSize(), threadPool.getMaximumPoolSize(), threadPool.isShutdown(),
                        threadPool.isTerminated()));
            }
        }, INITIALDELAY, INITIALDELAY,TimeUnit. MILLISECONDS);
    }

    private WorkFlowThreadPoolHolder() {
        throw new IllegalAccessError("Utility class");
    }

    /**
    * 获取回滚线程池
    */
    public static WorkflowThreadPool getEopfThreadPool() {
        Object obj = new Object();
        if(poolMap.get(eopfPool)==null) {
            synchronized (obj){
                if(poolMap.get(eopfPool)==null){
                    initEopfCachePool();
                }
            }
        }
        return poolMap.get(eopfPool);
    }

    /**
    * 获取提前返回异步处理线程池
     *
    */
    public static WorkflowThreadPool getRopThreadPool() {
        Object obj = new Object();
        if(poolMap.get(ropPool)==null) {
            synchronized (obj){
                if(poolMap.get(ropPool)==null){
                    initRopCachePool();
                }
            }
        }
        return poolMap.get(ropPool);
    }

    /**
    *  获取组合节点并行处理线程池
    *
    */
    public static WorkflowThreadPool getCndThreadPool() {
        Object obj = new Object();
        if(poolMap.get(cndPool)==null) {
            synchronized (obj){
                if(poolMap.get(cndPool)==null){
                    initCndCachePool();
                }
            }
        }
        return poolMap.get(cndPool);
    }

    /**
     * 初始化ROP线程池
     */
    private static synchronized void initRopCachePool() {
        if(poolMap.get(ropPool) !=null) {
            return;
        }
        WFConfigService wfConfigService = WFApplicationContextUtil.getBean(WFConfigService.class);
        int ropMsThreadCount = wfConfigService.getConfig().getRopAsynThreadCount();
        int ropMxThreadCount = wfConfigService.getConfig().getRopMxAsynThreadCount();
        WorkflowThreadPool ropThreadPool =  new WorkflowThreadPool(ropMsThreadCount, ropMxThreadCount,ROP_WORKER_NAME);
        poolMap.put(ropPool, ropThreadPool);
    }

    /**
     * 初始化CND线程池
     */
    private static synchronized void initCndCachePool() {
        if(poolMap.get(cndPool) !=null) {
            return;
        }
        WFConfigService wfConfigService = WFApplicationContextUtil.getBean(WFConfigService.class);
        int cndMsThreadCount = wfConfigService.getConfig().getCndAsynThreadCount();
        int cndMxThreadCount = wfConfigService.getConfig().getCndMxAsynThreadCount();
        WorkflowThreadPool cndThreadPool =  new WorkflowThreadPool(cndMsThreadCount, cndMxThreadCount,CND_WORKER_NAME);
        poolMap.put(cndPool, cndThreadPool);
    }

    /**
     * 初始化EOPF线程池
     */
    private static synchronized void initEopfCachePool() {
        if(poolMap.get(eopfPool) !=null) {
            return;
        }
        WFConfigService wfConfigService = WFApplicationContextUtil.getBean(WFConfigService.class);
        int eopfMsThreadCount = wfConfigService.getConfig().getEopfAsynThreadCount();
        int eopfMxThreadCount = wfConfigService.getConfig().getEopfMxAsynThreadCount();
        WorkflowThreadPool eopfThreadPool =  new WorkflowThreadPool(eopfMsThreadCount, eopfMxThreadCount, EOPF_WORKER_NAME);
        poolMap.put(eopfPool, eopfThreadPool);
    }

    /**
     * 修改回滚异步处理线程数
     * @param eopfMsThreadCount 最小线程数
     * @param eopfMxThreadCount 最大线程数
     */
    public static synchronized void changeEopfAsynThreadCount(int eopfMsThreadCount,int eopfMxThreadCount) {
        WorkflowThreadPool cachePoolNew = new WorkflowThreadPool(eopfMsThreadCount,eopfMxThreadCount,EOPF_WORKER_NAME);
        WorkflowThreadPool cachePoolOld =  poolMap.get(eopfPool);
        poolMap.put(eopfPool, cachePoolNew);
        if(cachePoolOld!=null) {
            cachePoolOld.shutdown();
        }
    }

    /**
     * 修改组合节点并行异步处理线程数
     *@param cndMsThreadCount 最小线程数
     * @param cndMxThreadCount 最大线程数
     */
    public static synchronized void changeCndAsynThreadCount(int cndMsThreadCount,int cndMxThreadCount) {
        WorkflowThreadPool cachePoolNew = new WorkflowThreadPool(cndMsThreadCount,cndMxThreadCount,CND_WORKER_NAME);
        WorkflowThreadPool cachePoolOld =  poolMap.get(cndPool);
        poolMap.put(cndPool, cachePoolNew);
        if(cachePoolOld!=null) {
            cachePoolOld.shutdown();
        }
    }

    /**
     * 修改提前返回异步处理线程数
     * @param ropMsThreadCount 最小线程数
     * @param ropMxThreadCount 最大线程数
     */
    public static synchronized void changeRopAsynThreadCount(int ropMsThreadCount,int ropMxThreadCount) {
        WorkflowThreadPool cachePoolNew = new WorkflowThreadPool(ropMsThreadCount,ropMxThreadCount,ROP_WORKER_NAME);
        WorkflowThreadPool cachePoolOld =  poolMap.get(ropPool);
        poolMap.put(ropPool, cachePoolNew);
        if(cachePoolOld!=null) {
            cachePoolOld.shutdown();
        }
    }
}
