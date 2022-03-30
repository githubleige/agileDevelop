package com.suning.zhongtai.wf.dao;

import java.util.List;
import java.util.Map;

import com.suning.zhongtai.wf.dao.entity.WfCndNodeItem;
import com.suning.zhongtai.wf.dao.entity.WfDefinition;
import com.suning.zhongtai.wf.dao.entity.WfNode;
import com.suning.zhongtai.wf.dao.entity.WfStep;

/**
 * 
 * 流程模板数据库访问接口
 *
 * @author 13073189
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface TemplateDao {
    
    /**
     * 
     * 功能描述: 查询生效的流程模板定义信息
     *
     * @param paramMap
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    List<WfDefinition> queryWfTemplatesDefinition(Map<String, Object> paramMap);
    
    /**
     * 
     * 功能描述: 查询流程步骤定义信息
     *
     * @param paramMap 流程模板ids
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    List<WfStep> queryWfStepsDefinition(Map<String, Object> paramMap);
    
    /**
     * 
     * 功能描述: 查询启用的流程节点定义信息
     *
     * @param paramMap
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    List<WfNode> queryWfNodesDefinition(Map<String, Object> paramMap);
    
    /**
     * 
     * 功能描述: 查询组合节点子项信息
     *
     * @param paramMap
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    List<WfCndNodeItem> queryWfCndNodeItems(Map<String, Object> paramMap);
  
}
