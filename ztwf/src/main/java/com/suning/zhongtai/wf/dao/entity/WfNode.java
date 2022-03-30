package com.suning.zhongtai.wf.dao.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 
 * 流程节点数据实体
 *
 * @author 13073189
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Entity(name = "WF_NODE")
public class WfNode {
    private Long id;
    // 节点编码
    private String nodeCode;
    // 节点名称
    private String nodeName;
    // 节点类型
    //'节点类型 0 标准节点 1 组合节点'
    private int nodeType;
    // 节点状态
    //'节点状态 0 未启用 1 启用',
    private int nodeState;
    // 回滚标识
    private int rollbackFlag;
    // cnd节点组合调用方式
    private int cndCallType;
    // 节点描述
    private String nodeDescription;
    // 创建时间
    private Timestamp createdTime;
    // 更新时间
    private Timestamp lastUpdTime;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ID")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "NODE_STATE")
    public int getNodeState() {
        return nodeState;
    }

    public void setNodeState(int nodeState) {
        this.nodeState = nodeState;
    }

    @Column(name = "ROLLBACK_FLAG")
    public int getRollbackFlag() {
        return rollbackFlag;
    }

    public void setRollbackFlag(int rollbackFlag) {
        this.rollbackFlag = rollbackFlag;
    }

    @Column(name = "CND_CALL_TYPE")
    public int getCndCallType() {
        return cndCallType;
    }

    public void setCndCallType(int cndCallType) {
        this.cndCallType = cndCallType;
    }

    @Column(name = "NODE_CODE")
    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    @Column(name = "NODE_NAME")
    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    @Column(name = "NODE_TYPE")
    public int getNodeType() {
        return nodeType;
    }

    public void setNodeType(int nodeType) {
        this.nodeType = nodeType;
    }

    @Column(name = "NODE_DESCRIPTION")
    public String getNodeDescription() {
        return nodeDescription;
    }

    public void setNodeDescription(String nodeDescription) {
        this.nodeDescription = nodeDescription;
    }

    @Column(name = "CREATED_TIME", insertable = false, length = 26)
    public Timestamp getCreatedTime() {
        return createdTime;
    }
    
    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }
    
    @Column(name = "LAST_UPD_TIME", insertable = false, length = 26)
    public Timestamp getLastUpdTime() {
        return lastUpdTime;
    }

    public void setLastUpdTime(Timestamp lastUpdTime) {
        this.lastUpdTime = lastUpdTime;
    }
}
