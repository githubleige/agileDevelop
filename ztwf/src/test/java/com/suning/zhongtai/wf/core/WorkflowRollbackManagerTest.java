package com.suning.zhongtai.wf.core;

import com.suning.zhongtai.wf.core.context.AbstractWorkflowDataContext;
import com.suning.zhongtai.wf.core.step.WorkflowStep;
import com.suning.zhongtai.wf.core.step.executable.EopfNodeExecutable;
import com.suning.zhongtai.wf.core.step.node.StandardWorkflowNode;
import com.suning.zhongtai.wf.log.WorkflowClmLogBean;
import com.suning.zhongtai.wf.tool.ThreadPoolHolder;
import mockit.Deencapsulation;
import mockit.Expectations;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class WorkflowRollbackManagerTest {

    WorkflowRollbackManager wrm = new WorkflowRollbackManager();

    @Test
    public void testRollback() {
        class TestDataContext extends AbstractWorkflowDataContext {
            @Override
            public List<String> getBisnessId() {
                List<String> bizIds = new ArrayList<>();
                bizIds.add("666666");
                return bizIds;
            }
        }
        final AbstractWorkflowDataContext wdc = new TestDataContext();
        WorkflowClmLogBean clmLogBean = new WorkflowClmLogBean();
        wdc.setWorkflowClmLog(clmLogBean);
        new Expectations(ThreadPoolHolder.class){
            {
                ThreadPoolHolder.getEopfThreadPool();
                result = Executors.newFixedThreadPool(1);
            }
        };
        StandardWorkflowNode sNode1 = new StandardWorkflowNode();
        sNode1.setNodeCode("ND_SET_RES_FLAG_IMS_PLK");
        sNode1.setWorkflowNodeExecutable(new EopfNodeExecutable());
        WorkflowStep ws1 = new WorkflowStep();
        ws1.setStepNum("20-10");
        ws1.setNode(sNode1);
        final List<WorkflowStep> wslist = new ArrayList<>();
        wslist.add(ws1);
        StandardWorkflowNode sNode2 = new StandardWorkflowNode();
        sNode2.setNodeCode("ND_SET_RES_FLAG_SPES");
        sNode2.setWorkflowNodeExecutable(new EopfNodeExecutable());
        WorkflowStep ws2 = new WorkflowStep();
        ws2.setStepNum("20-20");
        ws2.setNode(sNode1);
        wslist.add(ws2);
        new Expectations(){
            {
                Deencapsulation.setField(wrm, "rollbackList", wslist);
            }
        };
        wrm.rollback(wdc);
    }

    @Test
    public void testAddStep() {
        wrm.addStep(new WorkflowStep());
    }
}