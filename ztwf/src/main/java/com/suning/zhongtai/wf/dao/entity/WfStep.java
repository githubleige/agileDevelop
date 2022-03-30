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
 * 流程步骤数据实体
 *
 * @author 13073189
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Entity(name = "WF_STEP")
public class WfStep implements Serializable {

    private static final long serialVersionUID = 8221119387247194058L;
    private Long id;
    // 流程编码
    private Long wfId;
    //模板编码
    private String wfCode;
    // 节点编码
    private String nodeCode;
    // 回滚标识
    private int rollbackFlag;
    // 步骤号
    private int stepNum;
    // N出口
    private int nextStepN;
    // Y出口
    private int nextStepY;
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

    @Column(name = "NODE_CODE")
    public String getNodeCode() {
        return nodeCode;
    }
    
    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    @Column(name = "ROLLBACK_FLAG")
    public int getRollbackFlag() {
        return rollbackFlag;
    }

    public void setRollbackFlag(int rollbackFlag) {
        this.rollbackFlag = rollbackFlag;
    }

    @Column(name = "STEP_NUM")
    public int getStepNum() {
        return stepNum;
    }

    public void setStepNum(int stepNum) {
        this.stepNum = stepNum;
    }

    @Column(name = "NEXT_STEP_N")
    public int getNextStepN() {
        return nextStepN;
    }
    
    public void setNextStepN(int nextStepN) {
        this.nextStepN = nextStepN;
    }
    
    @Column(name = "NEXT_STEP_Y")
    public int getNextStepY() {
        return nextStepY;
    }
    
    public void setNextStepY(int nextStepY) {
        this.nextStepY = nextStepY;
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
