package com.suning.zhongtai.wf.core;

import com.suning.zhongtai.wf.bootstrap.IWorkflowInitFailureHandler;
import com.suning.zhongtai.wf.cfg.WFConfigService;
import com.suning.zhongtai.wf.constant.WorkflowConstant;
import com.suning.zhongtai.wf.dao.HotloadDao;
import com.suning.zhongtai.wf.dao.entity.WfEffectiveResult;
import com.suning.zhongtai.wf.exception.WorkflowException;
import com.suning.zhongtai.wf.exception.code.WorkflowCode;
import com.suning.zhongtai.wf.tool.FileUtils;
import com.suning.zhongtai.wf.tool.NetUtils;
import com.suning.zhongtai.wf.tool.WFApplicationContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * 流程引擎增量加载器
 * @author 18040994
 * @date 2019/7/26 11:57
 */
@Component
public class WorkflowHotReloadHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowHotReloadHelper.class);

    private IWorkflowInitFailureHandler initFailureHandler = null;

    @Resource
    private HotloadDao hotloadDao;

    /**
    * 将增量数据加载到内存，但不生效
    * @param
    * @return
    * @author 18040994
    * @date 2019/9/16 10:40
    */
    public void hotReload(){
        if(null == initFailureHandler){
            initFailureHandler = WorkflowLoadErrorHandlerFactory.getWorkflowInitFailureHandler();
        }
        String hotReload = getCurrentHotReLoadValue();
        LOGGER.info("当前hotReload："+hotReload);
        Matcher matcher = WorkflowConstant.HOTRELOAD_PATTERN.matcher(hotReload);
        if(matcher.find()){
            String failCause;
            //获取版本号
            String versionNum = hotReload.split("&")[0];
            LOGGER.info("增量加载版本号："+versionNum);
            //入导入结果表(预加载中)
            insertOperateResult(versionNum, WorkflowConstant.importStatus.PRELOADING.value(), WorkflowConstant.DATA_IMPORT);
            //根据版本号去加载增量模板
            IWorkflowEngineInitializer workflowEngineInitializer = WFApplicationContextUtil.getBean(IWorkflowEngineInitializer.class);
            try {
                workflowEngineInitializer.hotLoadTemplate(versionNum);
                //更新导入结果(预加载完成)
                updateOperateResult(versionNum,WorkflowConstant.importStatus.PRELOAD_SUCCCESS.value(), WorkflowConstant.DATA_IMPORT);
            }catch (WorkflowException e) {
                LOGGER.error("流程引擎增量加载异常",e);
                //更新导入结果(预加载失败)
                failCause = "版本【"+versionNum+"】增量数据加载异常："+e.getMessage();
                updateOperateResult(versionNum,WorkflowConstant.importStatus.PRELOAD_FAIL.value(), WorkflowConstant.DATA_IMPORT, failCause);
                initFailureHandler.processFailure(e);
            }catch (Exception e) {
                LOGGER.error("流程引擎增量加载未知异常",e);
                //更新导入结果(预加载失败)
                failCause = "版本【"+versionNum+"】增量数据加载出现未知异常："+e.getMessage();
                updateOperateResult(versionNum,WorkflowConstant.importStatus.PRELOAD_FAIL.value(), WorkflowConstant.DATA_IMPORT, failCause);
                initFailureHandler.processFailure(new WorkflowException(WorkflowCode.WF2010, "流程引擎增量加载异常", e));
            }
        }
    }

    /**
    * 导入结果表或生效结果表新增
    * @param versionNum 版本号
     * @param  operateType  ”1“--导入   ”2“--生效
     * @param failCause  可选参数  失败原因
    * @return
    * @author 18040994
    * @date 2019/11/9 15:03
    */
    private void insertOperateResult(String versionNum, int status, String operateType, String... failCause) {
        Map<String, Object> param = new HashMap<>();
        param.put("serverIp", NetUtils.getLocalAddress());
        param.put("versionNum", versionNum);
        param.put("status", status);
        if(failCause.length > 0){
            param.put("failCause",failCause);
        }
        else{
            param.put("failCause","");
        }
        try {
            if(WorkflowConstant.DATA_IMPORT.equals(operateType)){
                hotloadDao.insertImportResult(param);
            }
            else{
                hotloadDao.insertEffectiveResult(param);
            }
        } catch (Exception e) {
            LOGGER.error("更新导入结果表或生效结果表异常，操作版本【"+versionNum+"】，操作类型【"+operateType+"】",e);
        }
    }

    /**
     * 更新结果表或生效结果表新增
     * @param versionNum 版本号
     * @param  operateType  ”1“--导入   ”2“--生效
     * @param failCause  可选参数  失败原因
     * @return
     * @author 18040994
     * @date 2019/11/9 15:03
     */
    private void updateOperateResult(String versionNum, int status, String operateType, String... failCause) {
        Map<String, Object> param = new HashMap<>();
        param.put("serverIp", NetUtils.getLocalAddress());
        param.put("versionNum", versionNum);
        param.put("status", status);
        if(failCause.length > 0){
            param.put("failCause", failCause[0]);
        }
        try {
            if(WorkflowConstant.DATA_IMPORT.equals(operateType)){
                hotloadDao.updateImportResult(param);
            }
            else{
                hotloadDao.updateEffectiveResult(param);
            }
        } catch (Exception e) {
            LOGGER.error("更新导入结果表或生效结果表异常，操作版本【"+versionNum+"】，操作类型【"+operateType+"】",e);
        }
    }

    /**
     * 读取SCM中的热加载配置项
     * hotReload=版本号&操作时间戳&导入生效标识（1-已导入  2-已生效），
     * OMS_20190916_01&20190911193525&1
     * OMS_20190916_01&20190911193525&2
     */
    private String getCurrentHotReLoadValue() {
        WFConfigService wfConfigService = WFApplicationContextUtil.getBean(WFConfigService.class);
        return wfConfigService.getConfig().getHotReload();
    }

    /**
    * 增量数据立即生效
    * @param serverIp 指定生效的ip，可选参数
    * @return
    * @author 18040994
    * @date 2019/9/16 10:41
    */
    public void takeEffect(String... serverIp){
        if(serverIp.length > 0&&!serverIp[0].equals(NetUtils.getLocalAddress())){
            //指定生效的ip与本机不一致
            return;
        }
        if(null == initFailureHandler){
            initFailureHandler = WorkflowLoadErrorHandlerFactory.getWorkflowInitFailureHandler();
        }
        String hotReload = getCurrentHotReLoadValue();
        LOGGER.info("当前 hotReload："+hotReload);
        Matcher matcher = WorkflowConstant.HOTRELOAD_PATTERN.matcher(hotReload);
        if(matcher.find()){
            String failCause;
            String wfVersionNum = hotReload.split("&")[0];
            LOGGER.info("待生效版本号："+wfVersionNum);
            //再次判断当前机器是否已经生效完成，若已经生效，直接结束
            if(repeatTakeEffectCheck(wfVersionNum)){
                return;
            }
            insertOperateResult(wfVersionNum, WorkflowConstant.effectiveStatus.BECOMING_EFFECTIVE.value(), WorkflowConstant.TAKE_EFFECT);
            //生效成功后，删除本地缓存序列化文件
            String serializefileDir = WorkflowTemplateContainer.class.getClassLoader().getResource("cache").getPath();
            boolean success  = WorkflowTemplateContainer.loadHotTemplateFromMemory(wfVersionNum);
            if(!success){
                LOGGER.warn("根据版本号【"+wfVersionNum+"】从内存未获取到增量数据,尝试从本地序列化文件获取增量数据.....");
                success = WorkflowTemplateContainer.loadHotTemplateFromSerializedFile(wfVersionNum);
                if(!success){
                    LOGGER.warn("根据版本号【"+wfVersionNum+"】从序列化文件未获取到增量数据，尝试从数据库获取....");
                    try {
                        success = WorkflowTemplateContainer.loadHotTemplateFromDB(wfVersionNum);
                    } catch (WorkflowException e) {
                        LOGGER.error("尝试从数据库获取和生效版本【"+wfVersionNum+"】增量数据存在异常",e);
                        initFailureHandler.processFailure(e);
                        failCause = "尝试从数据库获取和生效版本【"+wfVersionNum+"】的增量数据存在异常："+e.getMessage();
                        updateOperateResult(wfVersionNum,WorkflowConstant.effectiveStatus.EFFECTIVE_FAIL.value(), WorkflowConstant.TAKE_EFFECT, failCause);
                    }catch (Exception e) {
                        LOGGER.error("尝试从数据库获取和生效版本【"+wfVersionNum+"】的增量数据存在未知异常",e);
                        initFailureHandler.processFailure(new WorkflowException(WorkflowCode.WF2010, "流程引擎增量加载异常", e));
                        failCause = "版本【"+wfVersionNum+"】尝试从数据库获取和生效增量数据存在未知异常："+e.getMessage();
                        updateOperateResult(wfVersionNum,WorkflowConstant.effectiveStatus.EFFECTIVE_FAIL.value(), WorkflowConstant.TAKE_EFFECT, failCause);
                    }
                }
            }
            if(success){
                FileUtils.deleteFiles(serializefileDir,wfVersionNum);
                updateOperateResult(wfVersionNum,WorkflowConstant.effectiveStatus.EFFECTIVE_SUCCCESS.value(), WorkflowConstant.TAKE_EFFECT);
                LOGGER.info("版本【"+wfVersionNum+"】生效完成！");
            }
        }
    }

    private boolean repeatTakeEffectCheck(String versionNum) {
        Map<String, Object> param = new HashMap<>();
        param.put("serverIp", NetUtils.getLocalAddress());
        param.put("versionNum", versionNum);
        param.put("versionStatus",WorkflowConstant.effectiveStatus.EFFECTIVE_SUCCCESS.value());
        List<WfEffectiveResult> effectiveResult = hotloadDao.queryEffectiveResult(param);
        if (!CollectionUtils.isEmpty(effectiveResult)) {
            return true;
        }
        return false;
    }
}
