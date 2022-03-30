package com.suning.zhongtai.wf.core;

import com.suning.zhongtai.wf.constant.WorkflowConstant;
import com.suning.zhongtai.wf.core.context.IWorkflowDataContext;
import com.suning.zhongtai.wf.core.context.WorkflowHookContext;
import com.suning.zhongtai.wf.exception.WorkflowException;
import com.suning.zhongtai.wf.exception.code.WorkflowCode;
import com.suning.zhongtai.wf.tool.WorkFlowThreadPoolHolder;
import com.suning.zhongtai.wf.worker.ROPWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 流程引擎执行构建器
 * @author: 18040994
 * @date: 2019/5/25 11:26
 */
public class WorkflowExecuteBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowExecuteBuilder.class);
    public final WorkflowInstance wfInstance;
    public  final IWorkflowDataContext workflowDataContext;

    public WorkflowExecuteBuilder(WorkflowInstance wfInstance,IWorkflowDataContext workflowDataContext){
        this.wfInstance = wfInstance;
        this.workflowDataContext = workflowDataContext;
        WorkflowHookContext hookContext = new WorkflowHookContext();
        hookContext.setDataContext(workflowDataContext);
        hookContext.setWfCode(wfInstance.getWorkflowTemplate().getWfCode());
        hookContext.setWfTypeCode(wfInstance.getWorkflowTemplate().getWfTypeCode());
        hookContext.setWfVersion(wfInstance.getWorkflowTemplate().getWfVersion());
        //设置流程实例的流程钩子执行上下文
        this.wfInstance.setHookContext(hookContext);
    }

    /**
    * 启动流程实例
     * 如果执行的结点（不管是executr还是rollback方法）抛出了异常会在这里进行捕获，并返回WFReturnValue
    * @param
    * @return
    * @author: 18040994
    * @date: 2019/5/27 14:06
    */
    public WFReturnValue startWorkflow(){
        WFReturnValue rv = new WFReturnValue();
        rv.setSuccess(false);

        try {
            //执行开始钩子（相当于加分布式锁）
            int hookRunResult = wfInstance.runHooks(WorkflowConstant.WORKFLOW_INSTANCE_START);
            if(WorkflowConstant.WORKFLOW_HOOK_RUN_FAILED == hookRunResult){
                rv.setWfErrorCode(WorkflowCode.WF1002.getValue());
                rv.setWfErrorMsg(WorkflowCode.WF1002.getDesc());
                return rv;
            }
            // 流程实例开始执行
            WorkflowCode wfCode = wfInstance.execute();

            //WF0001-ROP提前返回（功能没做）
            if (WorkflowCode.WF0001.getValue().equals(wfCode.getValue())) {
                ROPWorker ropWorker = new ROPWorker(wfInstance);
                WorkFlowThreadPoolHolder.getRopThreadPool().submit(ropWorker);
            }
            rv.setSuccess(true);
            //设置业务返回值
            rv.setWfBizResult(workflowDataContext.getReturnValue());
        } catch (WorkflowException e) {
            rv.setWfErrorCode(e.getErrorCode().getValue());
            rv.setWfErrorMsg(e.getMessage());
            rv.setException(e);
        } catch (Exception e) {
            rv.setWfErrorCode(WorkflowCode.WF2000.getValue());
            rv.setWfErrorMsg(e.getMessage());
            rv.setException(e);
        }
        //结束钩子执行确认，确保结束钩子被执行
        instanceEndHookRunConfirm(wfInstance,rv);
        return rv;
    }

    private void instanceEndHookRunConfirm(WorkflowInstance wfInstance, WFReturnValue rv) {
        try {
            if(!rv.isSuccess()&&!wfInstance.isInstanceEndHookHasRun()){
                WorkflowHookContext hookContext = wfInstance.getHookContext();
                if(null != hookContext){
                    hookContext.setEndFlag(WorkflowConstant.WORKFLOW_ABNORMAL_END);
                }
                //流程执行失败并且EOP或EOPF节点中没有执行过结束钩子
                wfInstance.runHooks(WorkflowConstant.WORKFLOW_INSTANCE_END);
            }
        } catch (Exception e) {
            //结束钩子执行异常，由于流程返回值中已经设置过错误码，此时仅打印异常错误日志
            LOGGER.error("workflow instance end hook run failed",e);
        }
    }

    /**
    * 添加实例启动钩子
     * @param hook 启动钩子
     * @param isAffect 钩子执行异常或失败是否需要中断流程，true-中断流程，默认false-不中断流程，仅记录异常日志
    * @return WorkflowExecuteBuilder
    * @author 18040994
    * @date  2019/5/27 14:06
    */
    public WorkflowExecuteBuilder addInstanceStartHook(WorkflowHook hook, boolean ... isAffect){
        wfInstance.addHook(WorkflowConstant.WORKFLOW_INSTANCE_START, isAffect.length>0 ? isAffect[0]:false,hook);
        return this;
    }

    /**
    * 添加实例主线程结束钩子
    * @param hook 启动钩子
     * @param isAffect 钩子执行异常或失败是否需要中断流程，true-中断流程，默认false-不中断流程，仅记录异常日志
    * @return WorkflowExecuteBuilder
    * @author 18040994
    * @date 2019/6/6 11:36
    */
    @Deprecated
    public WorkflowExecuteBuilder addMainThreadEndHook(WorkflowHook hook, boolean ... isAffect){
        wfInstance.addHook(WorkflowConstant.WORKFLOW_MAIN_THREAD_END, isAffect.length>0 ? isAffect[0]:false,hook);
        return this;
    }

    /**
    * 添加实例结束钩子
     * @param hook 启动钩子
     * @param isAffect 钩子执行异常是否需要中断流程，true-中断流程，默认false-不中断流程，仅记录异常日志
     * @return WorkflowExecuteBuilder
    * @author: 18040994
    * @date: 2019/5/27 14:07
    */
    public WorkflowExecuteBuilder addInstanceEndHook(WorkflowHook hook, boolean ... isAffect){
        wfInstance.addHook(WorkflowConstant.WORKFLOW_INSTANCE_END, isAffect.length>0 ? isAffect[0]:false,hook);
        return this;
    }

}
