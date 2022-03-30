package com.suning.zhongtai.wf.core;

import com.suning.zhongtai.wf.bootstrap.WorkflowEngineInitListener;
import com.suning.zhongtai.wf.constant.WorkflowConstant;
import com.suning.zhongtai.wf.core.step.WorkflowStep;
import com.suning.zhongtai.wf.core.step.executable.IWorkflowNodeExecutable;
import com.suning.zhongtai.wf.core.step.node.*;
import com.suning.zhongtai.wf.dao.HotloadDao;
import com.suning.zhongtai.wf.dao.TemplateDao;
import com.suning.zhongtai.wf.dao.entity.WfCndNodeItem;
import com.suning.zhongtai.wf.dao.entity.WfDefinition;
import com.suning.zhongtai.wf.dao.entity.WfNode;
import com.suning.zhongtai.wf.dao.entity.WfStep;
import com.suning.zhongtai.wf.exception.WorkflowException;
import com.suning.zhongtai.wf.exception.WorkflowLoadError;
import com.suning.zhongtai.wf.exception.code.WorkflowCode;
import com.suning.zhongtai.wf.tool.TemplateUtil;
import com.suning.zhongtai.wf.tool.WfSqlUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * 流程模板加载
 *
 * @author 13073189
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Component
public class WorkflowEngineInitializer implements IWorkflowEngineInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowEngineInitializer.class);

    private static final int PAGE_SIZE = 100;

    @Resource
    private TemplateDao templateDao;

    @Resource
    private HotloadDao hotloadDao;

    /**
     * 全量方式加载全部有效模板信息
     * 全量加载的关键方法
     */
    @Override
    public void loadTemplates() {
        //流程引擎加载错误信息。就是一个错误结点的容器
        WorkflowLoadError workflowLoadError = new WorkflowLoadError();
        // 节点创建，String是NODE_CODE，BaseWorkflowNode（包括基础节点和组合节点，其中组合节点已经加载好了）
        //String wfVersion不为空（null）,则说明走的是增量加载（实时更新实时刷新，类似zookeeper）
        Map<String, BaseWorkflowNode> nodesCollection = createNodes(null, workflowLoadError);
        // 模板创建
        Map<String, WorkflowTemplate> workflowTemplates = createTemplates(nodesCollection,null,workflowLoadError);
        //检查加载错误信息是否为空，若不为空将异常抛出
        if(workflowLoadError.checkErrorExist()){
            throw new WorkflowException(WorkflowCode.WF2010, "流程引擎初始化异常，错误信息如下："+workflowLoadError.toString());
        }
        // 流程模板校验
        TemplateUtil.templatesValidate(workflowTemplates);
        // 模板生效-把创建好的模板放入模板容器中
        WorkflowTemplateContainer.loadTemplates(workflowTemplates);
    }

    @Override
    public void hotLoadTemplate(String wfVersonNum) {
        //增量加载错误信息
        WorkflowLoadError workflowLoadError = new WorkflowLoadError();
        // 增量节点创建
        Map<String, BaseWorkflowNode> nodesCollection = createNodes(wfVersonNum,workflowLoadError);
        // 增量模板创建
        Map<String, WorkflowTemplate> workflowTemplates = createTemplates(nodesCollection,wfVersonNum,workflowLoadError);
        //检查增量加载错误信息是否为空，若不为空将异常抛出
        if(workflowLoadError.checkErrorExist()){
            throw new WorkflowException(WorkflowCode.WF2010, "流程引擎增量加载异常，错误信息如下："+workflowLoadError.toString());
        }
        // 增量流程模板校验
        TemplateUtil.templatesValidate(workflowTemplates);
        // 增量模板缓存
        WorkflowTemplateContainer.hotTemplatesCache(wfVersonNum,workflowTemplates);
    }

    // 构建流程模板
    private Map<String, WorkflowTemplate> createTemplates(Map<String, BaseWorkflowNode> nodesCollection, String wfVersion, WorkflowLoadError workflowLoadError) {
        Map<String, WorkflowTemplate> workflowTemplates;
        if(StringUtils.isNotEmpty(wfVersion)){
            //版本号不为空，触发增量加载
            workflowTemplates = hotLoadTemplates(wfVersion,nodesCollection, workflowLoadError);
        }
        else{
            //版本号为空，触发全量加载
            workflowTemplates = fullLoadTemplate(nodesCollection, workflowLoadError);
        }

        return workflowTemplates;
    }

    private Map<String, WorkflowTemplate> fullLoadTemplate(Map<String, BaseWorkflowNode> nodesCollection, WorkflowLoadError workflowLoadError) {
        Map<String,Object> conditions =  new HashMap<>();
        conditions.put("wfState",WorkflowConstant.ACTIVE_STATUS);
        //WfDefinition是关于流程模板的描述，但是没有具体执行步骤
        List<WfDefinition> wfDefinitions = templateDao.queryWfTemplatesDefinition(conditions);
        //这个map的对象是流程编码，key是WF_CODE流程编码，WorkflowTemplate是对wf_definition表抽取出来的数据封装
        Map<String, WorkflowTemplate> workflowTemplates = setWfTemplateBaseInfo(wfDefinitions);
        // 构建模板步骤，版本号为空，触发全量加载
        //WorkflowTemplate是对wf_definition表抽取出来的数据封装，这次是有具体执行步骤的
        //主要作用就是封装执行步骤
        //往WorkflowTemplate里面的 Map<String, WorkflowStep> workflowSteps里面封装执行对象
        createSteps(null, nodesCollection, workflowTemplates,workflowLoadError);
        return workflowTemplates;
    }

    private Map<String, WorkflowTemplate> setWfTemplateBaseInfo(List<WfDefinition> wfDefinitions) {
        Map<String, WorkflowTemplate> workflowTemplates = new HashMap<>();
        if(!CollectionUtils.isEmpty(wfDefinitions)){
            for (WfDefinition wfDefinition : wfDefinitions) {
            WorkflowTemplate wfTemplate = new WorkflowTemplate();
            String wfCode = wfDefinition.getWfCode();
            // 模板基本信息设置
            wfTemplate.setId(wfDefinition.getId());
            wfTemplate.setWfId(wfDefinition.getWfId());
            wfTemplate.setWfCode(wfCode);
            wfTemplate.setWfName(wfDefinition.getWfName());
            wfTemplate.setWfVersion(wfDefinition.getWfVersion());
            wfTemplate.setWfState(wfDefinition.getWfState());
            wfTemplate.setWfTypeCode(wfDefinition.getWfTypeCode());
            workflowTemplates.put(wfCode, wfTemplate);
        }
        }
        return workflowTemplates;
    }

    // 创建流程步骤
    /**
     * wfVersion:是全量加载，还是增量加载
     *nodesCollection：节点信息
     *
     *
     */
    private void createSteps(String wfVersion, Map<String, BaseWorkflowNode> nodesCollection,
            Map<String, WorkflowTemplate> workflowTemplates,WorkflowLoadError workflowLoadError) {
        int count = 0;
        List<String> templateCodelist = new ArrayList<>();
        for (WorkflowTemplate template : workflowTemplates.values()) {
            //流程编码
            String wfCode = template.getWfCode();
            templateCodelist.add(wfCode);
            count++;
            if (PAGE_SIZE == templateCodelist.size() || count == workflowTemplates.size()) {
                //wfVersion为空触发全量加载，templateCodelist是wfCode的集合（批量操作）
                batchCreateSteps(wfVersion,templateCodelist, nodesCollection, workflowTemplates,workflowLoadError);
                templateCodelist.clear();
            }
        }
    }

    // 分批创建流程步骤
    //templateCodelist是模板编码的集合（我们以只有一个为例来：P_ORD_CL_PE_HDL_OLN）
    //workflowTemplates的key是WF_CODE（流程编码）
    private void batchCreateSteps(String wfVersion, List<String> templateCodelist, Map<String, BaseWorkflowNode> nodesCollection,
            Map<String, WorkflowTemplate> workflowTemplates,WorkflowLoadError workflowLoadError) {
        List<WfStep> wfSteps;
        if(StringUtils.isNotEmpty(wfVersion)){
            wfSteps = hotloadDao.queryZtwfmtSteps(WfSqlUtil.inSqlParam(templateCodelist));
        }
        else{
            //templateCodelist是流程编码的集合,刚才抽取的100个
            wfSteps = templateDao.queryWfStepsDefinition(WfSqlUtil.inSqlParam(templateCodelist));
        }
        if(!CollectionUtils.isEmpty(wfSteps)){
            for (WfStep wfStep : wfSteps) {
                //获取流程编码
            String wfCode = wfStep.getWfCode();
            //获取流程中某一个节点的编码
            String nodeCode = wfStep.getNodeCode();
            //获取该节点在整个流程中的第几个步骤
            String stepNum = Integer.toString(wfStep.getStepNum());

            // step集合填充，这个是某一个流程模板（WorkflowTemplate）里面的map集合
            Map<String, WorkflowStep> workflowSteps = workflowTemplates.get(wfCode).getWorkflowSteps();
            WorkflowStep workflowStep = new WorkflowStep();
            workflowStep.setStepNum(Integer.toString(wfStep.getStepNum()));
            workflowStep.setNextStepNumY(Integer.toString(wfStep.getNextStepY()));
            workflowStep.setNextStepNumN(Integer.toString(wfStep.getNextStepN()));
            workflowStep.setRollbackFlag(WorkflowConstant.WORKFLOW_NODE_ROLLBACK_FLAG == wfStep.getRollbackFlag());
            if (WorkflowConstant.WORKFLOW_NODE_CODE_SOP.equals(nodeCode)) {
                workflowSteps.put(nodeCode, workflowStep);
            } else {
                workflowSteps.put(stepNum, workflowStep);
            }

            // 设置Node、isCnd
            BaseWorkflowNode node = nodesCollection.get(nodeCode);
            if (null == node) {
                workflowLoadError.getNotDefineNodeNames().add(nodeCode);
                LOGGER.error("模板" + wfCode + "的节点" + nodeCode + "未定义");
                continue;
            }
            workflowStep.setCnd(WorkflowConstant.WORKFLOW_NODE_TYPE_CND == node.getNodeType());
            workflowStep.setNode(node);
        }
        }
    }

    // 创建流程节点
    private Map<String, BaseWorkflowNode> createNodes(String wfVersion, WorkflowLoadError workflowLoadError) {
        // 缓存所有节点
        Map<String, BaseWorkflowNode> nodesCollection;
        if(StringUtils.isNotEmpty(wfVersion)){
            //版本号不为空，触发增量加载
            nodesCollection = hotLoadNodes(wfVersion, workflowLoadError);
        }
        else{
            //版本号为空，触发全量加载
            nodesCollection = fullLoadNodes(null, workflowLoadError);
        }

        return nodesCollection;
    }

    private Map<String, BaseWorkflowNode> fullLoadNodes(String wfVersion, WorkflowLoadError workflowLoadError) {
        Map<String,Object> conditions =  new HashMap<>();
        conditions.put("wfNodeState", WorkflowConstant.ACTIVE_STATUS);
        //查询条件为："wfNodeState"是"1"的记录
        List<WfNode> wfNodes = templateDao.queryWfNodesDefinition(conditions);
        //这个map里面的String对象放的是nodecode，BaseWorkflowNode是流程节点对象
        //如果是基础节点就是已经封装好了
        //如果是组合节点还没封装好了，还需要继续封装
        Map<String, BaseWorkflowNode> nodesCollection = setNodeBaseInfo(workflowLoadError, wfNodes);
        // 构造组合节点子项
        createCndNodeItems(nodesCollection,wfVersion,workflowLoadError);

        return nodesCollection;
    }

    //把wf_node表对应的WfNode对象组装成 BaseWorkflowNode对象，并放进MAP里面
    private Map<String, BaseWorkflowNode> setNodeBaseInfo(WorkflowLoadError workflowLoadError, List<WfNode> wfNodes) {
        Map<String, BaseWorkflowNode> nodesCollection = new HashMap<>();
        if (!CollectionUtils.isEmpty(wfNodes)) {
            for (WfNode wfNode : wfNodes) {
                String nodeCode = wfNode.getNodeCode();
                //'节点类型 0 基础节点 1 组合节点'
                int nodeType = wfNode.getNodeType();

                // 只有sop、eop、eopf、StandardNode(这四个信息存在wf_step表中)这四种节点有且只有一个实例，被所有流程公用,其它节点根据编码不同有不同的实例
                BaseWorkflowNode node = WorkflowNodeFactory.produceNode(nodeType, nodeCode, wfNode.getCndCallType());
                node.setNodeCode(nodeCode);
                node.setNodeName(wfNode.getNodeName());
                node.setNodeStatus(wfNode.getNodeState());
                //节点类型： 0 标准节点 1 组合节点
                node.setNodeType(nodeType);
                node.setRollbackFlag(WorkflowConstant.WORKFLOW_NODE_ROLLBACK_FLAG == wfNode.getRollbackFlag());
                // 模板中用到的节点设置Executable，cnd节点不需要设置
                //这里有一个疑问就是如果从表中取出数据包括EOP/SOP/EOPF，这些节点是没有执行对象的（IWorkflowNodeExecutable属性为空）
                //疑问待解决（已解决，用到是常量表示）
                if (WorkflowConstant.WORKFLOW_NODE_TYPE_CND != nodeType) {
                    ((BndWorkflowNode) node)
                            .setWorkflowNodeExecutable(findExcutableByNodeCode(nodeCode, workflowLoadError));
                }
                nodesCollection.put(nodeCode, node);
            }
        }
        return nodesCollection;
    }

    private Map<String, BaseWorkflowNode>  hotLoadNodes(String wfVersion, WorkflowLoadError workflowLoadError) {
        Map<String, BaseWorkflowNode> nodesCollection = new HashMap<>();
        //查询运行中的
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("wfNodeState",WorkflowConstant.ACTIVE_STATUS);
        List<WfNode> runningWfNodes = templateDao.queryWfNodesDefinition(conditions);
        Map<String,WfNode> runningNodeMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(runningWfNodes)){
            for(WfNode node: runningWfNodes){
                runningNodeMap.put(node.getNodeCode(), node);
            }
        }
        //查询增量节点
        List<WfNode> incrBndNodes = new ArrayList<>();
        List<WfNode> incrCndNodes = new ArrayList<>();
        conditions.put("wfVersion",wfVersion);
        List<WfNode> incrWfNodes = hotloadDao.queryZtwfmtNodes(conditions);
        if(!CollectionUtils.isEmpty(incrWfNodes)){
            for(WfNode node: incrWfNodes){
                Set<String> runningNodeCodes = runningNodeMap.keySet();
                if(runningNodeCodes.contains(node.getNodeCode())){
                    runningWfNodes.remove(runningNodeMap.get(node.getNodeCode()));
                }
                if(node.getNodeType() ==  WorkflowConstant.WORKFLOW_NODE_TYPE_BND){
                    incrBndNodes.add(node);
                }
                else{
                    incrCndNodes.add(node);
                }
            }
        }

        //增量新增和修改的基础节点(不需要构造组合节点子项)和运行时节点一起处理
        runningWfNodes.addAll(incrBndNodes);
        Map<String, BaseWorkflowNode> nodesCollection1 = setNodeBaseInfo(workflowLoadError, runningWfNodes);
        if(!CollectionUtils.isEmpty(nodesCollection1)){
            createCndNodeItems(nodesCollection1,null,workflowLoadError);
            nodesCollection.putAll(nodesCollection1);
        }
        //将增量新增和修改的组合节点单独拎出来处理
        Map<String, BaseWorkflowNode> nodesCollection2 = setNodeBaseInfo(workflowLoadError, incrCndNodes);
        if(!CollectionUtils.isEmpty(nodesCollection2)){
            createCndNodeItems(nodesCollection2, wfVersion, workflowLoadError);
            nodesCollection.putAll(nodesCollection2);
        }

        return nodesCollection;
    }

    private Map<String, WorkflowTemplate> hotLoadTemplates(String wfVersion,Map<String, BaseWorkflowNode> nodesCollection, WorkflowLoadError workflowLoadError) {
        Map<String, WorkflowTemplate> workflowTemplates = new HashMap<>();
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("wfState",WorkflowConstant.ACTIVE_STATUS);
        conditions.put("wfVersion",wfVersion);
        //查询增量节点影响的运行中模板
        Map<String,WfDefinition> runningWfDefinitionMap = new HashMap<>();
        List<WfDefinition> runningWfDefinitions = hotloadDao.queryIncrNodeAffectedTemplates(conditions);
        if(!CollectionUtils.isEmpty(runningWfDefinitions)){
            for(WfDefinition wfDefinition: runningWfDefinitions){
                runningWfDefinitionMap.put(wfDefinition.getWfCode(), wfDefinition);
            }
        }
        //查询增量新增或修改的模板
        List<WfDefinition> incrWfDefinitions = hotloadDao.queryZtwfmtTemplates(conditions);
        if(!CollectionUtils.isEmpty(incrWfDefinitions)){
            for(WfDefinition wfDefinition: incrWfDefinitions){
                Set<String> runningFlowCodes = runningWfDefinitionMap.keySet();
                if(runningFlowCodes.contains(wfDefinition.getWfCode())){
                    runningWfDefinitions.remove(runningWfDefinitionMap.get(wfDefinition.getWfCode()));
                }
            }
        }
        //隐式影响的流程模板，即当前增量节点涉及的运行中模板
        Map<String, WorkflowTemplate> implicitAffectedWfTemplates = setWfTemplateBaseInfo(runningWfDefinitions);
        if(implicitAffectedWfTemplates !=null && !implicitAffectedWfTemplates.isEmpty()){
            createSteps(null, nodesCollection, implicitAffectedWfTemplates,workflowLoadError);
            workflowTemplates.putAll(implicitAffectedWfTemplates);
        }
        //显示增量的流程版本，即当前版本修改或新增的流程
        Map<String, WorkflowTemplate> explicitAffectedWfTemplates = setWfTemplateBaseInfo(incrWfDefinitions);
        if(explicitAffectedWfTemplates !=null && !explicitAffectedWfTemplates.isEmpty()){
            createSteps(wfVersion, nodesCollection, explicitAffectedWfTemplates,workflowLoadError);
            workflowTemplates.putAll(explicitAffectedWfTemplates);
        }
        return workflowTemplates;
    }

    // cnd节点子项加载
    private void createCndNodeItems(Map<String, BaseWorkflowNode> nodesCollection, String wfVersion,WorkflowLoadError workflowLoadError) {
        Map<String,Object> conditions =  new HashMap<>();
        List<WfCndNodeItem> resutList;
        if(StringUtils.isNotEmpty(wfVersion)){
            conditions.put("wfVersion",wfVersion);
            resutList = hotloadDao.queryZtwfmtCndNodeItems(conditions);
        }
        else{
            conditions.put("wfNodeState",WorkflowConstant.ACTIVE_STATUS);
            //去加载组合结点的表
            resutList = templateDao.queryWfCndNodeItems(conditions);
        }
        if(!CollectionUtils.isEmpty(resutList)){
            for (WfCndNodeItem nodeItem : resutList) {
                String cndCode = nodeItem.getCndCode();
                // 检查该cnd节点是否已定义
                BaseWorkflowNode node = nodesCollection.get(cndCode);
                if (null == node) {
                    workflowLoadError.getNotDefineNodeNames().add(cndCode);
                    LOGGER.error("CND节点" + cndCode + "未定义");
                    continue;
                }

                // 检查该cnd节点类型是否正确
                if (!(node instanceof CndWorkflowNode)) {
                    workflowLoadError.getTypeErrorNodeNames().add(cndCode);
                    LOGGER.error("节点" + cndCode + "不是CND类型节点");
                    continue;
                }

                CndWorkflowNode cndNode = (CndWorkflowNode) node;

                String bndCode = nodeItem.getBndCode();
                // 检查该bnd节点是否已定义
                node = nodesCollection.get(bndCode);
                if (null == node) {
                    workflowLoadError.getNotDefineNodeNames().add(bndCode);
                    LOGGER.error("CND节点" + cndCode + "的子项" + bndCode + "未定义");
                    continue;
                }

                // Cnd节点子项只能是基础节点
                if (!(node instanceof StandardWorkflowNode)) {
                    workflowLoadError.getTypeErrorNodeNames().add(bndCode);
                    LOGGER.error("节点" + bndCode + "不是有效的CND节点" + cndCode + "子项");
                }

                StandardWorkflowNode bndNode = (StandardWorkflowNode) node;
                //下面这行代码不需要。因为基础结点已经封装好了
                bndNode.setWorkflowNodeExecutable(findExcutableByNodeCode(bndCode,workflowLoadError));

                // 绑定子项，放入组合结点的map中
                cndNode.getCndNodes().put(nodeItem.getStepNum(), bndNode);
            }
        }
    }
    
    private IWorkflowNodeExecutable findExcutableByNodeCode(String nodeCode,WorkflowLoadError workflowLoadError) {
        IWorkflowNodeExecutable executable = (IWorkflowNodeExecutable) WorkflowEngineInitListener.EXECUTABLES.get(nodeCode);
        
        if (null == executable) {
            workflowLoadError.getNotImplNodeNames().add(nodeCode);
            LOGGER.error("节点" + nodeCode + "没有找到对应的业务bean实现");
        }
        return executable;
    }
}