package com.suning.zhongtai.wf.core.step;

import com.suning.zhongtai.wf.constant.WorkflowConstant;
import com.suning.zhongtai.wf.core.WorkflowInstance;
import com.suning.zhongtai.wf.core.WorkflowThreadPool;
import com.suning.zhongtai.wf.core.context.IWorkflowDataContext;
import com.suning.zhongtai.wf.core.context.WorkflowDataContextWrapper;
import com.suning.zhongtai.wf.core.context.WorkflowHookContext;
import com.suning.zhongtai.wf.core.step.executable.IWorkflowNodeExecutable;
import com.suning.zhongtai.wf.core.step.node.*;
import com.suning.zhongtai.wf.exception.WorkflowException;
import com.suning.zhongtai.wf.exception.code.WorkflowCode;
import com.suning.zhongtai.wf.log.WorkflowClmStepLogBean;
import com.suning.zhongtai.wf.tool.WorkFlowThreadPoolHolder;
import com.suning.zhongtai.wf.tool.WorkflowLogUtil;
import com.suning.zhongtai.wf.worker.CNDWorker;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 流程执行步骤实体
 * @author: 18040994
 * @date: 2018/11/29 19:40
 */
public class WorkflowStep implements IWorkflowNode, Serializable {

    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowStep.class);


    /**
     * 是否支持能回滚 0不支持 1支持
     */
    protected boolean rollbackFlag;
    /**
     * 步骤号
     */
    private String stepNum;

    /**
     * 步骤执行结果Y对应的下个步骤号
     */
    private String nextStepNumY;

    /**
     * 步骤执行结果N对应的下个步骤号
     */
    private String nextStepNumN;

    /**
     * 是否是组合步骤标示
     */
    private boolean isCnd;

    /**
     * 流程节点
     */
    private BaseWorkflowNode node;

    public WorkflowStep() {
        super();
    }

    /**
     *
     * @param stepNum   步骤号
     * @param isCnd     是否为组合节点
     * @param node    节点
     */
    public WorkflowStep(String stepNum, boolean isCnd, BaseWorkflowNode node) {
        this.stepNum = stepNum;
        this.isCnd = isCnd;
        this.node = node;
    }

    @Override
    public boolean canRollback() {
        return node.isRollbackFlag();
    }

    /**
     * 进行执行操作
     *
     * @param workflowInstance 流程实例
     * @return 0 就是N (失败或异常); 1就是Y (成功)
     */
    @Override
    public int execute(WorkflowInstance workflowInstance){
        int result;
        long startTime = System.currentTimeMillis();
        //记录步骤开始日志
        WorkflowClmStepLogBean workflowClmStepLogBean = WorkflowLogUtil.stepLogBegin(stepNum, node.getNodeCode());
        try {
            if (isCnd) {
                //组合节点
                result = executeCndNode(workflowInstance, (CndWorkflowNode) node);
            } else {
                // 基础节点和标记节点
                result = executeBndNode(workflowInstance, (BndWorkflowNode) node, workflowClmStepLogBean);
            }
            // 记录步骤结束日志
            WorkflowLogUtil.stepInfoLogEnd(workflowInstance, workflowClmStepLogBean, result, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            // 信息异常
            String errorMsg = WorkflowLogUtil.concatStepErrorMsg(e.getMessage(), workflowInstance, stepNum, node.getNodeCode(), e);
            // 记录CLM日志
            WorkflowLogUtil.stepErrorLogEnd(workflowInstance, workflowClmStepLogBean,System.currentTimeMillis() - startTime, errorMsg);
            throw e;
        }
        return result;
    }

    /**
     * 执行组合节点
     *
     * @param workflowInstance 流程实例
     * @param cndWorkflowNode  组合节点
     * @return 0 就是N (失败或异常); 1就是Y (成功)
     * @throws InterruptedException
     * @throws ExecutionException
     */
    protected int executeCndNode(WorkflowInstance workflowInstance, CndWorkflowNode cndWorkflowNode){
        try {
            // 组合节点子节点
            Map<Integer, StandardWorkflowNode> cndNodes = cndWorkflowNode.getCndNodes();
            // 组合节点执行类型
            int cndExecuteType = cndWorkflowNode.getCndExecuteType();
            if (cndExecuteType == WorkflowConstant.WORKFLOW_CND_EXECUTE_TYPE_PARL) {
                // 并行执行
                return executeCndNodeByParl(workflowInstance, cndNodes);
            }
            else if(cndExecuteType == WorkflowConstant.WORKFLOW_CND_EXECUTE_TYPE_SERL){
                // 串行执行
                return executeCndNodeBySerl(workflowInstance, cndNodes);
            }
            else{
                throw new WorkflowException(WorkflowCode.WF2006, "illegal CND Call type : " + cndExecuteType);
            }
        } catch (Exception e) {
            throw new WorkflowException(WorkflowCode.WF1000, "CND execution (parl) encountered exception", e);
        }
    }

    /**
     * 组合节点串行
     *
     * @param workflowInstance 流程实例
     * @param cndNodes         组合节点
     * @return 0 就是N (失败或异常); 1就是Y (成功)
     */
    protected int executeCndNodeBySerl(WorkflowInstance workflowInstance, Map<Integer, StandardWorkflowNode> cndNodes) {

        int result = WorkflowConstant.WORKFLOW_FLAG_Y;
        // 循环顺序执行(需要保证顺序，通过步骤号来获取。10,20,30……)
        for (Map.Entry<Integer, StandardWorkflowNode> childNode : cndNodes.entrySet()) {
            // 记录开始时间
            long startTime = System.currentTimeMillis();
            // 子节点步骤号: 组合节点步骤号-Item步骤号
            String childStepNum = getFullStepNum(stepNum, childNode.getKey());
            StandardWorkflowNode sNode = childNode.getValue();
            // 初始化步骤日志
            WorkflowClmStepLogBean workflowClmStepLogBean = WorkflowLogUtil.stepLogBegin(childStepNum, sNode.getNodeCode());
            // 获取实现类
            IWorkflowNodeExecutable workflowNodeExecutable = sNode.getWorkflowNodeExecutable();
            WorkflowDataContextWrapper workflowDataContextWrapper = new WorkflowDataContextWrapper(childStepNum,sNode.getNodeCode(),workflowInstance.getWorkflowDataContext());
            try {
                // 执行方法
                result = workflowNodeExecutable.execute(workflowDataContextWrapper);
                //子项串行执行时，有一个返回值为N，则直接结束，整个组合节点返回值N
                if (WorkflowConstant.WORKFLOW_FLAG_N == result) {
                    break;
                }
            } catch (Exception e) {
                // 记录异常日志
                String errorMsg = WorkflowLogUtil
                        .concatStepErrorMsg("CND execution (Serl) encountered exception: ", workflowInstance, childStepNum,
                                sNode.getNodeCode(), e);
                WorkflowLogUtil
                        .stepErrorLogEnd(workflowInstance, workflowClmStepLogBean, (System.currentTimeMillis() - startTime), errorMsg);
                //串行执行当前step执行失败，后续step不再执行，抛出异常终止流程
                throw e;
            }
            // 记录日志
            WorkflowLogUtil.stepInfoLogEnd(workflowInstance, workflowClmStepLogBean, result, (System.currentTimeMillis() - startTime));
        }
        return result;
    }

    /**
     * 组合节点并行
     *
     * @param workflowInstance 流程实例
     * @param cndNodes         组合节点
     * @return 0 就是N (失败或异常); 1就是Y (成功)
     */
    protected int executeCndNodeByParl(WorkflowInstance workflowInstance, Map<Integer, StandardWorkflowNode> cndNodes)
            throws InterruptedException, ExecutionException {
        int result = WorkflowConstant.WORKFLOW_FLAG_Y;
        // 计数器
        CountDownLatch countDownLatch = new CountDownLatch(cndNodes.size());
        // 线程池
        WorkflowThreadPool cndThreadPool = WorkFlowThreadPoolHolder.getCndThreadPool();
        // 步骤执行结果
        List<Future<Integer>> futures = new ArrayList<>();
        // 并发执行
        for (Map.Entry<Integer, StandardWorkflowNode> childNode : cndNodes.entrySet()) {
            // 步骤号
            String fullStepNum = getFullStepNum(stepNum, childNode.getKey());
            // 获取子节点
            StandardWorkflowNode standardWorkflowNode = childNode.getValue();
            // 并行执行子任务
            CNDWorker cndWorker = new CNDWorker(workflowInstance, fullStepNum, standardWorkflowNode, countDownLatch);
            Future<Integer> future = cndThreadPool.submit(cndWorker);
            // 获取结果
            futures.add(future);
        }
        // 等待结果
        countDownLatch.await();
        for (Future<Integer> future : futures) {
            // 有一个步骤失败，则认为失败
            try {
                if (future.get() == WorkflowConstant.WORKFLOW_FLAG_N) {
                    result = WorkflowConstant.WORKFLOW_FLAG_N;
                    break;
                }
            } catch (Exception e) {
                //子线程抛出异常直接抛出，中段流程
                throw e;
            }
        }
        return result;
    }

    /**
     * 执行标准节点或标记节点
     * @param workflowInstance 流程实例
     * @param node                流程节点
     * @return 0 就是N (失败或异常); 1就是Y (成功)
     * @throws WorkflowException 包装业务异常
     */
    protected int executeBndNode(WorkflowInstance workflowInstance, BndWorkflowNode node, WorkflowClmStepLogBean workflowClmStepLogBean) {
        int result;
        String nodeCode = node.getNodeCode();
        long startTime = System.currentTimeMillis();
        // 获取上下文
        IWorkflowDataContext workflowDataContext = workflowInstance.getWorkflowDataContext();
        if(node instanceof StandardWorkflowNode){
            // 业务实现类
            try {
                result = node.getWorkflowNodeExecutable().execute(new WorkflowDataContextWrapper(stepNum,nodeCode,workflowDataContext));
            } catch (Exception e) {
                // 记录异常日志
                String errorMsg = WorkflowLogUtil.concatStepErrorMsg("BND execution encountered exception: ", workflowInstance, stepNum, nodeCode, e);
                WorkflowLogUtil.stepErrorLogEnd(workflowInstance, workflowClmStepLogBean,(System.currentTimeMillis() - startTime), errorMsg);
                // 业务异常中断流程
                throw new WorkflowException(WorkflowCode.WF1000, "BND execution encountered exception", e);
            }
        }
        else{
            //标记节点执行
            result = executeLabelNode(workflowInstance, nodeCode, workflowDataContext);
        }
        return result;
    }

    /**
    * 标记节点执行
    * @param workflowInstance
     * @param nodeCode
     * @param workflowDataContext
    * @return
    * @author 18040994
    * @date 2019/7/2 16:07
    */
    private int executeLabelNode(WorkflowInstance workflowInstance, String nodeCode, IWorkflowDataContext workflowDataContext) {
        int result = WorkflowConstant.WORKFLOW_FLAG_Y;
        //一般不会为空
        if(workflowInstance.getInstanceRuntimeHooks().isEmpty()){
            //实例中的钩子集合为空，标记节点除EOPF节点外其他的直接返回1
            if(WorkflowConstant.WORKFLOW_NODE_CODE_EOPF.equals(nodeCode)){
                //如果是EOPF节点，那么就开始执行回滚逻辑
                result = workflowInstance.getWorkflowRollbackManager().rollback(workflowDataContext);
            }
            return result;
        }
        switch(nodeCode){
            case WorkflowConstant.WORKFLOW_NODE_CODE_SOP:
                break;
            case WorkflowConstant.WORKFLOW_NODE_CODE_ROP:
                //执行主线程结束钩子（没有做）
                workflowInstance.runHooks(WorkflowConstant.WORKFLOW_MAIN_THREAD_END);
                workflowInstance.setRopHasRun(true);
                break;
            case WorkflowConstant.WORKFLOW_NODE_CODE_EOPF:
                //如果是EOPF节点，那么就开始执行回滚逻辑
                result = workflowInstance.getWorkflowRollbackManager().rollback(workflowDataContext);
            case WorkflowConstant.WORKFLOW_NODE_CODE_EOP:
                WorkflowHookContext hookContext = workflowInstance.getHookContext();
                if(null != hookContext){
                    hookContext.setEndFlag(WorkflowConstant.WORKFLOW_NORMAL_END);
                }
                //执行主线程结束钩子
                if(!workflowInstance.isRopHasRun()){
                    //如果存在ROP节点，且ROP节点已经执行完成，此时主线程结束钩子在EOP节点不再执行
                    workflowInstance.runHooks(WorkflowConstant.WORKFLOW_MAIN_THREAD_END);
                }
                //执行流程结束钩子
                workflowInstance.runHooks(WorkflowConstant.WORKFLOW_INSTANCE_END);
                workflowInstance.setInstanceEndHookHasRun(true);
                break;

        }

        return result;
    }

    /**
     * 进行回滚操作
     *
     * @param workflowDataContext 流程数据上下文
     * @return 0 就是N (失败或异常); 1就是Y (成功)
     * @throws WorkflowException 包装业务异常
     */
    @Override
    public int rollback(IWorkflowDataContext workflowDataContext) {

        try {
            //调用实际开发实现的方法
            StandardWorkflowNode standardWorkflowNode = (StandardWorkflowNode) node;
            String nodeCode = standardWorkflowNode.getNodeCode();
            IWorkflowNodeExecutable workflowNodeExecutable = standardWorkflowNode.getWorkflowNodeExecutable();
            // 执行
            return workflowNodeExecutable.rollback(new WorkflowDataContextWrapper(stepNum,nodeCode,workflowDataContext));
        } catch (Exception e) {
            // 直接抛出异常,由执行方法进行捕获处理
            throw new WorkflowException(WorkflowCode.WF1000, "step rollback encountered exception, node code is : " + node.getNodeCode()+", stepNum is "+stepNum, e);
        }
    }

    public String getStepNum() {
        return stepNum;
    }

    public void setStepNum(String stepNum) {
        this.stepNum = stepNum;
    }

    public boolean isCnd() {
        return isCnd;
    }

    public void setCnd(boolean cnd) {
        isCnd = cnd;
    }

    public BaseWorkflowNode getNode() {
        return node;
    }

    public void setNode(BaseWorkflowNode node) {
        this.node = node;
    }

    public String getFullStepNum(String cndStepNum, int childStepNum) {
        return StringUtils.join(cndStepNum, WorkflowConstant.STEP_SEPARATOR, childStepNum);
    }

    public String getNextStepNumY() {
        return nextStepNumY;
    }

    public void setNextStepNumY(String nextStepNumY) {
        this.nextStepNumY = nextStepNumY;
    }

    public String getNextStepNumN() {
        return nextStepNumN;
    }

    public void setNextStepNumN(String nextStepNumN) {
        this.nextStepNumN = nextStepNumN;
    }

    public boolean isRollbackFlag() {
        return rollbackFlag;
    }

    public void setRollbackFlag(boolean rollbackFlag) {
        this.rollbackFlag = rollbackFlag;
    }
}
