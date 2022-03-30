package com.suning.zhongtai.wf.tool;

import com.suning.zhongtai.wf.constant.WorkflowConstant;
import com.suning.zhongtai.wf.core.WorkflowTemplate;
import com.suning.zhongtai.wf.core.step.WorkflowStep;
import com.suning.zhongtai.wf.core.step.node.CndWorkflowNode;
import com.suning.zhongtai.wf.core.step.node.EopWorkflowNode;
import com.suning.zhongtai.wf.core.step.node.SopWorkflowNode;
import com.suning.zhongtai.wf.core.step.node.StandardWorkflowNode;
import com.suning.zhongtai.wf.exception.WorkflowException;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class TemplateUtilTest {

    // 单个模板校验
    @Test
    public void templateValidate() {
        WorkflowTemplate template = getTemplates().get("P_ORD_SBMT_OLN");
        TemplateUtil.templateValidate("P_ORD_SBMT_OLN", template);
    }
    
    // sop节点检查
    @Test(expectedExceptions = WorkflowException.class,expectedExceptionsMessageRegExp="流程模板P_ORD_SBMT_OLN开始节点未定义")
    public void templateValidate1() {
        WorkflowTemplate template = getTemplates().get("P_ORD_SBMT_OLN");
        template.getWorkflowSteps().remove(WorkflowConstant.WORKFLOW_NODE_CODE_SOP);
        TemplateUtil.templateValidate("P_ORD_SBMT_OLN", template);
    }

    // 后继Y节点检查
    @Test(expectedExceptions = WorkflowException.class,expectedExceptionsMessageRegExp="流程模板P_ORD_SBMT_OLN节点SOP的Y后继节点不存在")
    public void templateValidate2() {
        WorkflowTemplate template = getTemplates().get("P_ORD_SBMT_OLN");
        template.getWorkflowSteps().remove("20");
        TemplateUtil.templateValidate("P_ORD_SBMT_OLN", template);
    }
    
    // 后继N节点检查
    @Test(expectedExceptions = WorkflowException.class,expectedExceptionsMessageRegExp="流程模板P_ORD_SBMT_OLN节点CND_NO_GEN_B2C_POS的N后继节点不存在")
    public void templateValidate3() {
        WorkflowTemplate template = getTemplates().get("P_ORD_SBMT_OLN");
        template.getWorkflowSteps().get("20").setNextStepNumN("25");
        TemplateUtil.templateValidate("P_ORD_SBMT_OLN", template);
    }
    
    // eop节点检查
    @Test(expectedExceptions = WorkflowException.class,expectedExceptionsMessageRegExp="流程模板P_ORD_SBMT_OLN结束节点不存在")
    public void templateValidate4() {
        WorkflowTemplate template = getTemplates().get("P_ORD_SBMT_OLN");
        StandardWorkflowNode node = new StandardWorkflowNode();
        node.setNodeCode("ND_SYNC_OMSQ");
        node.setNodeType(0);
        node.setNodeName("订单信息下发（OMSQ）（全量）");
        node.setRollbackFlag(false);
        node.setNodeStatus(1);
        WorkflowStep step = new WorkflowStep();
        template.getWorkflowSteps().put("30", step);
        step.setStepNum("30");//覆盖掉eop
        step.setNextStepNumY("30");
        step.setNextStepNumN("30");
        step.setNode(node);
        TemplateUtil.templateValidate("P_ORD_SBMT_OLN", template);
    }
    
    // 批量校验
    @Test
    public void templatesValidate() {
        Map<String, WorkflowTemplate> templates = getTemplates();
        TemplateUtil.templatesValidate(templates);
    }

    public Map<String, WorkflowTemplate> getTemplates() {
        Map<String, WorkflowTemplate> workflowTemplates = new HashMap<>();
        WorkflowTemplate template = new WorkflowTemplate();
        workflowTemplates.put("P_ORD_SBMT_OLN", template);

        template.setId(1l);
        template.setWfCode("P_ORD_SBMT_OLN");
        template.setWfName("订单提交（在线提交）");
        template.setWfState(1);
        template.setWfVersion("V1");

        Map<String, WorkflowStep> steps = template.getWorkflowSteps();
        WorkflowStep step = new WorkflowStep();
        step.setStepNum("10");
        step.setNextStepNumY("20");
        step.setNextStepNumN("20");
        SopWorkflowNode sopNode = new SopWorkflowNode();
        sopNode.setNodeCode("SOP");
        step.setNode(sopNode);
        steps.put("SOP", step);

        Map<Integer, StandardWorkflowNode> nodeItems = new HashMap<>();
        StandardWorkflowNode node = new StandardWorkflowNode();
        node.setNodeCode("ND_NO_GEN_POS");
        node.setNodeType(0);
        node.setNodeName("单号生成（POS）");
        node.setRollbackFlag(false);
        node.setNodeStatus(1);
        nodeItems.put(new Integer(10), node);

        node = new StandardWorkflowNode();
        node.setNodeCode("ND_NO_GEN_B2C");
        node.setNodeType(0);
        node.setNodeName("单号生成（B2C）");
        node.setRollbackFlag(false);
        node.setNodeStatus(1);
        nodeItems.put(new Integer(20), node);

        CndWorkflowNode cndNode = new CndWorkflowNode();
        cndNode.setCndExecuteType(0);
        cndNode.getCndNodes().putAll(nodeItems);
        cndNode.setNodeStatus(1);
        cndNode.setRollbackFlag(false);
        cndNode.setNodeCode("CND_NO_GEN_B2C_POS");
        cndNode.getCndNodes().putAll(nodeItems);

        step = new WorkflowStep();
        step.setNextStepNumN("30");
        step.setNextStepNumY("30");
        step.setStepNum("20");
        step.setCnd(true);
        step.setNode(cndNode);
        steps.put("20", step);

        step = new WorkflowStep();
        EopWorkflowNode eopNode = new EopWorkflowNode();
        eopNode.setNodeCode("EOP");
        step.setNode(eopNode);
        step.setStepNum("30");
        steps.put("30", step);

        return workflowTemplates;
    }
}
