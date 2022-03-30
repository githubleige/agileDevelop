package com.suning.zhongtai.wf.core.step.node;

import org.testng.annotations.Test;

import com.suning.zhongtai.wf.constant.WorkflowConstant;

import junit.framework.Assert;

public class WorkflowNodeFactoryTest {

  @Test
  public void produceNode() {
      BaseWorkflowNode node = WorkflowNodeFactory.produceNode(1, "cndNode1", 1);
      Assert.assertTrue(node instanceof CndWorkflowNode);
      Assert.assertTrue(((CndWorkflowNode)node).getCndExecuteType()==1);
  }
  
  @Test
  public void produceNode1() {
      BaseWorkflowNode node = WorkflowNodeFactory.produceNode(0, WorkflowConstant.WORKFLOW_NODE_CODE_SOP, 0);
      Assert.assertTrue(node instanceof SopWorkflowNode);
  }
  
  @Test
  public void produceNode2() {
      BaseWorkflowNode node = WorkflowNodeFactory.produceNode(0, WorkflowConstant.WORKFLOW_NODE_CODE_EOP, 0);
      Assert.assertTrue(node instanceof EopWorkflowNode);
  }
  
  @Test
  public void produceNode3() {
      BaseWorkflowNode node = WorkflowNodeFactory.produceNode(0, WorkflowConstant.WORKFLOW_NODE_CODE_EOPF, 0);
      Assert.assertTrue(node instanceof EopfWorkflowNode);
  }
  
  @Test
  public void produceNode4() {
      BaseWorkflowNode node = WorkflowNodeFactory.produceNode(0, WorkflowConstant.WORKFLOW_NODE_CODE_ROP, 0);
      Assert.assertTrue(node instanceof RopWorkflowNode);
  }
  
  @Test
  public void produceNode5() {
      BaseWorkflowNode node = WorkflowNodeFactory.produceNode(0, "Standard", 0);
      Assert.assertTrue(node instanceof StandardWorkflowNode);
  }
}
