package com.suning.zhongtai.wf.dao.impl;

import com.suning.framework.dal.client.DalClient;
import com.suning.zhongtai.wf.dao.HotloadDao;
import com.suning.zhongtai.wf.dao.entity.*;
import com.suning.zhongtai.wf.dao.rowmapper.*;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Repository
public class HotloadDaoImpl implements HotloadDao {

    @Resource(name = "wfDalClient")
    private DalClient wfDalClient;

    @Override
    public List<WfImportResult> queryImportResult(Map<String, Object> paramMap) {
        return wfDalClient.queryForList("hotload.queryImportResult",paramMap, new WfImportRowMapper());
    }

    @Override
    public int insertImportResult(Map<String, Object> paramMap) {
        return wfDalClient.execute("hotload.insertImportResult",paramMap);
    }

    @Override
    public int updateImportResult(Map<String, Object> paramMap) {
        return wfDalClient.execute("hotload.updateImportResult",paramMap);
    }

    @Override
    public int deleteImportResult(Map<String, Object> paramMap) {
        return wfDalClient.execute("hotload.deleteImportResult",paramMap);
    }

    @Override
    public List<WfEffectiveResult> queryEffectiveResult(Map<String, Object> paramMap) {
        return wfDalClient.queryForList("hotload.queryEffectiveResult", paramMap, new WfEffectRowMapper());
    }

    @Override
    public int insertEffectiveResult(Map<String, Object> paramMap) {
        return wfDalClient.execute("hotload.insertEffectiveResult",paramMap);
    }

    @Override
    public int updateEffectiveResult(Map<String, Object> paramMap) {
        return wfDalClient.execute("hotload.updateEffectiveResult",paramMap);
    }

    @Override
    public int deleteEffectiveResult(Map<String, Object> paramMap) {
        return wfDalClient.execute("hotload.deleteEffectiveResult",paramMap);
    }

    @Override
    public List<WfNode> queryZtwfmtNodes(Map<String, Object> paramMap) {
        return wfDalClient.queryForList("hotload.queryZtwfmtNodes", paramMap, new WfNodeRowMapper());
    }

    @Override
    public List<WfDefinition> queryZtwfmtTemplates(Map<String, Object> paramMap) {
        return wfDalClient.queryForList("hotload.queryZtwfmtTemplates", paramMap, new WfDefinitionRowMapper());
    }

    @Override
    public List<WfDefinition> queryIncrNodeAffectedTemplates(Map<String, Object> paramMap) {
        return wfDalClient.queryForList("hotload.queryIncrNodeAffectedTemplates", paramMap, new WfDefinitionRowMapper());
    }

    @Override
    public List<WfStep> queryZtwfmtSteps(Map<String, Object> paramMap) {
        return wfDalClient.queryForList("hotload.queryZtwfmtSteps", paramMap, new WfStepRowMapper());
    }

    @Override
    public List<WfCndNodeItem> queryZtwfmtCndNodeItems(Map<String, Object> paramMap) {
        return wfDalClient.queryForList("hotload.queryZtwfmtCndNodeItems", paramMap, new WfCndItemRowMapper());
    }
}
