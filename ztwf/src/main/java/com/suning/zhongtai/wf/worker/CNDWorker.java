package com.suning.zhongtai.wf.worker;

import com.suning.zhongtai.wf.constant.WorkflowConstant;
import com.suning.zhongtai.wf.core.WorkflowInstance;
import com.suning.zhongtai.wf.core.context.WorkflowDataContextWrapper;
import com.suning.zhongtai.wf.core.step.executable.IWorkflowNodeExecutable;
import com.suning.zhongtai.wf.core.step.node.StandardWorkflowNode;
import com.suning.zhongtai.wf.log.WorkflowClmStepLogBean;
import com.suning.zhongtai.wf.tool.WorkflowLogUtil;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * CND并行处理工作线程
 */
public class CNDWorker implements Callable<Integer> {

    /**
     * 流程实例
     */
    private WorkflowInstance workflowInstance;

    /**
     * 子步骤号
     */
    private String stepNum;

    /**
     * 组合节点中的标准节点
     */
    private StandardWorkflowNode standardWorkflowNode;

    /**
     * 用于多个子同步任务之间的结果汇聚等待
     */
    private CountDownLatch countDownLatch;

    public CNDWorker(WorkflowInstance workflowInstance, String stepNum, StandardWorkflowNode standardWorkflowNode,
            CountDownLatch countDownLatch) {
        this.workflowInstance = workflowInstance;
        this.stepNum = stepNum;
        this.standardWorkflowNode = standardWorkflowNode;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public Integer call() {
        Integer result;
        // 记录开始时间
        long startTime = System.currentTimeMillis();
        String nodeCode = standardWorkflowNode.getNodeCode();
        // 初始化步骤日志
        WorkflowClmStepLogBean workflowClmStepLogBean = WorkflowLogUtil.stepLogBegin(stepNum, nodeCode);
        // 执行子步骤
        IWorkflowNodeExecutable workflowNodeExecutable = standardWorkflowNode.getWorkflowNodeExecutable();
        WorkflowDataContextWrapper workflowDataContextWrapper = new WorkflowDataContextWrapper(stepNum,nodeCode,
                workflowInstance.getWorkflowDataContext());
        try {
            result = workflowNodeExecutable.execute(workflowDataContextWrapper);
        } catch (Exception e) {
            // 拼接错误信息
            String errorMsg = WorkflowLogUtil.concatStepErrorMsg("CND item execution (parl) encountered exception: ", workflowInstance, stepNum, nodeCode, e);
            // 记录日志
            WorkflowLogUtil
                    .stepErrorLogEnd(workflowInstance, workflowClmStepLogBean,  (System.currentTimeMillis() - startTime), errorMsg);
            // 业务异常直接抛出去
            throw e;
        } finally {
            countDownLatch.countDown();
        }
        // 记录日志
        WorkflowLogUtil.stepInfoLogEnd(workflowInstance, workflowClmStepLogBean, result,System.currentTimeMillis() - startTime);
        return result;
    }

}
