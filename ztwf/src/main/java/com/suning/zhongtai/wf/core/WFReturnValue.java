package com.suning.zhongtai.wf.core;

import java.io.Serializable;
/**
 * WF执行返回值（流程执行的返回值）
 * @author: 18040994
 * @date: 2018/12/6 15:46
 */
public class WFReturnValue implements Serializable {

    private static final long serialVersionUID = 5560293334421936452L;

    //流程执行成功与否标识
    private boolean success;
    //流程执行错误码
    private String wfErrorCode;
    //流程错误描述
    private String wfErrorMsg;
    //流程业务返回值
    private Object wfBizResult;
    //流程异常信息
    private Exception exception;

    public WFReturnValue() {
    }

    /**
     *
     * @param success   流程实例执行成功标识，true-成功  false-失败
     * @param wfErrorCode   流程实例返回码
     * @param wfErrorMsg    流程实例错误消息
     * @param wfBizResult   流程业务返回值
     * @param exception     流程异常
     */
    public WFReturnValue(boolean success, String wfErrorCode, String wfErrorMsg, Object wfBizResult,
            Exception exception) {
        this.success = success;
        this.wfErrorCode = wfErrorCode;
        this.wfErrorMsg = wfErrorMsg;
        this.wfBizResult = wfBizResult;
        this.exception = exception;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getWfBizResult() {
        return wfBizResult;
    }

    public void setWfBizResult(Object wfBizResult) {
        this.wfBizResult = wfBizResult;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public String getWfErrorCode() {
        return wfErrorCode;
    }

    public void setWfErrorCode(String wfErrorCode) {
        this.wfErrorCode = wfErrorCode;
    }

    public String getWfErrorMsg() {
        return wfErrorMsg;
    }

    public void setWfErrorMsg(String wfErrorMsg) {
        this.wfErrorMsg = wfErrorMsg;
    }
}
