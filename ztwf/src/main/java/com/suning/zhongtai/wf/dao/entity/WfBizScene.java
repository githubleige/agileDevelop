package com.suning.zhongtai.wf.dao.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 
 * 业务场景数据实体
 *
 * @author 13073189
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Entity(name = "WF_BIZSCENE")
public class WfBizScene {
    private Long id;
    // 业务场景编码
    private String bizCode;
    // 流程类型编码
    private String wfTypeCode;
    // 场景名称
    private String bizName;
    // 场景描述
    private String bizDescription;
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

    @Column(name = "BIZ_CODE")
    public String getBizCode() {
        return bizCode;
    }

    public void setBizCode(String bizCode) {
        this.bizCode = bizCode;
    }

    @Column(name = "BIZ_NAME")
    public String getBizName() {
        return bizName;
    }

    public void setBizName(String bizName) {
        this.bizName = bizName;
    }

    @Column(name = "BIZ_DESCRIPTION")
    public String getBizDescription() {
        return bizDescription;
    }

    public void setBizDescription(String bizDescription) {
        this.bizDescription = bizDescription;
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
