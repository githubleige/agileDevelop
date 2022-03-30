package com.suning.zhongtai.wf.core;

import com.suning.zhongtai.wf.exception.code.WorkflowCode;

/**
 * 流程执行接口
 * @author: 18040994
 * @date: 2018/11/29 14:26
 */
public interface IWorkflowExecution {

    /**
     * 继续执行
     */
    void continueExecute();

    /**
     *执行
     * @return WorkflowCode 流程引擎返回码
     */
    WorkflowCode execute();
}
