package com.suning.zhongtai.wf.bootstrap;

import com.suning.zhongtai.wf.core.*;
import com.suning.zhongtai.wf.core.context.AbstractWorkflowDataContext;
import com.suning.zhongtai.wf.core.step.WorkflowStep;
import com.suning.zhongtai.wf.core.step.node.CndWorkflowNode;
import com.suning.zhongtai.wf.core.step.node.EopWorkflowNode;
import com.suning.zhongtai.wf.core.step.node.SopWorkflowNode;
import com.suning.zhongtai.wf.core.step.node.StandardWorkflowNode;
import com.suning.zhongtai.wf.exception.WorkflowException;
import com.suning.zhongtai.wf.exception.code.WorkflowCode;
import com.suning.zhongtai.wf.log.IWorkflowLog;
import com.suning.zhongtai.wf.log.WorkflowLog;
import com.suning.zhongtai.wf.tool.WFApplicationContextUtil;
import com.suning.zhongtai.wf.tool.ThreadPoolHolder;
import com.suning.zhongtai.wf.worker.ROPWorker;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import org.junit.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkflowEngineTest {

    @Tested
    WorkflowEngine workflowEngine;

    @Injectable
    WFUidGenerator wfUidGenerator;

    @Injectable
    IWorkflowLog workflowLog;

    @Test
    public void getWorkflowEngine() {
        new Expectations(WFApplicationContextUtil.class) {
            {
                WFApplicationContextUtil.getBean(WorkflowEngine.class);
                result = workflowEngine;
            }
        };
        WorkflowEngine engine = workflowEngine.getWorkflowEngine();

        Assert.assertNotNull(engine);
    }

    // 正常启动
    @Test(dependsOnMethods="getWorkflowEngine")
    public void startWorkflow(final @Mocked WorkflowInstance workflowInstance) {
        new Expectations(WorkflowTemplateContainer.class,  ThreadPoolHolder.class, WFApplicationContextUtil.class) {
            {
                WorkflowTemplateContainer.getWorkflowTemplate("P_ORD_SBMT_OLN");
                result = getTemplates().get("P_ORD_SBMT_OLN");

                wfUidGenerator.getUID();
                result = "3a27d65a-c212-4bea-986e-28d5b7059a78";

                new WorkflowInstance((WorkflowRollbackManager) any, (WorkflowLog) any, (AbstractWorkflowDataContext) any,
                        (WorkflowTemplate) any, anyString);
                result = workflowInstance;
                
                workflowInstance.execute();
                result = WorkflowCode.WF0001;
                
                ThreadPoolHolder.getRopThreadPool().submit((ROPWorker)any);
                result = null;

            }
        };

        WFReturnValue rv = workflowEngine.startWorkflow("P_ORD_SBMT_OLN", new AbstractWorkflowDataContext() {
            @Override
            public List<String> getBisnessId() {
                List<String> bizIds = new ArrayList<>();
                bizIds.add("orderId1");
                return bizIds;
            }
        });
        
        Assert.assertTrue(rv.isSuccess());
    }
    
    // 异常启动
    @Test(dependsOnMethods="getWorkflowEngine")
    public void startWorkflow1(final @Mocked WorkflowInstance workflowInstance ) {
        new Expectations(WorkflowTemplateContainer.class,ThreadPoolHolder.class) {
            {
                WorkflowTemplateContainer.getWorkflowTemplate("P_ORD_SBMT_OLN");
                result = getTemplates().get("P_ORD_SBMT_OLN");
                
//                new WorkflowInstance((WorkflowRollbackManager) any, (WorkflowLog) any, (AbstractWorkflowDataContext) any,
//                        (WorkflowTemplate) any,"wqwqwqwqwqwq");
//                result = workflowInstance;

                wfUidGenerator.getUID();
                result = "3a27d65a-c212-4bea-986e-28d5b7059a78";
                
                workflowInstance.execute();
                result = new WorkflowException(WorkflowCode.WF2001, "模板不存在");
                 
            }
        };
        WFReturnValue rv = workflowEngine.startWorkflow("P_ORD_SBMT_OLN", new AbstractWorkflowDataContext() {
            @Override
            public List<String> getBisnessId() {
                List<String> bizIds = new ArrayList<>();
                bizIds.add("orderId1");
                return bizIds;
            }
        });
        
        Assert.assertTrue(!rv.isSuccess());
        Assert.assertEquals(WorkflowCode.WF2001.getValue(), rv.getWfErrorCode());
    }
    
    // 异常启动-其它异常
    @Test(dependsOnMethods="getWorkflowEngine")
    public void startWorkflow2(final @Mocked WorkflowInstance workflowInstance ) {
        new Expectations(WorkflowTemplateContainer.class,ThreadPoolHolder.class) {
            { 
                WorkflowTemplateContainer.getWorkflowTemplate("P_ORD_SBMT_OLN");
                result = getTemplates().get("P_ORD_SBMT_OLN");

                wfUidGenerator.getUID();
                result = "3a27d65a-c212-4bea-986e-28d5b7059a78";
                
//                new WorkflowInstance((WorkflowRollbackManager) any, (WorkflowLog) any, (AbstractWorkflowDataContext) any,
//                        (WorkflowTemplate) any, "sasasasasas");
//                result = workflowInstance;
                
                workflowInstance.execute();
                result = new RuntimeException();
                 
            }
        };
        WFReturnValue rv = workflowEngine.startWorkflow("P_ORD_SBMT_OLN", new AbstractWorkflowDataContext() {
            @Override
            public List<String> getBisnessId() {
                List<String> bizIds = new ArrayList<>();
                bizIds.add("orderId1");
                return bizIds;
            }
        });
        
        Assert.assertTrue(!rv.isSuccess());
        Assert.assertEquals(WorkflowCode.WF2000.getValue(), rv.getWfErrorCode());
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
