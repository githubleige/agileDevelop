package com.suning.zhongtai.wf.dao.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 
 * 流程类型与节点关系实体
 *
 * @author 13073189
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Entity(name = "WF_REL_WFTYPE_NODE")
public class WfRelTypeNode {
    private Long id;
    // 流程类型编码
    private String wfTypeCode;
    // 节点编码
    private String nodeCode;
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

    @Column(name = "WF_TYPE_CODE")
    public String getWfTypeCode() {
        return wfTypeCode;
    }

    public void setWfTypeCode(String wfTypeCode) {
        this.wfTypeCode = wfTypeCode;
    }

    @Column(name = "node_code")
    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
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
