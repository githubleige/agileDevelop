package com.suning.zhongtai.wf.core;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.suning.zhongtai.wf.cfg.WFConfigService;
import com.suning.zhongtai.wf.constant.WorkflowConstant;
import com.suning.zhongtai.wf.tool.FileUtils;
import com.suning.zhongtai.wf.tool.WFApplicationContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.suning.zhongtai.wf.exception.WorkflowException;
import com.suning.zhongtai.wf.exception.code.WorkflowCode;

/**
 * 模板容器，缓存所有有效流程模板
 * 提供根据流程模板编码获取对应模板的方法
 *
 * @author 13073189
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Component
public class WorkflowTemplateContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowTemplateContainer.class);

    // 模板集合
    private static final Map<String, WorkflowTemplate> WORKFLOW_TEMPLATES = new ConcurrentHashMap<>();

    //模板增量缓存
    private static final Map<String, Map<String, WorkflowTemplate>> WORKFLOW_TEMPLATES_HOTCACHE = new ConcurrentHashMap<>();

    /**
     * 
     * 全量方式重新加载所有模板。重新加载所有生效的模板到jvm内存，并从内存中剔除失效的模板
     *
     * @param workflowTemplates 全部生效模板
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    protected static void loadTemplates(Map<String, WorkflowTemplate> workflowTemplates) {
        LOGGER.info("开始加载模板，模板容器hashCode是："+MethodHandles.lookup().lookupClass().hashCode());
        WORKFLOW_TEMPLATES.putAll(workflowTemplates);
        LOGGER.info("开始加载模板完成，加载的模板编码如下："+workflowTemplates.keySet().toString());
    }

    /**
     * 从本地缓存加载增量模板数据
     * @param wfVersionNum 版本号
     * @return
     * @author 18040994
     * @date 2019/9/16 17:12
     */
    protected static boolean loadHotTemplateFromMemory(String wfVersionNum) {
        Map<String, WorkflowTemplate> hotWorkflowTemplateCache = getHotWorkflowTemplateCache(wfVersionNum);
        if(hotWorkflowTemplateCache == null){
            return false;
        }
        loadTemplates(hotWorkflowTemplateCache);
        return true;
    }

    /**
    * 从本地序列化文件加载增量模板数据
    * @param wfVersionNum 版本号
    * @return
    * @author 18040994
    * @date 2019/9/16 17:12
    */
    protected static boolean loadHotTemplateFromSerializedFile(String wfVersionNum) {
        boolean result =false;
        try {
            String hotCacheDir = WorkflowTemplateContainer.class.getClassLoader().getResource("cache").getPath();
            String fileName = hotCacheDir + File.separator+wfVersionNum;
            File serializeFile = new File(fileName);
            if(!serializeFile.exists()){
                LOGGER.warn(wfVersionNum+"序列化文件不存在");
                return result;
            }
            Map<String, WorkflowTemplate> hotTempatesCache = deserializeHotTemplatesCacheFromFile(wfVersionNum);
            if(hotTempatesCache.isEmpty()){
                LOGGER.warn(wfVersionNum+"序列化文件存在但是反序列化结果为空");
            }else{
                loadTemplates(hotTempatesCache);
                result = true;
                LOGGER.info("尝试从本地序列化文件获取增量数据成功，版本("+wfVersionNum+")生效完成！");
            }
        } catch (Exception e) {
            LOGGER.error("增量数据本地缓存检查异常！",e);
        }
        return result;
    }

    /**
     * 从数据库加载增量模板数据
     * @param wfVersionNum 版本号
     * @return
     * @author 18040994
     * @date 2019/9/16 17:12
     */
    protected static boolean loadHotTemplateFromDB(String wfVersionNum) {
        IWorkflowEngineInitializer workflowEngineInitializer = WFApplicationContextUtil.getBean(IWorkflowEngineInitializer.class);
        workflowEngineInitializer.hotLoadTemplate(wfVersionNum);
        Map<String, WorkflowTemplate> hotWorkflowTemplateCache = getHotWorkflowTemplateCache(wfVersionNum);
        if(hotWorkflowTemplateCache != null && !hotWorkflowTemplateCache.isEmpty()){
            loadTemplates(hotWorkflowTemplateCache);
            return true;
        }
        return false;
    }

    /**
    * 缓存增量模板
    * @param workflowTemplates 增量模板
    * @return
    * @author 18040994
    * @date 2019/9/15 19:02
    */
    protected static void hotTemplatesCache(String wfVersonNum,Map<String, WorkflowTemplate> workflowTemplates) {
        LOGGER.info("开始缓存增量模板，缓存容器hashCode是："+MethodHandles.lookup().lookupClass().hashCode());
        WORKFLOW_TEMPLATES_HOTCACHE.put(wfVersonNum,workflowTemplates);
        //持久化增量模板到本地文件
        serializeHotTemplatesCacheToFile(wfVersonNum,workflowTemplates);
        LOGGER.info("缓存增量模板完成，缓存的增量模板编码如下："+workflowTemplates.keySet().toString());
    }

    /**
    * 序列化内存中的增量模板数据到文件
    * @param wfVersonNum 增量模板版本
     * @param workflowTemplates 增量模板数据
    * @return
    * @author 18040994
    * @date 2019/9/16 16:18
    */
    private static void serializeHotTemplatesCacheToFile(String wfVersonNum,Map<String, WorkflowTemplate> workflowTemplates) {
        try {
            String hotCacheDir = WorkflowTemplateContainer.class.getClassLoader().getResource("cache").getPath();
            String fileName = hotCacheDir + File.separator+wfVersonNum;
            String jsonString = JSON.toJSONString(workflowTemplates, true);
            LOGGER.info(wfVersonNum+"增量数据序列化内容："+jsonString);
            FileUtils.writeFile(fileName,jsonString.getBytes("UTF-8"));
            File serializeFile = new File(fileName);
            if(serializeFile.exists()){
                LOGGER.info(wfVersonNum+"序列化文件成功");
            } else{
                LOGGER.warn(wfVersonNum+"序列化文件失败，文件未生成");
            }
        } catch (Exception e) {
            LOGGER.error(wfVersonNum+"增量模板数据序列化到本地文件异常", e);
        }
    }

    /**
    * 从本地文件反序列化增量模板数据到内存
    * @param wfVersonNum 增量模板数据版本
    * @return
    * @author 18040994
    * @date 2019/9/16 16:18
    */
    private static Map<String, WorkflowTemplate> deserializeHotTemplatesCacheFromFile(String wfVersonNum) {
        Map<String, WorkflowTemplate> hotTemplatesCache = new ConcurrentHashMap<>();
        try {
            String hotCacheDir = WorkflowTemplateContainer.class.getClassLoader().getResource("cache").getPath();
            String fileName = hotCacheDir + File.separator+wfVersonNum;
            String fileContent = FileUtils.readFileContent(fileName);
            LOGGER.info(wfVersonNum+"序列化文件内容，如下："+fileContent);
            hotTemplatesCache = JSON
                    .parseObject(fileContent, new TypeReference<Map<String, WorkflowTemplate>>() {
                    });
            //反序列化成功之后删除序列化文件
            FileUtils.deleteFiles(hotCacheDir,wfVersonNum);
            LOGGER.info(wfVersonNum+"文件反序列化成功");
        } catch (Exception e) {
            LOGGER.error(wfVersonNum+"从本地文件反序列化增量模板数据到内存异常", e);
        }
        return hotTemplatesCache;
    }

    /**
     * 
     * 功能描述: 根据流程模板编码获取对应的模板对象
     *
     * @param templateCode
     * @return
     * @throws WorkflowException 
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public static WorkflowTemplate getWorkflowTemplate(String templateCode) {
        WorkflowTemplate template = WORKFLOW_TEMPLATES.get(templateCode);
        if (null == template) {
            LOGGER.warn("根据模板编码" + templateCode + "找不到对应的模板, "
                    + "当前模板容器"+ MethodHandles.lookup().lookupClass().hashCode()+"中的模板编码如下："+WORKFLOW_TEMPLATES.keySet().toString());
            throw new WorkflowException(WorkflowCode.WF2001, "根据模板编码" + templateCode + "找不到对应的模板");
        }
        return template;
    }

    /**
    * 根据版本号获取增量模板信息
    * @param versionNum 版本号
    * @return
    * @author 18040994
    * @date 2019/9/16 10:19
    */
    public static Map<String, WorkflowTemplate> getHotWorkflowTemplateCache(String versionNum){
        Map<String, WorkflowTemplate> hotTemplate = WORKFLOW_TEMPLATES_HOTCACHE.get(versionNum);
        if(null == hotTemplate){
            LOGGER.warn("根据版本号" + versionNum + "找不到对应的模板增量数据, "
                    + "当前模板缓存容器"+ MethodHandles.lookup().lookupClass().hashCode()+"中的版本号如下："+WORKFLOW_TEMPLATES_HOTCACHE.keySet().toString());
        }
        return  hotTemplate;
    }

}
