package com.suning.zhongtai.wf.log;

import com.suning.zhongtai.wf.constant.WorkflowConstant;
import com.suning.zhongtai.wf.core.WorkflowInstance;
import com.suning.zhongtai.wf.core.WorkflowTemplate;
import com.suning.zhongtai.wf.core.context.IWorkflowDataContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.suning.zhongtai.wf.constant.WorkflowConstant.WORKFLOW_CLM_LOG_MONITOR;

/**
 * 流程日志类
 */
@Component
public class WorkflowLog implements IWorkflowLog {

    /**
     * CLM日志
     */
    private static final Logger CLM_LOGGER = LoggerFactory.getLogger(WORKFLOW_CLM_LOG_MONITOR);

    /**
     * 云迹日志
     */
    private static final Logger CLOUDYTRACE_LOGGER = LoggerFactory.getLogger(WorkflowLog.class);

    /**
     * 记录步骤的日志
     *
     * @param instance               流程实例
     * @param workflowClmStepLogBean 步骤日志类
     */
    @Override
    public void stepLog(WorkflowInstance instance, WorkflowClmStepLogBean workflowClmStepLogBean) {
        IWorkflowDataContext workflowDataContext = instance.getWorkflowDataContext();
        WorkflowClmLogBean workflowClmLog = workflowDataContext.getWorkflowClmLog();
        List<WorkflowClmStepLogBean> stepLogs = workflowClmLog.getStepLogs();
        // 将步骤的日志添加到日志集合里(将日志往上下文对象里面塞)
        stepLogs.add(workflowClmStepLogBean);
    }

    /**
     * 记录流程实例开始的日志
     * 初始化一个WorkflowClmLogBean来承装后面产生的日志
     * @param instance 流程实例
     */
    @Override
    public void instanceBegin(WorkflowInstance instance) {
        // 流程实例开始，初始化日志对象
        IWorkflowDataContext workflowDataContext = instance.getWorkflowDataContext();
        WorkflowTemplate workflowTemplate = instance.getWorkflowTemplate();
        WorkflowClmLogBean wfClmLog = new WorkflowClmLogBean();
        long currentTime = System.currentTimeMillis();
        // 记录日志
        wfClmLog.setWfInsId(instance.getWfInstanceId());
        wfClmLog.setWfCode(workflowTemplate.getWfCode());
        wfClmLog.setWfVer(workflowTemplate.getWfVersion());
        wfClmLog.setBizId(workflowDataContext.getBisnessId());
        wfClmLog.setLogTime(currentTime);
        //new一个容器用来承装每个步骤的执行日志
        List<WorkflowClmStepLogBean> stepLogs = Collections.synchronizedList(new ArrayList<WorkflowClmStepLogBean>());
        wfClmLog.setStepLogs(stepLogs);
        // 将日志对象设置到上下文中
        workflowDataContext.setWorkflowClmLog(wfClmLog);
    }

    /**
     * 记录流程实例结束的日志
     *
     * @param instance 流程实例
     * @param errorMsg 错误信息
     */
    @Override
    public void instanceEnd(WorkflowInstance instance, String errorMsg) {

        IWorkflowDataContext workflowDataContext = instance.getWorkflowDataContext();
        WorkflowClmLogBean workflowClmLogBean = workflowDataContext.getWorkflowClmLog();
        workflowClmLogBean.setEndTime(System.currentTimeMillis());
        workflowClmLogBean.setExeTime(System.currentTimeMillis()-instance.getStartTime());
        if(StringUtils.isNotEmpty(errorMsg)){
            workflowClmLogBean.setErrMsg(errorMsg);
        }
        // 打印CLM日志，存在一个流程多个bizId的场景，为了便于CLM查询，一个bizId对应一条记录
        String bizIds = workflowClmLogBean.getBizId();
        String[] split = bizIds.split(WorkflowConstant.COMMA_SEPARATOR);
        for(int i = 0; i<split.length; i++){
            workflowClmLogBean.setBizId(split[i]);
            CLM_LOGGER.info("{}", workflowClmLogBean);
            //同时打印云迹
            CLOUDYTRACE_LOGGER.info("{}", workflowClmLogBean);
        }
    }
}
