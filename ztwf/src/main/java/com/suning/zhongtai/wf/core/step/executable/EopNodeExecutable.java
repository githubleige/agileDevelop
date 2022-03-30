package com.suning.zhongtai.wf.core.step.executable;

import org.springframework.stereotype.Component;

import com.suning.zhongtai.wf.annotation.WorkflowNode;
import com.suning.zhongtai.wf.core.context.WorkflowDataContextWrapper;
import com.suning.zhongtai.wf.constant.WorkflowConstant;

/**
 * 
 * 结束节点执行接口的实现
 *
 * @author 13073189
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@WorkflowNode(nodeCode = WorkflowConstant.WORKFLOW_NODE_CODE_EOP)
@Component
public class EopNodeExecutable implements IWorkflowNodeExecutable {

    private static final long serialVersionUID = 7725088477444542605L;

    @Override
    public int execute(WorkflowDataContextWrapper dataContext) {
        return WorkflowConstant.WORKFLOW_FLAG_Y;
    }

    @Override
    public int rollback(WorkflowDataContextWrapper dataContext) {
        return WorkflowConstant.WORKFLOW_FLAG_Y;
    }

}
