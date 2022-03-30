package com.suning.zhongtai.wf.dao;

import com.suning.zhongtai.wf.dao.entity.*;

import java.util.List;
import java.util.Map;

/**
 * 
 * 增量加载数据库访问接口
 *
 * @author 18040994
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface HotloadDao {
    
    /**
     * 功能描述: 查询导入结果
     */
    List<WfImportResult> queryImportResult(Map<String, Object> paramMap);

    /**
     * 功能描述: 插入导入结果
     */
    int insertImportResult(Map<String, Object> paramMap);

    /**
     * 功能描述: 更新导入结果
     */
    int updateImportResult(Map<String, Object> paramMap);

    /**
     * 功能描述: 删除导入结果
     */
    int deleteImportResult(Map<String, Object> paramMap);

    /**
     * 功能描述: 查询生效结果
     */
    List<WfEffectiveResult> queryEffectiveResult(Map<String, Object> paramMap);

    /**
     * 功能描述: 插入导入结果
     */
    int insertEffectiveResult(Map<String, Object> paramMap);

    /**
     * 功能描述: 更新生效结果
     */
    int updateEffectiveResult(Map<String, Object> paramMap);

    /**
     * 功能描述: 删除导入结果
     */
    int deleteEffectiveResult(Map<String, Object> paramMap);

    /**
     * 功能描述: 增量节点信息
     */
    List<WfNode> queryZtwfmtNodes(Map<String, Object> paramMap);

    /**
     * 功能描述: 增量模板信息
     */
    List<WfDefinition> queryZtwfmtTemplates(Map<String, Object> paramMap);

    /**
     * 功能描述: 查询增量节点影响的运行时模板
     */
    List<WfDefinition> queryIncrNodeAffectedTemplates(Map<String, Object> paramMap);

    /**
     * 功能描述: 查询增量步骤信息
     */
    List<WfStep> queryZtwfmtSteps(Map<String, Object> paramMap);

    /**
     * 功能描述: 查询增量组合节点子项信息
     */
    List<WfCndNodeItem> queryZtwfmtCndNodeItems(Map<String, Object> paramMap);
}
