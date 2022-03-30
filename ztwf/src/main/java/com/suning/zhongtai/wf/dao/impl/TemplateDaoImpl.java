package com.suning.zhongtai.wf.dao.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.suning.framework.dal.client.DalClient;
import com.suning.zhongtai.wf.dao.TemplateDao;
import com.suning.zhongtai.wf.dao.entity.WfCndNodeItem;
import com.suning.zhongtai.wf.dao.entity.WfDefinition;
import com.suning.zhongtai.wf.dao.entity.WfNode;
import com.suning.zhongtai.wf.dao.entity.WfStep;
import com.suning.zhongtai.wf.dao.rowmapper.WfCndItemRowMapper;
import com.suning.zhongtai.wf.dao.rowmapper.WfDefinitionRowMapper;
import com.suning.zhongtai.wf.dao.rowmapper.WfNodeRowMapper;
import com.suning.zhongtai.wf.dao.rowmapper.WfStepRowMapper;

/**
 * 流程模板数据库访问接口实现
 *
 * @author 13073189
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Repository
public class TemplateDaoImpl implements TemplateDao {
    
    @Resource(name = "wfDalClient")
    private DalClient wfDalClient;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<WfDefinition> queryWfTemplatesDefinition(Map<String, Object> paramMap) {
        return wfDalClient.queryForList("workflow.queryWfTemplates", paramMap, new WfDefinitionRowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WfStep> queryWfStepsDefinition(Map<String, Object> paramMap) {
        return wfDalClient.queryForList("workflow.queryWfSteps", paramMap, new WfStepRowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WfNode> queryWfNodesDefinition(Map<String, Object> paramMap) {
        return wfDalClient.queryForList("workflow.queryWfNodes", paramMap, new WfNodeRowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WfCndNodeItem> queryWfCndNodeItems(Map<String, Object> paramMap) {
        return wfDalClient.queryForList("workflow.queryWfCndNodeItems", paramMap, new WfCndItemRowMapper());
    }
}
