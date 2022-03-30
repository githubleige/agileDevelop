package com.suning.zhongtai.wf.worker;

import com.suning.zhongtai.wf.core.context.IWorkflowDataContext;
import com.suning.zhongtai.wf.core.step.WorkflowStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
* 流程回滚工作线程
 *
* @author: 18040994
* @date: 2018/12/10 20:15
*/
public class RollbackWorker implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RollbackWorker.class);

    private WorkflowStep workflowStep;

    private IWorkflowDataContext pdc;

    /**
     * 用于多个子同步任务之间的结果汇聚等待
     */
    private CountDownLatch countDownLatch;

    public RollbackWorker(WorkflowStep workflowStep,IWorkflowDataContext pdc,CountDownLatch countDownLatch){
        this.workflowStep = workflowStep;
        this.pdc = pdc;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        try {
            workflowStep.rollback(pdc);
        } catch (Exception e) {
            //记录异常日志
            String stepNum = workflowStep.getStepNum();  //组合步骤子项的步骤号应该为“组合节点的步骤号-节点子项步骤号”
            String nodeCode = workflowStep.getNode().getNodeCode();
            String bisnessId = pdc.getBisnessId().toString();
            String wfInstanceId = pdc.getWfInstanceId();
            LOGGER.error("RollbackWork encountered exception，stepNum={}，node Code={}，wfInstanceId={}, bizId={}", stepNum, nodeCode, wfInstanceId, bisnessId, e);
        }finally {
            //保持线程同步，保证主线程需要等所有线程回滚后才能往下执行
            countDownLatch.countDown();
        }
    }
}
