package com.suning.zhongtai.wf.dao.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 
 * 组合节点子项数据实体
 *
 * @author 13073189
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Entity(name = "WF_CND_ITEM")
public class WfCndNodeItem {
    private Long id;
    // cnd节点编码
    private String cndCode;
    // 基础节点编码
    private String bndCode;
    // 基础节点步骤号
    private int stepNum;
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

    @Column(name = "CND_CODE")
    public String getCndCode() {
        return cndCode;
    }

    public void setCndCode(String cndCode) {
        this.cndCode = cndCode;
    }

    @Column(name = "BND_CODE")
    public String getBndCode() {
        return bndCode;
    }

    public void setBndCode(String bndCode) {
        this.bndCode = bndCode;
    }

    @Column(name = "STEP_NUM")
    public int getStepNum() {
        return stepNum;
    }

    public void setStepNum(int stepNum) {
        this.stepNum = stepNum;
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
