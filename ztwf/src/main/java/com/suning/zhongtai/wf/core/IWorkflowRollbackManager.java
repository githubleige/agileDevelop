package com.suning.zhongtai.wf.core;

import com.suning.zhongtai.wf.core.context.IWorkflowDataContext;
import com.suning.zhongtai.wf.core.step.WorkflowStep;

/**
 * 流程回滚管理接口<br/>
 * @author: 13073189
 * @date: 2018/12/03 14:15
 */
public interface IWorkflowRollbackManager {

    /**
     * 回滚
     * @param pdc 流程上下文接口
     */
    int rollback(IWorkflowDataContext pdc) ;

    /**
     * 添加待回滚步骤
     * @param workflowStep 待回滚步骤
     */
    void addStep(WorkflowStep workflowStep);

}
