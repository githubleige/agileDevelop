package com.suning.zhongtai.wf.tool;

import com.suning.zhongtai.wf.constant.WorkflowConstant;
import com.suning.zhongtai.wf.core.WorkflowInstance;
import com.suning.zhongtai.wf.core.WorkflowTemplate;
import com.suning.zhongtai.wf.core.context.IWorkflowDataContext;
import com.suning.zhongtai.wf.log.IWorkflowLog;
import com.suning.zhongtai.wf.log.WorkflowClmStepLogBean;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * 拼接日志工具类
 */
public class WorkflowLogUtil {

    /**
     * 流程步骤正常信息模版
     */
    private static final String WORKFLOW_STEP_INFO_LOG = "%s, wfInstanceId: %s, bizId: %s, wfCode: %s, wfVersion: %s, stepNum: %s, nodeCode: %s";
    /**
     * 流程步骤异常信息模版
     */
    private static final String WORKFLOW_STEP_ERROR_LOG = "%s, wfInstanceId: %s, bizId: %s, wfCode: %s, wfVersion: %s, stepNum: %s, nodeCode: %s, errorInfo: %s";
    /**
     * 流程实例正常信息模版
     */
    private static final String WORKFLOW_INSTANCE_INFO_LOG = "%s, wfInstanceId: %s, bizId: %s, wfCode: %s, wfVersion: %s, executionTime: %d";
    /**
     * 流程实例异常信息模版
     */
    private static final String WORKFLOW_INSTANCE_ERROR_LOG = "%s, wfInstanceId: %s, bizId: %s, wfCode: %s, wfVersion: %s, executionTime: %dms, errorInfo: %s";

    private WorkflowLogUtil() {
    }

    /**
     * 拼接步骤正常日志
     *
     * @param msg              信息
     * @param workflowInstance 流程实例
     * @param stepNum          步骤号
     * @param nodeCode         节点编码
     * @return 日志
     */
    public static String concatStepInfoMsg(String msg, WorkflowInstance workflowInstance, String stepNum, String nodeCode) {
        IWorkflowDataContext workflowDataContext = workflowInstance.getWorkflowDataContext();
        WorkflowTemplate workflowTemplate = workflowInstance.getWorkflowTemplate();
        return String.format(WORKFLOW_STEP_INFO_LOG, msg, workflowInstance.getWfInstanceId(), workflowDataContext.getBisnessId(), workflowTemplate.getWfCode(),
                workflowTemplate.getWfVersion(), stepNum, nodeCode);
    }

    /**
     * 拼接步骤异常日志
     *
     * @param msg              信息
     * @param workflowInstance 流程实例
     * @param stepNum          步骤号
     * @param nodeCode         节点编码
     * @param e                异常
     * @return 异常日志
     */
    public static String concatStepErrorMsg(String msg, WorkflowInstance workflowInstance, String stepNum, String nodeCode, Exception e) {
        IWorkflowDataContext workflowDataContext = workflowInstance.getWorkflowDataContext();
        WorkflowTemplate workflowTemplate = workflowInstance.getWorkflowTemplate();
        return String.format(WORKFLOW_STEP_ERROR_LOG, msg, workflowInstance.getWfInstanceId(), workflowDataContext.getBisnessId(), workflowTemplate.getWfCode(),
                workflowTemplate.getWfVersion(), stepNum, nodeCode, ExceptionUtils.getStackTrace(e));
    }

    /**
     * 拼接流程正常日志
     *
     * @param msg              信息
     * @param workflowInstance 流程实例
     * @param exeTime          执行耗时
     * @return 日志
     */
    public static String concatInstanceInfoMsg(String msg, WorkflowInstance workflowInstance, Long exeTime) {
        IWorkflowDataContext workflowDataContext = workflowInstance.getWorkflowDataContext();
        WorkflowTemplate workflowTemplate = workflowInstance.getWorkflowTemplate();
        return String.format(WORKFLOW_INSTANCE_INFO_LOG, msg, workflowInstance.getWfInstanceId(), workflowDataContext.getBisnessId(), workflowTemplate.getWfCode(),
                workflowTemplate.getWfVersion(), exeTime);
    }

    /**
     * 拼接流程异常日志
     *
     * @param msg              信息
     * @param workflowInstance 流程实例
     * @param exeTime          执行耗时
     * @param e                异常
     * @return 异常日志
     */
    public static String concatInstanceErrorMsg(String msg, WorkflowInstance workflowInstance, long exeTime, Exception e) {
        IWorkflowDataContext workflowDataContext = workflowInstance.getWorkflowDataContext();
        WorkflowTemplate workflowTemplate = workflowInstance.getWorkflowTemplate();
        return String.format(WORKFLOW_INSTANCE_ERROR_LOG, msg, workflowInstance.getWfInstanceId(), workflowDataContext.getBisnessId(), workflowTemplate.getWfCode(),
                workflowTemplate.getWfVersion(), exeTime, ExceptionUtils.getStackTrace(e));
    }

    /**
     * 初始化步骤日志对象
     *
     * @param stepNum  步骤号
     * @param nodeCode 节点编码
     * @return 步骤日志对象
     */
    public static WorkflowClmStepLogBean stepLogBegin(String stepNum, String nodeCode) {

        WorkflowClmStepLogBean workflowClmStepLogBean = new WorkflowClmStepLogBean();
        workflowClmStepLogBean.setStepNum(stepNum);
        workflowClmStepLogBean.setNodeCode(nodeCode);
        return workflowClmStepLogBean;
    }

    /**
     * 记录步骤正常结束信息
     *
     * @param workflowInstance       流程实例
     * @param workflowClmStepLogBean 步骤日志对象
     * @param result                 执行结果
     * @param exeTime                执行耗时
     */
    public static void stepInfoLogEnd(WorkflowInstance workflowInstance, WorkflowClmStepLogBean workflowClmStepLogBean, int result, long exeTime) {
        IWorkflowLog workflowLog = workflowInstance.getWorkflowLog();
        workflowClmStepLogBean.setResult(Integer.toString(result));
        workflowClmStepLogBean.setExeTime(exeTime);
        // 将步骤日志放入流程日志中
        workflowLog.stepLog(workflowInstance, workflowClmStepLogBean);
    }

    /**
     * 记录步骤异常结束信息
     *
     * @param workflowInstance       流程实例
     * @param workflowClmStepLogBean 步骤日志对象
     * @param exeTime                执行耗时
     * @param errMsg                 异常信息
     */
    public static void stepErrorLogEnd(WorkflowInstance workflowInstance, WorkflowClmStepLogBean workflowClmStepLogBean, long exeTime,
            String errMsg) {
        IWorkflowLog workflowLog = workflowInstance.getWorkflowLog();
        workflowClmStepLogBean.setResult(Integer.toString(WorkflowConstant.WORKFLOW_FLAG_N));
        workflowClmStepLogBean.setExeTime(exeTime);
        workflowClmStepLogBean.setErrMsg(errMsg);
        // 将步骤日志放入流程日志中
        workflowLog.stepLog(workflowInstance, workflowClmStepLogBean);
    }
}
