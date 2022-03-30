package com.suning.zhongtai.wf.core;

import com.suning.zhongtai.wf.constant.WorkflowConstant;
import com.suning.zhongtai.wf.core.context.IWorkflowDataContext;
import com.suning.zhongtai.wf.core.context.WorkflowHookContext;
import com.suning.zhongtai.wf.core.step.WorkflowStep;
import com.suning.zhongtai.wf.core.step.node.BaseWorkflowNode;
import com.suning.zhongtai.wf.core.step.node.CndWorkflowNode;
import com.suning.zhongtai.wf.core.step.node.StandardWorkflowNode;
import com.suning.zhongtai.wf.exception.WorkflowException;
import com.suning.zhongtai.wf.exception.code.WorkflowCode;
import com.suning.zhongtai.wf.log.IWorkflowLog;
import com.suning.zhongtai.wf.tool.WorkflowLogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 流程实例
 * @author: 18040994
 * @date: 2018/11/29 15:08
 */
public class WorkflowInstance implements IWorkflowExecution {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowInstance.class);

    //流程实例id
    private String wfInstanceId;

    //流程回滚管理
    private IWorkflowRollbackManager workflowRollbackManager;

    //流程日志（工具类）
    private IWorkflowLog workflowLog;
    /**
     * 很重要，所有的数据对象都封装在这个上下文中
     */
    //流程数据上下文(被一个流程实例所有结点所公用，里面封装了List<OrderDTO>数据对象)
    private IWorkflowDataContext dataContext;

    //流程钩子执行上下文
    private WorkflowHookContext hookContext;

    //流程模板
    private WorkflowTemplate workflowTemplate;

    //ROP步骤（还没有开发）
    private WorkflowStep ropStep;

    //流程开始执行时间
    private long startTime;

    //ROP节点已执行标识
    //废弃   提前返回节点（目前还没有做）
    private boolean ropHasRun = false;

    //流程结束钩子已执行标识
    private boolean instanceEndHookHasRun = false;

    //实例运行时钩子(开始时候执行该订单分布式锁，防止重复下单，下单完成后解锁)
    private final Map<String, List<WorkflowHookWrapper>> instanceRuntimeHooks = new HashMap<>();

    public WorkflowInstance(IWorkflowRollbackManager workflowRollbackManager,
            IWorkflowLog workflowLog, IWorkflowDataContext dataContext,
            WorkflowTemplate workflowTemplate, String wfInstanceId) {
        this.workflowRollbackManager = workflowRollbackManager;
        this.workflowLog = workflowLog;
        this.dataContext = dataContext;
        this.workflowTemplate = workflowTemplate;
        this.wfInstanceId = wfInstanceId;
    }

    /**
     * 流程实例继续执行
     */
    @Override
    public void continueExecute() {
        //获取ROP节点步骤的下一个步骤
        WorkflowStep nextStep = getRopNextStep(ropStep);
        //判断是不是结束Step
        while (!isEndStep(nextStep)) {
            //如果不是结束步骤，则继续执行下个步骤
            int nextStepRes = nextStep.execute(this);
            //判断当前step是否需要回滚
            stepRollbackProcess(nextStep);
            nextStep = workflowTemplate.findNextStep(nextStep, nextStepRes);
        }
        if (isRopStep(nextStep)) {
            //如果ROP步骤后面再次出现ROP步骤，直接抛异常结束流程
            throw new WorkflowException(WorkflowCode.WF2008, "ROP node reappears after ROP node，the second ROP node stepNum is: " + nextStep.getStepNum(),
                    null);
        } else {
            //执行结束步骤
            nextStep.execute(this);
            workflowLog.instanceEnd(this, "");
        }
    }

    /**
     *  获取ROP步骤的下一个步骤
     *
     * @author: 18040994
     * @date: 2018/12/21 15:20
     */
    private WorkflowStep getRopNextStep(WorkflowStep currentStep) {

        WorkflowStep nextStep;
        try {
            nextStep = workflowTemplate.findNextStep(currentStep, WorkflowConstant.WORKFLOW_FLAG_Y);
        } catch (WorkflowException e) {
            nextStep = workflowTemplate.findNextStep(currentStep, WorkflowConstant.WORKFLOW_FLAG_N);
        }
        return nextStep;
    }

    /**
     * 流程实例执行
     */
    @Override
    public WorkflowCode execute() {
        try {
            startTime = System.currentTimeMillis();
            //将流程实例id设到上下文中
            dataContext.setWfInstanceId(wfInstanceId);
            //记录流程开始
            workflowLog.instanceBegin(this);
            //通过流程模板获取开始步骤
            WorkflowStep startStep = workflowTemplate.findStartStep();
            //执行开始步骤
            int res = startStep.execute(this);
            WorkflowStep nextStep = workflowTemplate.findNextStep(startStep, res);
            //判断下个步骤是不是结束步骤（ROP、EOP、EOPF）
            while (!isEndStep(nextStep)) {
                //如果不是结束Step，则继续执行下个步骤
                int nextStepRes = nextStep.execute(this);
                //判断当前step是否需要回滚（不需要根据返回值来判断是否需要回滚）
                stepRollbackProcess(nextStep);
                nextStep = workflowTemplate.findNextStep(nextStep, nextStepRes);
            }
            //执行结束步骤
            nextStep.execute(this);
            //构造返回值返回给流程启动类
            if (isRopStep(nextStep)) {
                ropStep = nextStep;
                return WorkflowCode.WF0001;
            } else {
                workflowLog.instanceEnd(this, "");
                return WorkflowCode.WF0000;
            }
        } catch (WorkflowException e) {
            recordErrorLog("workflow terminated with exception", e);
            throw e;
        } catch (Exception e) {
            recordErrorLog("workflow terminated with unknown exception", e);
            throw e;
        }
    }

    /**
     * @param e 异常
     * @return
     * @description: 记录流程实例异常日志
     * @throw
     * @author: 18040994
     * @date: 2018/12/23 11:46
     */
    private void recordErrorLog(String errorInfo, Exception e) {
        String errorLog = WorkflowLogUtil
                .concatInstanceErrorMsg(errorInfo, this, System.currentTimeMillis() - startTime, e);
        LOGGER.error(errorLog);
        workflowLog.instanceEnd(this, e.getMessage());
    }

    /**
     * @param step 流程步骤
     * @return true or false
     * @description: 检查当前节点是否为EOP、EOPF或者ROP
     * @throw
     * @author: 18040994
     * @date: 2018/11/29 20:02
     */
    private boolean isEndStep(WorkflowStep step) {
        String nodeCode = step.getNode().getNodeCode();
        return WorkflowConstant.WORKFLOW_NODE_CODE_EOP.equals(nodeCode) || WorkflowConstant.WORKFLOW_NODE_CODE_EOPF
                .equals(nodeCode)||WorkflowConstant.WORKFLOW_NODE_CODE_ROP.equals(nodeCode);
    }

    /**
     * @param step 流程步骤
     * @return true or false
     * @description: 判断当前节点是否为ROP
     * @throw
     * @author: 18040994
     * @date: 2018/11/29 20:02
     */
    private boolean isRopStep(WorkflowStep step) {
        return WorkflowConstant.WORKFLOW_NODE_CODE_ROP.equals(step.getNode().getNodeCode());
    }

    /**
     * @param step 流程步骤
     * @return
     * @description: 流程步骤回滚处理
     * 回滚前提条件：当前步骤支持回滚+步骤对应的节点支持回滚,
     * 需要回滚的步骤放到待回滚列表
     * @throw
     * @author: 18040994
     * @date: 2018/12/7 14:24
     */
    private void stepRollbackProcess(WorkflowStep step) {
        boolean stepRollbackFlag = step.isRollbackFlag();
        BaseWorkflowNode node = step.getNode();
        boolean nodeRollbackFlag = node.isRollbackFlag();
        //step回滚标识和里面的node回滚标识是否都是true
        if (nodeRollbackFlag && stepRollbackFlag) {
            if (!step.isCnd()) {
                //标准节点
                workflowRollbackManager.addStep(step);
            } else {
                //组合节点, 以组合节点本身是否支持回滚为准
                //组合节点添加流程回滚容器，是遍历里面所有的基础节点，然后判断其回滚标识，如果支持回滚，则按照
                //基础节点添加到回滚容器中，不是则不添加。只要组合节点里面有一个基础节点回滚标识为Y,则组合节点
                //回滚标识肯定为Y
                CndWorkflowNode cndWorkflowNode = (CndWorkflowNode) node;
                Map<Integer, StandardWorkflowNode> cndChildNodes = cndWorkflowNode.getCndNodes();
                Set<Integer> keySet = cndChildNodes.keySet();
                for (Integer childStepNum : keySet) {
                    StandardWorkflowNode cndChildNode = cndChildNodes.get(childStepNum);
                    String itemStepNum = step.getStepNum() + WorkflowConstant.STEP_SEPARATOR + childStepNum;
                    boolean isChildSupportRollback = cndChildNode.isRollbackFlag();
                    if (isChildSupportRollback) {
                        WorkflowStep childStep = new WorkflowStep(itemStepNum, false, cndChildNode);
                        workflowRollbackManager.addStep(childStep);
                    }
                }
            }
        }
    }

    /**
     * 获取流程管理对象
     */
    public IWorkflowRollbackManager getWorkflowRollbackManager() {
        return this.workflowRollbackManager;
    }

    /**
     * 获取流程上下文
     */
    public IWorkflowDataContext getWorkflowDataContext() {
        return this.dataContext;
    }

    /**
     * 获取流程日志
     */
    public IWorkflowLog getWorkflowLog() {
        return this.workflowLog;
    }

    /**
     * 获取流程模板
     */
    public WorkflowTemplate getWorkflowTemplate() {
        return workflowTemplate;
    }

    /**
     * 获取流程实例ID
     */
    public String getWfInstanceId() {
        return wfInstanceId;
    }

    public boolean isRopHasRun() {
        return ropHasRun;
    }

    public void setRopHasRun(boolean ropHasRun) {
        this.ropHasRun = ropHasRun;
    }

    public boolean isInstanceEndHookHasRun() {
        return instanceEndHookHasRun;
    }

    public void setInstanceEndHookHasRun(boolean instanceEndHookHasRun) {
        this.instanceEndHookHasRun = instanceEndHookHasRun;
    }

    public WorkflowHookContext getHookContext() {
        return hookContext;
    }

    public void setHookContext(WorkflowHookContext hookContext) {
        this.hookContext = hookContext;
    }

    public Map<String, List<WorkflowHookWrapper>> getInstanceRuntimeHooks() {
        return instanceRuntimeHooks;
    }

    public long getStartTime() {
        return startTime;
    }

    /**
     * 添加实例运行时钩子
     * @param hookType 钩子类型
     * @param isAffect 是否影响主流程
     * @param hook
     */
    public void  addHook(String hookType, boolean isAffect, WorkflowHook hook){
        WorkflowHookWrapper hookWrapper = new WorkflowHookWrapper(hook,isAffect,hookType);
        if(null == instanceRuntimeHooks.get(hookType)){
            instanceRuntimeHooks.put(hookType, new ArrayList<WorkflowHookWrapper>());
        }
        instanceRuntimeHooks.get(hookType).add(hookWrapper);
    }

    /**
    * 运行实例钩子
    * @param hookType 钩子类型
    * @return
    * @author 18040994
    * @date 2019/6/6 20:07
    */
    public int runHooks(String hookType) {
        List<WorkflowHookWrapper> hookWrappers = instanceRuntimeHooks.get(hookType);
        if (null != hookWrappers) {
            for (WorkflowHookWrapper hookWrapper : hookWrappers) {
                try {
                    int hookRunResult = hookWrapper.runHook(this.hookContext);
                    if(WorkflowConstant.WORKFLOW_HOOK_RUN_FAILED==hookRunResult&&hookWrapper.isAffect()){
                        return WorkflowConstant.WORKFLOW_HOOK_RUN_FAILED;
                    }
                } catch (Exception e) {
                    processHookException(hookType, hookWrapper, e);
                }
            }
        }
        return WorkflowConstant.WORKFLOW_HOOK_RUN_SUCCESS;
    }

    private void processHookException(String hookType, WorkflowHookWrapper hookWrapper, Exception e) {
        if (hookWrapper.isAffect()) {
            //钩子影响主流程直接将异常抛出，终止当前流程
            throw new WorkflowException(WorkflowCode.WF1001,
                    "workflow instance hook (" + hookType + ") encountered exception", e);
        } else {
            //不影响主流程的异常打印异常日志
            String errorLog = WorkflowLogUtil
                    .concatInstanceErrorMsg("workflow instance hook(" + hookType + ") run failed", this,
                            System.currentTimeMillis() - startTime, e);
            LOGGER.error(errorLog);
        }
    }
}
