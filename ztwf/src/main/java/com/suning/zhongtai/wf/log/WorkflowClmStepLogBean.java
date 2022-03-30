package com.suning.zhongtai.wf.log;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * 流程CLM步骤日志对象
 */
public class WorkflowClmStepLogBean implements Serializable {

    private static final long serialVersionUID = 4378401682310763924L;
    /**
     * 步骤号
     */
    private String stepNum;
    /**
     * 执行结果
     */
    private String result;
    /**
     * 执行时间
     */
    private long exeTime;
    /**
     * 节点编码
     */
    private String nodeCode;
    /**
     * 异常信息
     */
    private String errMsg;

    public String getStepNum() {
        return stepNum;
    }

    public void setStepNum(String stepNum) {
        this.stepNum = stepNum;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public long getExeTime() {
        return exeTime;
    }

    public void setExeTime(long exeTime) {
        this.exeTime = exeTime;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
