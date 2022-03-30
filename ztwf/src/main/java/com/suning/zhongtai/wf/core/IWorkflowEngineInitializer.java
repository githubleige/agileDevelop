package com.suning.zhongtai.wf.core;

/**
 * 流程模板初始化接口
 *
 * @author 13073189
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface IWorkflowEngineInitializer {
    /**
     * 
     * 功能描述: 全量加载流程模板
     *在开始过程中一般只会在初始化加载内存（不会出现实时更改，实时将更改刷新到内存）
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    void loadTemplates();

    /**
    * 功能描述: 根据版本号加载增量流程模板
     * 实时更改，实时将更改刷新到内存，类似于zookeeper的机制
    * @param wfVersonNum
    * @return
    * @author 18040994
    * @date 2019/9/15 18:34
    */
    void hotLoadTemplate(String wfVersonNum);
}
