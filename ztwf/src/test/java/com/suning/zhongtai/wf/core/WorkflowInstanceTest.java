package com.suning.zhongtai.wf.core;

import com.suning.zhongtai.wf.core.context.WorkflowDataContextWrapper;
import com.suning.zhongtai.wf.core.context.AbstractWorkflowDataContext;
import com.suning.zhongtai.wf.core.step.WorkflowStep;
import com.suning.zhongtai.wf.core.step.executable.*;
import com.suning.zhongtai.wf.core.step.node.*;
import com.suning.zhongtai.wf.exception.code.WorkflowCode;
import com.suning.zhongtai.wf.log.WorkflowLog;
import com.suning.zhongtai.wf.tool.ThreadPoolHolder;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import static org.testng.Assert.assertTrue;

public class WorkflowInstanceTest {

    WorkflowRollbackManager workflowRollbackManager =new WorkflowRollbackManager();

    WorkflowLog workflowLog = new WorkflowLog();

    @Mocked
    ThreadPoolHolder threadPoolHolder;

    class TestBizContext extends AbstractWorkflowDataContext<String> {

        @Override
        public List<String> getBisnessId() {
            List<String> bizIds = new ArrayList<>();
            bizIds.add("orderId1");
            return bizIds;
        }
    }
    AbstractWorkflowDataContext dataContext = new TestBizContext();

    WorkflowTemplate workflowTemplate = new WorkflowTemplate();


    @Test
    public void testWorkflowInstanceExecute() {
        final WorkflowInstance wins = new WorkflowInstance(workflowRollbackManager, workflowLog, dataContext,workflowTemplate,"saasaddadsdasda");
        workflowTemplate.setWfCode("WF_TEST_01");
        workflowTemplate.setWfVersion("V1");
        class cndIem1 implements IWorkflowNodeExecutable {
            @Override
            public int execute(WorkflowDataContextWrapper dataContext) {
                dataContext.setStepRollback(dataContext.getStepNum(),true);
                return 1;
            }
            @Override
            public int rollback(WorkflowDataContextWrapper dataContext) {
                return 1;
            }
        }

        class cndItem2 implements IWorkflowNodeExecutable {
            @Override
            public int execute(WorkflowDataContextWrapper dataContext) {
                dataContext.setStepRollback(dataContext.getStepNum(),true);
                return 1;
            }
            @Override
            public int rollback(WorkflowDataContextWrapper dataContext) {
                return 1;
            }
        }

        //cndIem1 子项
        StandardWorkflowNode cndItem1Node = new StandardWorkflowNode();
        cndItem1Node.setRollbackFlag(true);
        cndItem1Node.setNodeCode("cndIem1");
        cndItem1Node.setWorkflowNodeExecutable(new cndIem1());

        //cndItem2 子项
        StandardWorkflowNode cndItem2Node = new StandardWorkflowNode();
        cndItem2Node.setRollbackFlag(true);
        cndItem2Node.setNodeCode("cndItem2");
        cndItem2Node.setWorkflowNodeExecutable(new cndItem2());

        Map<Integer, StandardWorkflowNode> cnd1Items = new HashMap<>();
        cnd1Items.put(10, cndItem1Node);
        cnd1Items.put(20, cndItem2Node);

        CndWorkflowNode cnd1Node = new CndWorkflowNode();
        cnd1Node.setRollbackFlag(true);
        cnd1Node.setCndExecuteType(1);
        cnd1Node.getCndNodes().putAll(cnd1Items);

        CndWorkflowNode cnd2Node = new CndWorkflowNode();
        cnd2Node.setRollbackFlag(false);
        cnd2Node.setCndExecuteType(0);
        cnd2Node.getCndNodes().putAll(cnd1Items);

        //SOP Step
        SopWorkflowNode sopNode = new SopWorkflowNode();
        sopNode.setNodeCode("SOP");
        sopNode.setWorkflowNodeExecutable(new SopNodeExecutable());
        WorkflowStep SOP = new WorkflowStep();
        SOP.setStepNum("10");
        SOP.setNode(sopNode);
        SOP.setNextStepNumY("20");
        SOP.setNextStepNumN("60");

        //cndStep1
        WorkflowStep cndStep1 = new WorkflowStep();
        cndStep1.setCnd(true);
        cndStep1.setStepNum("20");
        cndStep1.setNode(cnd1Node);
        cndStep1.setNextStepNumY("30");
        cndStep1.setNextStepNumN("60");

        //ROP步骤
        RopWorkflowNode ropNode = new RopWorkflowNode();
        ropNode.setNodeCode("ROP");
        ropNode.setWorkflowNodeExecutable(new RopNodeExecutable());
        final WorkflowStep ROP = new WorkflowStep();
        ROP.setStepNum("30");
        ROP.setNode(ropNode);
        ROP.setNextStepNumY("40");
        ROP.setNextStepNumY("40");

        //cndStep2
        WorkflowStep cndStep2 = new WorkflowStep();
        cndStep2.setCnd(true);
        cndStep2.setStepNum("40");
        cndStep2.setNode(cnd2Node);
        cndStep2.setNextStepNumY("50");
        cndStep2.setNextStepNumN("50");

        //EOPF Step
        EopfWorkflowNode eopfNode = new EopfWorkflowNode();
        eopfNode.setNodeCode("EOPF");
        eopfNode.setWorkflowNodeExecutable(new EopfNodeExecutable());
        WorkflowStep EOPF = new WorkflowStep();
        EOPF.setStepNum("50");
        EOPF.setNode(eopfNode);
        EOPF.setNextStepNumY("40");
        EOPF.setNextStepNumN("40");

        // EOP Step
        EopWorkflowNode eopNode = new EopWorkflowNode();
        eopNode.setNodeCode("EOP");
        eopNode.setWorkflowNodeExecutable(new EopNodeExecutable());
        final WorkflowStep EOP = new WorkflowStep();
        EOP.setStepNum("60");
        EOP.setNode(eopNode);

        final Map<String, WorkflowStep> workflowSteps = new HashMap<>();
        workflowSteps.put("SOP", SOP);
        workflowSteps.put("20", cndStep1);
        workflowSteps.put("30", ROP);
        workflowSteps.put("40", cndStep2);
        workflowSteps.put("50", EOPF);
        workflowSteps.put("60", EOP);

        new Expectations(workflowTemplate){
            {
                Deencapsulation.setField(workflowTemplate, "workflowSteps",  workflowSteps);

                ThreadPoolHolder.getCndThreadPool();
                result = Executors.newFixedThreadPool(1);

                ThreadPoolHolder.getEopfThreadPool();
                result = Executors.newFixedThreadPool(1);
            }
        };

        WorkflowCode workflowCode = wins.execute();
        assertTrue(workflowCode.getValue().equalsIgnoreCase("WF0001"));
        if(workflowCode.getValue().equalsIgnoreCase("WF0001")){
            wins.continueExecute();
        }
    }

}