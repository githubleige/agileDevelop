package com.suning.zhongtai.wf.core;

import com.suning.zhongtai.wf.constant.WorkflowConstant;
import com.suning.zhongtai.wf.core.step.WorkflowStep;
import com.suning.zhongtai.wf.exception.WorkflowException;
import com.suning.zhongtai.wf.exception.code.WorkflowCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 流程模版类
 */
public class WorkflowTemplate implements Serializable {

    private static final long serialVersionUID = 3667411232382292001L;

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowTemplate.class);
    //id
    private Long id;
    //模板id
    private Long wfId;
    //流程编码
    private String wfCode;
    //流程名称
    private String wfName;
    //流程版本
    private String wfVersion;
    //流程状态
    private int wfState;
    //流程类型编码
    private String wfTypeCode;
    //流程场景编码
    private String bisSceneCode;
    //流程创建时间
    private Timestamp wfCreateTime;
    //流程更新时间
    private Timestamp wfUpdateTime;
    //流程定义描述
    private String description;
    //流程步骤集合，key=stepNum(开始步骤除外，开始步骤的key="SOP"), value=步骤号对应的步骤
    private final Map<String, WorkflowStep> workflowSteps = new ConcurrentHashMap<>();
    
    /**
     * 获取开始节点
     *
     * @return 流程节点
     */
    public WorkflowStep findStartStep() {
        WorkflowStep workflowStep = workflowSteps.get(WorkflowConstant.WORKFLOW_NODE_CODE_SOP);
        if(null == workflowStep){
            LOGGER.error("Failure to retrieve the start step from the workflow template ({})", wfCode);
            throw new WorkflowException(WorkflowCode.WF2002, "Failure to retrieve "+WorkflowConstant.WORKFLOW_NODE_CODE_SOP+" step", null);
        }
        return workflowStep;
    }

    /**
     * 获取下一个节点
     *
     * @param currentStep 当前步骤
     * @param result  当前步骤执行结果
     * @return 流程节点
     */
    public WorkflowStep findNextStep(WorkflowStep currentStep, int result) {
        String nextStepNum;
        if (WorkflowConstant.WORKFLOW_FLAG_Y == result) {
            nextStepNum = currentStep.getNextStepNumY();
        } else {
            nextStepNum = currentStep.getNextStepNumN();
        }
        WorkflowStep nextStep = workflowSteps.get(nextStepNum);
        if (null == nextStep) {
            LOGGER.error("Failure to retrieve the next step from the workflow template ({}) based on stepNum ({}) and return value ({})", wfCode, currentStep.getStepNum(), result);
            throw new WorkflowException(WorkflowCode.WF2002, "Failure to retrieve the next step, current stepNum is "+currentStep.getStepNum()+", workflow Code is "+wfCode, null);
        }
        return nextStep;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getWfId() {
        return wfId;
    }

    public void setWfId(Long wfId) {
        this.wfId = wfId;
    }

    public String getWfCode() {
        return wfCode;
    }
    
    public void setWfCode(String wfCode) {
        this.wfCode = wfCode;
    }

    public String getWfVersion() {
        return wfVersion;
    }

    public void setWfVersion(String wfVersion) {
        this.wfVersion = wfVersion;
    }

    public String getWfName() {
        return wfName;
    }

    public void setWfName(String wfName) {
        this.wfName = wfName;
    }

    public int getWfState() {
        return wfState;
    }

    public void setWfState(int wfState) {
        this.wfState = wfState;
    }

    public String getWfTypeCode() {
        return wfTypeCode;
    }

    public void setWfTypeCode(String wfTypeCode) {
        this.wfTypeCode = wfTypeCode;
    }

    public String getBisSceneCode() {
        return bisSceneCode;
    }

    public void setBisSceneCode(String bisSceneCode) {
        this.bisSceneCode = bisSceneCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public Timestamp getWfCreateTime() {
        return wfCreateTime;
    }

    public void setWfCreateTime(Timestamp wfCreateTime) {
        this.wfCreateTime = wfCreateTime;
    }

    public Timestamp getWfUpdateTime() {
        return wfUpdateTime;
    }

    public void setWfUpdateTime(Timestamp wfUpdateTime) {
        this.wfUpdateTime = wfUpdateTime;
    }

    public Map<String, WorkflowStep> getWorkflowSteps() {
        return workflowSteps;
    }

}
