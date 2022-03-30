package com.suning.zhongtai.wf.bootstrap;

import com.suning.zhongtai.wf.exception.WorkflowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 流程引擎初始化失败默认处理策略：记录异常日志，抛异常
 * @author 18040994
 * @date 2019/7/22 14:33
 */
public class DefaultInitFailureHandler implements IWorkflowInitFailureHandler {
    private static Logger LOGGER = LoggerFactory.getLogger(DefaultInitFailureHandler.class);

    @Override
    public void processFailure(WorkflowException workflowException) {
        LOGGER.error(workflowException.getMessage(), workflowException);
        throw workflowException;
    }
}
