package com.suning.zhongtai.wf.dao.entity;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 流程引擎生效结果表
 * @author 18040994
 * @date 2019/11/6 14:01
 */
public class WfEffectiveResult implements Serializable {

    private static final long serialVersionUID = 791506024730575841L;
    private long id;
    /**
     * 生效状态:0-生效中 1-生效完成  2-生效失败 3-数据入运行表完成 4-数据运行表失败
     */
    private int status;
    /**
     * 机器IP
     */
    private String serverIp;
    /**
     * 生效版本号
     */
    private String versionNum;
    /**
     * 生效开始时间
     */
    private Timestamp beginTime;
    /**
     * 生效结束时间
     */
    private Timestamp endTime;
    /**
     * 生效失败原因
     */
    private String failCause;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(String versionNum) {
        this.versionNum = versionNum;
    }

    public Timestamp getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Timestamp beginTime) {
        this.beginTime = beginTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(java.sql.Timestamp endTime) {
        this.endTime = endTime;
    }

    public String getFailCause() {
        return failCause;
    }

    public void setFailCause(String failCause) {
        this.failCause = failCause;
    }
}
