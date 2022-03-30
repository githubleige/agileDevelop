package com.suning.zhongtai.wf.dao.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 
 * 流程类型数据库实体
 *
 * @author 13073189
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Entity(name = "WF_TYPE")
public class WfType {
    private Long id;
    // 流程类型编码
    private String wfTypeCode;
    // 流程类型名称
    private String wfTypeName;
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

    @Column(name = "wf_type_code")
    public String getWfTypeCode() {
        return wfTypeCode;
    }

    public void setWfTypeCode(String wfTypeCode) {
        this.wfTypeCode = wfTypeCode;
    }

    @Column(name = "wf_type_tame")
    public String getWfTypeName() {
        return wfTypeName;
    }

    public void setWfTypeName(String wfTypeName) {
        this.wfTypeName = wfTypeName;
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
