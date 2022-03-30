package com.suning.zhongtai.wf.core.step.node;

import com.suning.zhongtai.wf.core.WorkflowInstance;
import com.suning.zhongtai.wf.core.context.IWorkflowDataContext;

/**
 * 流程节点接口
 * @author: 18040994
 * @date: 2018/11/29 14:15
 */
public interface IWorkflowNode {

    /**
     * 进行执行操作
     *
     * @param workflowInstance 流程实例
     * @return 0 就是N (失败或异常); 1就是Y (成功)
     */
    int execute(WorkflowInstance workflowInstance);

    /**
     * 进行回滚操作
     *
     * @param dataContext 流程数据上下文
     * @return 0 就是N (失败或异常); 1就是Y (成功)
     */
    int rollback(IWorkflowDataContext dataContext);

    /**
     * 能否进行回滚操作
     *
     * @return true 可以回滚; false 不可以回滚
     */
    boolean canRollback();

}
