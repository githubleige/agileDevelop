package com.suning.zhongtai.wf.core.context;

import com.suning.zhongtai.wf.log.WorkflowClmLogBean;

import java.util.List;

/**
 * 流程数据上下文接口
 * @author: 18040994
 * @date: 2018/11/29 14:16
 */
public interface IWorkflowDataContext<T> {

    Object getErrorMsg();

    void setErrorMsg(Object errorMsg);

    Object getReturnValue();

    void setReturValue(Object returnValue);

    boolean getStepRollbackFlag(String stepNum);

    void setStepRollbackFlag(String stepNum, boolean isNeedRollbackFlag);

    String getWfInstanceId();

    void setWfInstanceId(String wfInstanceId);

    WorkflowClmLogBean getWorkflowClmLog();

    void setWorkflowClmLog(WorkflowClmLogBean workflowClmLogBean);

    void setBizObject(T t);

    T getBizObject();

    /**
     * 获取业务ID
     */
    List<String> getBisnessId();
}
