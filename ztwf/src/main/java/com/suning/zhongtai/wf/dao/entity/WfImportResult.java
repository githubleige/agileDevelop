package com.suning.zhongtai.wf.dao.entity;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 流程引擎导入结果表
 * @author 18040994
 * @date 2019/11/6 11:28
 */
public class WfImportResult implements Serializable {

    private static final long serialVersionUID = 7239932878502369350L;
    private long id;
    /**
     * 导入状态: 0-预加载中 1-预加载完成  2-预加载失败 3-数据导入完成 4-数据导入失败
     */
    private int status;
    /**
     * 机器IP
     */
    private String serverIp;
    /**
     * 导入版本号
     */
    private String versionNum;
    /**
     * 导入开始时间
     */
    private Timestamp beginTime;
    /**
     * 导入完成时间
     */
    private Timestamp endTime;
    /**
     * 导入失败原因
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

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public String getFailCause() {
        return failCause;
    }

    public void setFailCause(String failCause) {
        this.failCause = failCause;
    }
}
