package com.suning.zhongtai.wf.core.context;

import com.suning.zhongtai.wf.log.WorkflowClmLogBean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 流程上下文抽象类，子类需要实现getBisnessId()方法
 * @author: 18040994
 * @date: 2019/1/9 12:02
 */
public abstract class AbstractWorkflowDataContext<T> implements IWorkflowDataContext<T>, Serializable {

    //业务对象  List<OrderDTO>
    private T bizObject;

    //业务返回值
    private Object returnValue;

    //业务错误信息
    private Object errorMsg;

    //流程实例id
    private String wfInstanceId;

    //流程日志（里面有一个 List<WorkflowClmStepLogBean>,打印日志主要就是打印这里面的额日志）
    private WorkflowClmLogBean workflowClmLog;
    //流程步骤是否回滚（Object是boolean，如果需要回滚就会是，步骤号+true。如果不需要回滚就是：步骤号+false）
    //在业务生产中没有用，主要用来测试
    private Map<String, Object> variables = new HashMap<>();

    /**
     * 获取业务错误信息
     */
    @Override
    public Object getErrorMsg() {
        return errorMsg;
    }

    /**
     * 设置业务错误信息
     * @param errorMsg 业务相关的错误信息
     */
    @Override
    public void setErrorMsg(Object errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * 获取业务返回值
     */
    @Override
    public Object getReturnValue() {
        return returnValue;
    }

    /**
     * 设置业务返回值
     * @param returnValue 业务返回值
     */
    @Override
    public void setReturValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    /**
     * 根据步骤号获取当前步骤是否需要回滚
     * @param  stepNum 步骤号，组合节点子项的步骤号为组合节点步骤-子项步骤号
     */
    @Override
    public boolean getStepRollbackFlag(String stepNum) {
        Object stepRollbackFlag = variables.get(stepNum);
        if (stepRollbackFlag == null) {
            return false;
        } else {
            return (boolean) stepRollbackFlag;
        }

    }

    /**
     * 在上下文中设置当前步骤是否需要回滚
     */
    @Override
    public void setStepRollbackFlag(String stepNum, boolean isNeedRollbackFlag) {
        variables.put(stepNum, isNeedRollbackFlag);
    }

    /**
     * 获取业务对象
     */
    @Override
    public T getBizObject() {
        return this.bizObject;
    }

    /**
     * 设置业务对象
     */
    @Override
    public void setBizObject(T t) {
        this.bizObject = t;
    }

    /**
     * 获取流程实例ID
     */
    @Override
    public String getWfInstanceId() {
        return wfInstanceId;
    }

    /**
     * 设置流程实例ID
     */
    @Override
    public void setWfInstanceId(String wfInstanceId) {
        this.wfInstanceId = wfInstanceId;
    }

    /**
     * 获取流程日志
     */
    @Override
    public WorkflowClmLogBean getWorkflowClmLog() {
        return this.workflowClmLog;
    }

    /**
     * 设置流程日志
     */
    @Override
    public void setWorkflowClmLog(WorkflowClmLogBean workflowClmLogBean) {
        this.workflowClmLog = workflowClmLogBean;
    }

}
