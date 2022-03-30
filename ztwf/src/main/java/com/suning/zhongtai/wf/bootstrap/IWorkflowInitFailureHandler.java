package com.suning.zhongtai.wf.bootstrap;

import com.suning.zhongtai.wf.exception.WorkflowException;

/**
 * 流程初始化失败处理器接口
 * @author 18040994
 * @date 2019/7/22 12:00
 */
public interface IWorkflowInitFailureHandler {

    /**
    * 处理失败
    * @param  workflowException 流程异常信息
    * @author 18040994
    * @date 2019/7/22 12:01
    */
    void processFailure(WorkflowException workflowException);
}
