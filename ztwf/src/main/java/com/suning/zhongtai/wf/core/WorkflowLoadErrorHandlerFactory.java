package com.suning.zhongtai.wf.core;

import com.suning.zhongtai.wf.bootstrap.DefaultInitFailureHandler;
import com.suning.zhongtai.wf.bootstrap.IWorkflowInitFailureHandler;
import com.suning.zhongtai.wf.cfg.WFConfigService;
import com.suning.zhongtai.wf.tool.WFApplicationContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 流程引擎加载错误处理器工厂
 * @author 18040994
 * @date 2019/9/15 19:18
 */
public class WorkflowLoadErrorHandlerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowLoadErrorHandlerFactory.class);

    /**
    * 获取加载错误处理器
    * @param
    * @return
    * @author 18040994
    * @date 2019/9/15 19:26
    */
    public static IWorkflowInitFailureHandler getWorkflowInitFailureHandler(){
        IWorkflowInitFailureHandler initFailureHandler = new DefaultInitFailureHandler();
        //获取scm上wfConfigs配置文件的内容
        WFConfigService wfConfigService = WFApplicationContextUtil.getBean(WFConfigService.class);
        String handlerClazzName = wfConfigService.getConfig().getInitFailureHandlerClazzName();
        LOGGER.info("用户自定义流程引擎初始化失败处理器类名: "+handlerClazzName);
        if(!StringUtils.isEmpty(handlerClazzName)){
            IWorkflowInitFailureHandler handler;
            try {
                Class handlerClazz = Class.forName(handlerClazzName);
                handler = (IWorkflowInitFailureHandler)handlerClazz.newInstance();
                if(null != handler){
                    initFailureHandler = handler;
                }else{
                    LOGGER.warn("用户启用了自定义流程引擎初始化失败处理器，但是未获取到处理器类，采用系统默认初始化失败处理器");
                }
            } catch (Exception e) {
                LOGGER.warn("加载用户自定义流程引擎初始化失败处理器失败，采用系统默认初始化失败处理器",e);
            }
        }else {
            LOGGER.warn("用户启用了自定义流程引擎初始化失败处理器，但是处理器类名为空，采用系统默认初始化失败处理器");
        }

        return initFailureHandler;
    }


}
