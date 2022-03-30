/*
 * Copyright (C), 2002-2016, 苏宁易购电子商务有限公司
 * FileName: EsbEntity.java
 * Author:   15073741
 * Date:     2016-8-1 下午6:05:38
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.suning.zhongtai.wf.dao.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 
 * 流程模板基本信息数据实体
 *
 * @author 13073189
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Entity(name = "WF_DEFINITION")
public class WfDefinition implements Serializable {

    private static final long serialVersionUID = -197420247684139732L;

    private Long id;
    // 流程模板id
    private Long wfId;
    // 流程编码
    private String wfCode;
    // 流程名称
    private String wfName;
    // 流程版本
    private String wfVersion;
    // 流程状态
    private int wfState;
    // 流程类型编码
    private String wfTypeCode;
    // 流程场景编码
    private String bizSceneCode;
    // 流程定义描述
    private String wfDdescription;
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
    
    @Column(name = "WF_ID")
    public Long getWfId() {
        return wfId;
    }

    public void setWfId(Long wfId) {
        this.wfId = wfId;
    }

    @Column(name = "WF_CODE")
    public String getWfCode() {
        return wfCode;
    }

    public void setWfCode(String wfCode) {
        this.wfCode = wfCode;
    }

    @Column(name = "WF_VERSION")
    public String getWfVersion() {
        return wfVersion;
    }

    public void setWfVersion(String wfVersion) {
        this.wfVersion = wfVersion;
    }

    @Column(name = "WF_NAME")
    public String getWfName() {
        return wfName;
    }

    public void setWfName(String wfName) {
        this.wfName = wfName;
    }

    @Column(name = "WF_STATE")
    public int getWfState() {
        return wfState;
    }

    public void setWfState(int wfState) {
        this.wfState = wfState;
    }

    @Column(name = "WF_TYPE_CODE")
    public String getWfTypeCode() {
        return wfTypeCode;
    }

    public void setWfTypeCode(String wfTypeCode) {
        this.wfTypeCode = wfTypeCode;
    }

    @Column(name = "BIZ_SCENE_CODE")
    public String getBizSceneCode() {
        return bizSceneCode;
    }

    public void setBizSceneCode(String bizSceneCode) {
        this.bizSceneCode = bizSceneCode;
    }

    @Column(name = "DESCRIPTION")
    public String getWfDdescription() {
        return wfDdescription;
    }

    public void setWfDdescription(String wfDdescription) {
        this.wfDdescription = wfDdescription;
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
