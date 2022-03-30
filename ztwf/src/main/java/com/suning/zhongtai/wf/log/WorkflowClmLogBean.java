package com.suning.zhongtai.wf.log;

import com.alibaba.fastjson.JSON;
import com.suning.zhongtai.wf.constant.WorkflowConstant;
import com.suning.zhongtai.wf.tool.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

import static com.suning.zhongtai.wf.constant.WorkflowConstant.WORKFLOW_CLM_LOG_TYPE;

/**
 * 流程CLM日志对象
 */
public class WorkflowClmLogBean implements Serializable {

    private static final long serialVersionUID = 7062057740118833260L;

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowClmLogBean.class);

    /**
     * CLM必须的,日志类型。Clm用于区分不同业务场景下的日志。
     */
    private String logType = WORKFLOW_CLM_LOG_TYPE;
    /**
     * CLM必须的,日志/流程开始时间，业务系统打印此日志的时间。格式：yyyy-MM-dd HH:mm:ss
     */
    private String logTime;
    /**
     * 流程结束时间
     */
    private String endTime;
    /**
     * 流程实例id
     */
    private String wfInsId;
    /**
     * 流程编码
     */
    private String wfCode;
    /**
     * 流程版本
     */
    private String wfVer;
    /**
     * 业务id
     */
    private String bizId;
    /**
     * 流程执行时间
     */
    private long exeTime;
    /**
     * 异常信息
     */
    private String errMsg;
    /**
     * 步骤日志
     */
    private List<WorkflowClmStepLogBean> stepLogs;
    //已经回滚的步骤。连成的字符串
    private String alreadyRollbackSteps;

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getLogTime() {
        return logTime;
    }

    public void setLogTime(long logTime) {
        this.logTime = DateUtils.getDateTimeString(logTime);
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = DateUtils.getDateTimeString(endTime);
    }

    public String getWfInsId() {
        return wfInsId;
    }

    public void setWfInsId(String wfInsId) {
        this.wfInsId = wfInsId;
    }

    public String getWfCode() {
        return wfCode;
    }

    public void setWfCode(String wfCode) {
        this.wfCode = wfCode;
    }

    public String getWfVer() {
        return wfVer;
    }

    public void setWfVer(String wfVer) {
        this.wfVer = wfVer;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public void setBizId(List<String> bizIds) {
        StringBuilder sb = new StringBuilder();
        if(null != bizIds&&!bizIds.isEmpty()){
            for(String bizId: bizIds){
                sb.append(bizId);
                sb.append(WorkflowConstant.COMMA_SEPARATOR);
            }
            sb.deleteCharAt(sb.lastIndexOf(WorkflowConstant.COMMA_SEPARATOR));
        }else{
            LOGGER.warn("getBisnessId()返回的列表为空");
        }
        this.bizId = sb.toString();
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public List<WorkflowClmStepLogBean> getStepLogs() {
        return stepLogs;
    }

    public void setStepLogs(List<WorkflowClmStepLogBean> stepLogs) {
        this.stepLogs = stepLogs;
    }

    public String getAlreadyRollbackSteps() {
        return alreadyRollbackSteps;
    }

    public void setAlreadyRollbackSteps(String alreadyRollbackSteps) {
        this.alreadyRollbackSteps = alreadyRollbackSteps;
    }

    public long getExeTime() {
        return exeTime;
    }

    public void setExeTime(long exeTime) {
        this.exeTime = exeTime;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
