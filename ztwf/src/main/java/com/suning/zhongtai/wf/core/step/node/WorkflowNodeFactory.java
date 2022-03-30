package com.suning.zhongtai.wf.core.step.node;

import com.suning.zhongtai.wf.constant.WorkflowConstant;

/**
 * 
 * 流程节点对象创建工厂类
 *
 * @author 13073189
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */

public final class WorkflowNodeFactory {
    /**
     *
     * @param nodeType '节点类型 0 标准节点 1 组合节点'
     * @param nodeCode 节点编码
     * @param cndCallType 组合节点执行方式 0 串行 1 并行
     * @return
     */
    public static BaseWorkflowNode produceNode(int nodeType, String nodeCode, int cndCallType) {
        BaseWorkflowNode node;
        if (WorkflowConstant.WORKFLOW_NODE_TYPE_CND == nodeType) {
            //如果是组合节点
            CndWorkflowNode cndNode = new CndWorkflowNode();
            //在这里组合结点的设置并发执行还是串行执行
            cndNode.setCndExecuteType(cndCallType);
            node = cndNode;
        } else if (WorkflowConstant.WORKFLOW_NODE_CODE_SOP.equals(nodeCode)) {
            //如果节点编码是SOP：开始节点。
            node = new SopWorkflowNode();
        } else if (WorkflowConstant.WORKFLOW_NODE_CODE_EOP.equals(nodeCode)) {
            //如果节点编码是EOP：结束节点（流程正常结束，不会有任何回滚操作）
            node = new EopWorkflowNode();
        } else if (WorkflowConstant.WORKFLOW_NODE_CODE_EOPF.equals(nodeCode)) {
            //如果节点编码是EOPF：结束节点（异常，执行到该节点开始执行回滚操作）
            node = new EopfWorkflowNode();
        } else if (WorkflowConstant.WORKFLOW_NODE_CODE_ROP.equals(nodeCode)) {
            //如果节点是提前返回节点（目前还没有做）
            node = new RopWorkflowNode();
        } else {
            //如果节点是StandardNode：标准节点（我们正常使用的节点）
            node = new StandardWorkflowNode();
        }
        
        return node;
    }
}
