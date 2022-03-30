package com.suning.zhongtai.wf.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.suning.zhongtai.wf.core.step.WorkflowStep;
import com.suning.zhongtai.wf.core.step.node.EopWorkflowNode;
import com.suning.zhongtai.wf.core.step.node.SopWorkflowNode;
import com.suning.zhongtai.wf.tool.FileUtils;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.testng.Assert.*;

public class WorkflowTemplateContainerTest {

    @Test
    public void testHotTemplatesCache() {
        String versionNum = "OMS_20190916_01";
        Map<String,WorkflowTemplate> tps = new HashMap<>();
        SopWorkflowNode sop = new SopWorkflowNode();
        EopWorkflowNode eop = new EopWorkflowNode();
        WorkflowStep s1 = new WorkflowStep();
        s1.setStepNum("SOP");
        s1.setNode(sop);
        WorkflowStep s2 = new WorkflowStep();
        s2.setStepNum("20");
        s2.setNode(eop);
        WorkflowTemplate template = new WorkflowTemplate();
        template.setWfCode("ORDER_TEST");
        template.setWfVersion(versionNum);
        template.setWfName("订单提交");
        template.setWfState(1);
        template.getWorkflowSteps().put("SOP",s1);
        template.getWorkflowSteps().put("20",s2);
        tps.put("ORDER_TEST",template);
        WorkflowTemplateContainer.hotTemplatesCache(versionNum,tps);
        String jsonString = JSON.toJSONString(tps, true);
        System.out.println(jsonString);
        Map<String, WorkflowTemplate> templateMap = JSON
                .parseObject(jsonString, new TypeReference<Map<String, WorkflowTemplate>>() {
                });
        WorkflowStep SOP = templateMap.get("ORDER_TEST").findStartStep();
        System.out.println(SOP.getStepNum());
    }

    @Test
    public void deleteFile(){
        String versionNum = "OMS_20190916_01";
        String serializefileDir = WorkflowTemplateContainer.class.getClassLoader().getResource("cache").getPath();
        FileUtils.deleteFiles(serializefileDir,versionNum);
    }
}