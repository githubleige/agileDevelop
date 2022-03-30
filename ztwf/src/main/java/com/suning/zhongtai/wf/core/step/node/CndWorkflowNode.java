package com.suning.zhongtai.wf.core.step.node;

import java.util.Map;
import java.util.TreeMap;

/**
 * 组合节点
 */
public class CndWorkflowNode extends BaseWorkflowNode {

    private static final long serialVersionUID = 1486628990806616654L;
    /**
     * CND执行方式，并行-1， 串行-0
     */
    protected int cndExecuteType;
   
    /**
     * 组合节点子项集合，key-子项步骤号，value-标准节点
     */
    private final Map<Integer, StandardWorkflowNode> cndNodes = new TreeMap<>();

    public int getCndExecuteType() {
        return cndExecuteType;
    }

    public void setCndExecuteType(int cndExecuteType) {
        this.cndExecuteType = cndExecuteType;
    }

    public Map<Integer, StandardWorkflowNode> getCndNodes() {
        return cndNodes;
    }

}
