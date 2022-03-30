package com.suning.zhongtai.wf.core.step.node;

import com.suning.zhongtai.wf.core.step.executable.IWorkflowNodeExecutable;

/**
 * 基础节点抽象类
 */
public abstract class BndWorkflowNode extends BaseWorkflowNode {

    private static final long serialVersionUID = -6875214895595531142L;

    /**
     * 流程节点执行接口的实现类,对应的是具体的节点类型,我们平时写的节点就是通过这个对象来填充的
     */
    protected IWorkflowNodeExecutable workflowNodeExecutable;

    public IWorkflowNodeExecutable getWorkflowNodeExecutable() {
        return workflowNodeExecutable;
    }

    public void setWorkflowNodeExecutable(IWorkflowNodeExecutable workflowNodeExecutable) {
        this.workflowNodeExecutable = workflowNodeExecutable;
    }

}
