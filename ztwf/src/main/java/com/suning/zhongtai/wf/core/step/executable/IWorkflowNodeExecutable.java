package com.suning.zhongtai.wf.core.step.executable;

import com.suning.zhongtai.wf.core.context.WorkflowDataContextWrapper;

import java.io.Serializable;

/**
 * 
 * 流程节点执行接口
 *
 * @author 13073189
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface IWorkflowNodeExecutable extends Serializable {

    /**
     * 进行执行操作
     *
     * @param dataContext 流程数据上下文
     * @return 0 就是N (失败或异常); 1就是Y (成功)
     */
    int execute(WorkflowDataContextWrapper dataContext);

    /**
     * 进行回滚操作
     *
     * @param dataContext 流程数据上下文
     * @return 0 就是N (失败或异常); 1就是Y (成功)
     */
    int rollback(WorkflowDataContextWrapper dataContext);

}
