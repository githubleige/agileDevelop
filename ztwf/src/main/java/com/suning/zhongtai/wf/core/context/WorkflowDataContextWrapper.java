package com.suning.zhongtai.wf.core.context;

/**
 * 实际运行时，步骤所需的流程数据上下文
 *
 * @param <T> 业务数据类型
 */
public class WorkflowDataContextWrapper<T> {

    /**
     * 步骤号
     */
    private String stepNum;

    /**
     * 节点编码
     */
    private String nodeCode;

    /**
     * 流程数据上下文（这个上下文是在一个流程结点全部公用的对象）
     */
    private IWorkflowDataContext<T> workflowDataContext;

    public WorkflowDataContextWrapper() {
    }

    /**
     * 上下文包装器
     * @param stepNum   当前步骤号，组合节点子项的步骤号为组合节点步骤-子项步骤号
     * @param nodeCode  当前步骤对应的节点编码
     * @param workflowDataContext   流程上下文
     */
    public WorkflowDataContextWrapper(String stepNum, String nodeCode, IWorkflowDataContext<T> workflowDataContext) {
        this.stepNum = stepNum;
        this.nodeCode = nodeCode;
        this.workflowDataContext = workflowDataContext;
    }

    /**
     * 设置当前步骤是否需要回滚
     * @param stepNum  步骤号，组合节点子项的步骤号为组合节点步骤-子项步骤号
     * @param rollback  回滚标识 true or false
     */
    public void setStepRollback(String stepNum, boolean rollback) {
        workflowDataContext.setStepRollbackFlag(stepNum, rollback);
    }

    /**
     * 获取当前步骤号
     */
    public String getStepNum() {
        return stepNum;
    }

    /**
     * 设置当前步骤号
     * @param stepNum  步骤号，组合节点子项的步骤号为组合节点步骤-子项步骤号
     */
    public void setStepNum(String stepNum) {
        this.stepNum = stepNum;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    /**
     * 获取流程上下文
     */
    public IWorkflowDataContext<T> getWorkflowDataContext() {
        return workflowDataContext;
    }

    /**
     * 设置流程上下文
     * @param workflowDataContext  上下文对象
     */
    public void setWorkflowDataContext(IWorkflowDataContext<T> workflowDataContext) {
        this.workflowDataContext = workflowDataContext;
    }
}
