package com.suning.zhongtai.wf.tool;

import com.suning.zhongtai.wf.constant.WorkflowConstant;
import com.suning.zhongtai.wf.core.WorkflowTemplate;
import com.suning.zhongtai.wf.core.step.WorkflowStep;
import com.suning.zhongtai.wf.exception.WorkflowException;
import com.suning.zhongtai.wf.exception.code.WorkflowCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 模板合法性检查工具
 *
 * @author 13073189
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public final class TemplateUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateUtil.class);
    /**
     * 遍历每个模板，检查模板及节点合法性，保证运行时的正确性
     * @param workflowTemplates 模板集合
     */
    public static void templatesValidate(Map<String, WorkflowTemplate> workflowTemplates) {
        Set<Entry<String, WorkflowTemplate>> entrySet = workflowTemplates.entrySet();
        Iterator<Entry<String, WorkflowTemplate>> iterator = entrySet.iterator();
        // 依次检查每个模板
        while (iterator.hasNext()) {
            Entry<String, WorkflowTemplate> entry = iterator.next();
            //获取流程编码（例如P_ORD_ACL_HDL_PGS）
            String templateCode = entry.getKey();
            //获取流程编码对应的执行模板
            WorkflowTemplate template = entry.getValue();
            //开始校验
            templateValidate(templateCode, template);
        }
    }

    /**
     * 模板校验
     * @param templateCode  模板编码
     * @param template  流程模板
     */
    public static void templateValidate(String templateCode, WorkflowTemplate template) {
        Map<String, WorkflowStep> steps = template.getWorkflowSteps();
        Set<Entry<String, WorkflowStep>> stepsSet = steps.entrySet();
        Iterator<Entry<String, WorkflowStep>> stepsIterator = stepsSet.iterator();

        // 检查项：开始节点是否存在
        if (!steps.containsKey(WorkflowConstant.WORKFLOW_NODE_CODE_SOP)) {
            LOGGER.error("流程模板" + templateCode + "开始节点（SOP）未定义");
            throw new WorkflowException(WorkflowCode.WF2002, "流程模板" + templateCode + "开始节点（SOP）未定义");
        }
        boolean hasEopNode = false;
        while (stepsIterator.hasNext()) {
            Entry<String, WorkflowStep> stepEntry = stepsIterator.next();
            WorkflowStep step = stepEntry.getValue();
            
            String nodeCode = step.getNode().getNodeCode();
            if (WorkflowConstant.WORKFLOW_NODE_CODE_EOP.equals(nodeCode)
                    || WorkflowConstant.WORKFLOW_NODE_CODE_EOPF.equals(nodeCode)) {
                // 结束节点是否存在
                hasEopNode = true;
                continue;
            }

            // 检查项：节点的Y出口是否存在
            if (!steps.containsKey(step.getNextStepNumY())) {
                LOGGER.error("流程模板" + templateCode + "节点" + nodeCode + "的Y后继节点不存在");
                throw new WorkflowException(WorkflowCode.WF2002,
                        "流程模板" + templateCode + "节点" + nodeCode + "的Y后继节点不存在");
            }
            // 检查项：节点的N出口是否存在
            if (!steps.containsKey(step.getNextStepNumN())) {
                LOGGER.error("流程模板" + templateCode + "节点" + nodeCode + "的N后继节点不存在");
                throw new WorkflowException(WorkflowCode.WF2002,
                        "流程模板" + templateCode + "节点" + nodeCode + "的N后继节点不存在");
            }
        }

        // 检查项：结束节点是否存在
        if (!hasEopNode) {
            LOGGER.error("流程模板" + templateCode + "结束节点不存在");
            throw new WorkflowException(WorkflowCode.WF2002, "流程模板" + templateCode + "结束节点不存在");
        }
    }
     
}
