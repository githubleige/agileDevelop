package com.suning.zhongtai.wf.bootstrap;

import com.suning.zhongtai.wf.core.*;
import com.suning.zhongtai.wf.core.context.IWorkflowDataContext;
import com.suning.zhongtai.wf.exception.WorkflowException;
import com.suning.zhongtai.wf.exception.code.WorkflowCode;
import com.suning.zhongtai.wf.log.IWorkflowLog;
import com.suning.zhongtai.wf.tool.WorkFlowThreadPoolHolder;
import com.suning.zhongtai.wf.tool.WFApplicationContextUtil;
import com.suning.zhongtai.wf.worker.ROPWorker;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 流程启动类,，业务侧通过此类启动流程
 * @author: 18040994
 * @date: 2018/11/29 16:32
 */
@Component
public class WorkflowEngine {

    // 流程日志的工具类（往容器日志中放日志等）
    @Resource
    private IWorkflowLog workflowLog;
    //一个uuid生成器
    @Resource
    private  WFUidGenerator wfUidGenerator;
    
    private static WorkflowEngine workflowEngine;

    /**
     * 获取流程启动类,实现单例
     * @return WorkflowEngine
     */
    public static WorkflowEngine getWorkflowEngine(){

        if(null == workflowEngine){
            synchronized (WorkflowEngine.class){
                if (null == workflowEngine){
                    workflowEngine = WFApplicationContextUtil.getBean(WorkflowEngine.class);
                }
            }
        }
        return  workflowEngine;
    }

    /**
     * 流程启动方法
     * @param workflowCode 流程编码
     * @param workflowDataContext 流程数据上下文
     * @return WFReturnValue
     * @author: 18040994
     * @date: 2018/11/29 16:33
     */
    public static WFReturnValue startWorkflow(String workflowCode, IWorkflowDataContext workflowDataContext) {
        return getWorkflowEngine().start(workflowCode, workflowDataContext);
    }

    private WFReturnValue start(String workflowCode, IWorkflowDataContext workflowDataContext) {

        WFReturnValue rv = new WFReturnValue();
        rv.setSuccess(false);
        
        try {
            // 根据流程编码和数据上下文获取流程实例
            WorkflowInstance wfInstance = buildWorkflowInstance(workflowCode, workflowDataContext);
            
            // 流程实例开始执行
            WorkflowCode wfCode = wfInstance.execute();
            
            //WF0001-ROP提前返回
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
        return rv;
    }

    /**
     * 构建流程实例
     * @param workflowCode 流程编码
     * @param dataContext 流程数据上下文
     * @return WorkflowInstance 流程实例
     * @throw
     * @author: 18040994
     * @date: 2018/11/29 19:05
     */
    private WorkflowInstance buildWorkflowInstance(String workflowCode, IWorkflowDataContext dataContext){

        // 从模板缓存容器中取出对应的流程模板
        WorkflowTemplate workflowTemplate = WorkflowTemplateContainer.getWorkflowTemplate(workflowCode);

        //流程回滚管理对象
        WorkflowRollbackManager workflowRollbackManager = new WorkflowRollbackManager();

        //String wfInstanceId = Long.toString(wfUidGenerator.getUID());
        String wfInstanceId = wfUidGenerator.getUID();

        // 构造流程实例
        WorkflowInstance workflowInstance = new WorkflowInstance(workflowRollbackManager, workflowLog, dataContext,
                workflowTemplate, wfInstanceId);

        return workflowInstance;
    }
    //入口方法

    /**
     *
     * @param workflowCode
     * @param workflowDataContext  这是一个WorkflowInstance执行实例中所有步骤共享的对象，里面有封装dto对象：List<OrderDTO> 也就是上下文对象
     * @return
     */
    public static WorkflowExecuteBuilder prepareStart(String workflowCode, IWorkflowDataContext workflowDataContext) {

        // 根据流程编码和数据上下文获取流程实例
        WorkflowInstance wfInstance = getWorkflowEngine().buildWorkflowInstance(workflowCode, workflowDataContext);

        WorkflowExecuteBuilder WorkflowExecuteBuilder = new WorkflowExecuteBuilder(wfInstance, workflowDataContext);

        return WorkflowExecuteBuilder;
    }
}
