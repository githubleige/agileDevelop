package com.suning.zhongtai.wf.core.context;

import com.suning.zhongtai.wf.constant.WorkflowConstant;

/**
*流程引擎钩子执行上下文
* @author 18040994
* @date 2019/7/19 17:48
*/
public class WorkflowHookContext {
    //流程数据上下文
    private IWorkflowDataContext dataContext;
    //流程编码
    private String wfCode;
    //流程版本
    private String wfVersion;
    //流程类型编码
    private String wfTypeCode;
    //流程结束标识  1-正常结束    0-异常结束    -1-执行中
    //
    private int endFlag = WorkflowConstant.WORKFLOW_RUNING;

    public IWorkflowDataContext getDataContext() {
        return dataContext;
    }

    public void setDataContext(IWorkflowDataContext dataContext) {
        this.dataContext = dataContext;
    }

    public String getWfCode() {
        return wfCode;
    }

    public void setWfCode(String wfCode) {
        this.wfCode = wfCode;
    }

    public String getWfVersion() {
        return wfVersion;
    }

    public void setWfVersion(String wfVersion) {
        this.wfVersion = wfVersion;
    }

    public String getWfTypeCode() {
        return wfTypeCode;
    }

    public void setWfTypeCode(String wfTypeCode) {
        this.wfTypeCode = wfTypeCode;
    }

    public int getEndFlag() {
        return endFlag;
    }

    public void setEndFlag(int endFlag) {
        this.endFlag = endFlag;
    }
}
