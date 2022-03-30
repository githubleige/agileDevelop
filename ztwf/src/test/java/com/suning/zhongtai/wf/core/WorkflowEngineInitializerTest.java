package com.suning.zhongtai.wf.core;

import com.suning.zhongtai.wf.dao.TemplateDao;
import com.suning.zhongtai.wf.dao.entity.WfCndNodeItem;
import com.suning.zhongtai.wf.dao.entity.WfDefinition;
import com.suning.zhongtai.wf.dao.entity.WfNode;
import com.suning.zhongtai.wf.dao.entity.WfStep;
import com.suning.zhongtai.wf.exception.WorkflowException;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * WorkflowEngineInitializer测试用例
 *
 * @author 13073189
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class WorkflowEngineInitializerTest {
    @Tested
    private WorkflowEngineInitializer workflowEngineInitializer;

    @Injectable
    private WorkflowTemplateContainer templateContainer;

    @Injectable
    private TemplateDao templateDao;

    //正常模板构造测试
    @Test
    public void loadTemplates() {
        // 一个模板
        final List<WfDefinition> wfDefinitions = getWfDefinitions();
        // 3个步骤
        final List<WfStep> wfSteps = getWfSteps();
        // 5个节点
        final List<WfNode> wfNodes = getWfNodes();
        // 2个子项
        final List<WfCndNodeItem> cndNodeItems = getWfCndNodeItems();
        
        new Expectations(WorkflowTemplateContainer.class) {
            {
                templateDao.queryWfTemplatesDefinition((Map<String, Object>) any);
                result = wfDefinitions;
                templateDao.queryWfStepsDefinition((Map<String, Object>) any);
                result = wfSteps;
                templateDao.queryWfNodesDefinition((Map<String, Object>) any);
                result = wfNodes;
                templateDao.queryWfCndNodeItems((Map<String, Object>) any);
                result = cndNodeItems;
            }
        };
        workflowEngineInitializer.loadTemplates();
        new Verifications() {
            {
                WorkflowTemplateContainer.loadTemplates((Map<String, WorkflowTemplate>) any);
                times = 1;
            }
        };
    }
    
    // 检查该cnd节点是否已定义
    @Test(expectedExceptions=WorkflowException.class)
    public void loadTemplates1() {
        // 一个模板
        final List<WfDefinition> wfDefinitions = getWfDefinitions();
        // 3个步骤
        final List<WfStep> wfSteps = getWfSteps();
        // 5个节点
        final List<WfNode> wfNodes = getWfNodes();
        wfNodes.remove(0);//删除一个组合节点
        // 2个子项
        final List<WfCndNodeItem> cndNodeItems = getWfCndNodeItems();
        cndNodeItems.add(getAWfCndNodeItem());//添加一个组合节点子项
        
        new Expectations(WorkflowTemplateContainer.class) {
            {
//                templateDao.queryWfTemplatesDefinition((Map<String, Object>) any);
//                result = wfDefinitions;
//                templateDao.queryWfStepsDefinition((Map<String, Object>) any);
//                result = wfSteps;
                templateDao.queryWfNodesDefinition((Map<String, Object>) any);
                result = wfNodes;
                templateDao.queryWfCndNodeItems((Map<String, Object>) any);
                result = cndNodeItems;
            }
        };
        workflowEngineInitializer.loadTemplates();
    }
    
    // 检查该bnd节点是否已定义
    @Test(expectedExceptions=WorkflowException.class)
    public void loadTemplates2() {
        // 一个模板
        final List<WfDefinition> wfDefinitions = getWfDefinitions();
        // 3个步骤
        final List<WfStep> wfSteps = getWfSteps();
        // 5个节点
        final List<WfNode> wfNodes = getWfNodes();
        //wfNodes.get(4).setNodeCode("EOP");
        // 2个子项
        final List<WfCndNodeItem> cndNodeItems = getWfCndNodeItems();
        cndNodeItems.get(0).setBndCode("XXX"); 
        
        new Expectations(WorkflowTemplateContainer.class) {
            {
//                templateDao.queryWfTemplatesDefinition((Map<String, Object>) any);
//                result = wfDefinitions;
//                templateDao.queryWfStepsDefinition((Map<String, Object>) any);
//                result = wfSteps;
                templateDao.queryWfNodesDefinition((Map<String, Object>) any);
                result = wfNodes;
                templateDao.queryWfCndNodeItems((Map<String, Object>) any);
                result = cndNodeItems;
            }
        };
        workflowEngineInitializer.loadTemplates();
    }
    
    // Cnd节点子项只能是基础节点
    @Test(expectedExceptions=WorkflowException.class)
    public void loadTemplates3() {
        // 一个模板
        final List<WfDefinition> wfDefinitions = getWfDefinitions();
        // 3个步骤
        final List<WfStep> wfSteps = getWfSteps();
        // 5个节点
        final List<WfNode> wfNodes = getWfNodes();
        //wfNodes.get(4).setNodeCode("EOP");
        // 2个子项
        final List<WfCndNodeItem> cndNodeItems = getWfCndNodeItems();
        cndNodeItems.get(0).setBndCode("CND_NO_GEN_B2C_POS"); 
        
        new Expectations(WorkflowTemplateContainer.class) {
            {
//                templateDao.queryWfTemplatesDefinition((Map<String, Object>) any);
//                result = wfDefinitions;
//                templateDao.queryWfStepsDefinition((Map<String, Object>) any);
//                result = wfSteps;
                templateDao.queryWfNodesDefinition((Map<String, Object>) any);
                result = wfNodes;
                templateDao.queryWfCndNodeItems((Map<String, Object>) any);
                result = cndNodeItems;
            }
        };
        workflowEngineInitializer.loadTemplates();
    }

    private List<WfCndNodeItem> getWfCndNodeItems() {
        final List<WfCndNodeItem> cndNodeItems = new ArrayList<>();
        WfCndNodeItem wfCndNodeItem = new WfCndNodeItem();
        wfCndNodeItem.setCndCode("CND_NO_GEN_B2C_POS");
        wfCndNodeItem.setBndCode("ND_NO_GEN_B2C");
        wfCndNodeItem.setStepNum(10);
        cndNodeItems.add(wfCndNodeItem);
        
        wfCndNodeItem = new WfCndNodeItem();
        wfCndNodeItem.setCndCode("CND_NO_GEN_B2C_POS");
        wfCndNodeItem.setBndCode("ND_NO_GEN_POS");
        wfCndNodeItem.setStepNum(20);
        cndNodeItems.add(wfCndNodeItem);
        return cndNodeItems;
    }
    
    private WfCndNodeItem getAWfCndNodeItem() {
        WfCndNodeItem wfCndNodeItem = new WfCndNodeItem();
        wfCndNodeItem.setCndCode("CND_INFO_SYNC_SBMT");
        wfCndNodeItem.setBndCode("ND_SYNC_OMSQ");
        wfCndNodeItem.setStepNum(10);
        return wfCndNodeItem;
    }

    private List<WfNode> getWfNodes() {
        final List<WfNode> wfNodes = new ArrayList<>();
        WfNode wfNode = new WfNode();
        wfNode.setCndCallType(0);
        wfNode.setNodeCode("CND_NO_GEN_B2C_POS");
        wfNode.setNodeType(1); 
        wfNode.setNodeName("单号生成");
        wfNode.setRollbackFlag(0);
        wfNode.setNodeState(1);
        wfNodes.add(wfNode);
        
         wfNode = new WfNode();
        wfNode.setNodeCode("ND_NO_GEN_B2C");
        wfNode.setNodeType(0); 
        wfNode.setNodeName("单号生成（B2C）");
        wfNode.setRollbackFlag(0);
        wfNode.setNodeState(1);
        wfNodes.add(wfNode);
        
        wfNode = new WfNode();
        wfNode.setNodeCode("ND_NO_GEN_POS");
        wfNode.setNodeType(0); 
        wfNode.setNodeName("单号生成（POS）");
        wfNode.setRollbackFlag(0);
        wfNode.setNodeState(1);
        wfNodes.add(wfNode);
        
        wfNode = new WfNode();
        wfNode.setNodeCode("SOP");
        wfNode.setNodeName("开始节点");
        wfNode.setNodeType(0); 
        wfNode.setRollbackFlag(0);
        wfNode.setNodeState(1);
        wfNodes.add(wfNode);
        
        wfNode = new WfNode();
        wfNode.setNodeCode("EOP");
        wfNode.setNodeName("结束节点");
        wfNode.setNodeType(0); 
        wfNode.setRollbackFlag(0);
        wfNode.setNodeState(1);
        wfNodes.add(wfNode);
        return wfNodes;
    }

    private List<WfStep> getWfSteps() {
        final List<WfStep> wfSteps = new ArrayList<>();
        WfStep wfStep = new WfStep();
        wfStep.setId(1l);
        wfStep.setWfId(1l);
        wfStep.setWfCode("P_ORD_SBMT_OLN");
        wfStep.setStepNum(10);
        wfStep.setNodeCode("SOP");
        wfStep.setNextStepY(20);
        wfStep.setNextStepN(20);
        wfSteps.add(wfStep);
        
        wfStep = new WfStep();
        wfStep.setId(2l);
        wfStep.setWfId(1l);
        wfStep.setWfCode("P_ORD_SBMT_OLN");
        wfStep.setStepNum(30);
        wfStep.setNodeCode("EOP"); 
        wfSteps.add(wfStep);
        
        wfStep = new WfStep();
        wfStep.setId(1l);
        wfStep.setWfId(1l);
        wfStep.setWfCode("P_ORD_SBMT_OLN"); 
        wfStep.setStepNum(20);
        wfStep.setNodeCode("CND_NO_GEN_B2C_POS");
        wfStep.setNextStepY(30);
        wfStep.setNextStepN(30);
        wfSteps.add(wfStep);
        return wfSteps;
    }

    private List<WfDefinition> getWfDefinitions() {
        final List<WfDefinition> wfDefinitions = new ArrayList<>();
        WfDefinition wfDefinition = new WfDefinition();
        wfDefinition.setId(1l);
        wfDefinition.setWfCode("P_ORD_SBMT_OLN");
        wfDefinition.setWfName("订单提交（在线提交）");
        wfDefinition.setWfState(1);
        wfDefinition.setWfVersion("V1");  
        wfDefinitions.add(wfDefinition);
        return wfDefinitions;
    }

}
