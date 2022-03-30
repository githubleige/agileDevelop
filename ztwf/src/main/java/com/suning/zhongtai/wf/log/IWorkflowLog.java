package com.suning.zhongtai.wf.log;
import com.suning.zhongtai.wf.core.WorkflowInstance;

/**
 * 流程日志接口<br/>
 * @author: 18040994
 * @date: 2018/11/29 14:03
 */
public interface IWorkflowLog {

    void stepLog(WorkflowInstance instance, WorkflowClmStepLogBean workflowClmStepLogBean);

    void instanceBegin(WorkflowInstance instance);

    void instanceEnd(WorkflowInstance instance, String errorMsg);
}
