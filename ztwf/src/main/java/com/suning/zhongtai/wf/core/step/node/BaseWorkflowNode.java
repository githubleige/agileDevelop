package com.suning.zhongtai.wf.core.step.node;

import java.io.Serializable;
import java.util.Date;


/**
 * 流程节点抽象类
 */
public abstract class  BaseWorkflowNode implements Serializable {

    private static final long serialVersionUID = -5196320799836952737L;
    /**
     * 节点编码
     */
    protected String nodeCode;

    //初始化时用
    //'节点类型 0 标准节点 1 组合节点'
    protected int nodeType;

    /**
     * 节点名称
     */
    protected String nodeName;

    /**
     * 节点状态 0停用 1启用
     */
    protected int nodeStatus;

    /**
     * 是否支持能回滚 0不支持 1支持
     */
    protected boolean rollbackFlag;

    /**
     * 节点描述
     */
    protected String nodeDesc;

    /**
     * 创建时间
     */
    protected Date createTime;

    /**
     * 更新时间
     */
    protected Date updateTime;

    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public int getNodeStatus() {
        return nodeStatus;
    }

    public void setNodeStatus(int nodeStatus) {
        this.nodeStatus = nodeStatus;
    }

    public boolean isRollbackFlag() {
        return rollbackFlag;
    }

    public void setRollbackFlag(boolean rollbackFlag) {
        this.rollbackFlag = rollbackFlag;
    }

    public String getNodeDesc() {
        return nodeDesc;
    }

    public void setNodeDesc(String nodeDesc) {
        this.nodeDesc = nodeDesc;
    }

    public int getNodeType() {
        return nodeType;
    }

    public void setNodeType(int nodeType) {
        this.nodeType = nodeType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
