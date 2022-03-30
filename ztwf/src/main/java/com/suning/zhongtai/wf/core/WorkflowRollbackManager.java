package com.suning.zhongtai.wf.core;

import com.suning.zhongtai.wf.constant.WorkflowConstant;
import com.suning.zhongtai.wf.core.context.IWorkflowDataContext;
import com.suning.zhongtai.wf.core.step.WorkflowStep;
import com.suning.zhongtai.wf.exception.WorkflowException;
import com.suning.zhongtai.wf.exception.code.WorkflowCode;
import com.suning.zhongtai.wf.log.WorkflowClmLogBean;
import com.suning.zhongtai.wf.tool.WorkFlowThreadPoolHolder;
import com.suning.zhongtai.wf.worker.RollbackWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 流程回滚管理实现类
 *
 * @author: 13073189
 * @date: 2018/12/03 14:15
 */
@Component
public class WorkflowRollbackManager implements IWorkflowRollbackManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowRollbackManager.class);

    //待回滚列表
    private List<WorkflowStep> rollbackList = new ArrayList<>();
    //已经执行完流程（逻辑是在EOPF结点里面执行的，如果执行的最后一个结点是EOPF,不管是否正常执行），现在开始执行回滚逻辑
    @Override
    public int rollback(IWorkflowDataContext pdc) {
        String wfInstanceId = pdc.getWfInstanceId();
        StringBuffer sb = new StringBuffer();
        try {
            WorkflowThreadPool eopfThreadPool = WorkFlowThreadPoolHolder.getEopfThreadPool();
            int size = rollbackList.size();
            CountDownLatch countDownLatch = new CountDownLatch(size);
            for (int i = 0; i < size; i++) {
                WorkflowStep ws = rollbackList.get(i);
                String stnc = ws.getStepNum() + "_" + ws.getNode().getNodeCode();
                sb.append(stnc);
                //拼接回滚步骤的字符串
                if (i < (size - 1)) {
                    sb.append(",");
                }
                RollbackWorker rollbackWorker = new RollbackWorker(ws, pdc,countDownLatch);
                eopfThreadPool.execute(rollbackWorker);
            }
            //将已提交的回滚列表记录到CLM
            WorkflowClmLogBean workflowClmLogBean = pdc.getWorkflowClmLog();
            workflowClmLogBean.setAlreadyRollbackSteps(sb.toString());
            //等待所以子任务执行完成
            countDownLatch.await();
            return WorkflowConstant.WORKFLOW_FLAG_Y;
        } catch (Exception e) {
            WorkflowException we = new WorkflowException(WorkflowCode.WF2004,"wfInstanceId："+wfInstanceId+", rollback encounters an exception", e);
            LOGGER.info("rollback encounters an exception，wfInstanceId={}, , submitted rollbackList={}", wfInstanceId, sb.toString());
            throw we;
        }
    }
    //添加回滚步骤进来
    @Override
    public void addStep(WorkflowStep workflowStep) {
        rollbackList.add(workflowStep);
    }

}
