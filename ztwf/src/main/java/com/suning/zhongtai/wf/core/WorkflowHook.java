package com.suning.zhongtai.wf.core;

import com.suning.zhongtai.wf.core.context.WorkflowHookContext;

/**
 * 流程引擎钩子接口
 * @author: 18040994
 * @date: 2019/5/27 13:59
 */
public interface WorkflowHook {
    /**
     * @param  hookContext 钩子运行上下文
     * @return 1-钩子执行成功   0-钩子执行失败
     *  通过钩子运行上下可以获取流程编码、流程程版本号、流程类型编码、流程运行上下文
     */
    int execute(WorkflowHookContext hookContext);
}
