/*
 * Copyright (C), 2002-2017, 苏宁易购电子商务有限公司
 */
package com.suning.zhongtai.wf.tool;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring applicationContext工具类<br>
 * @author 13073189
 * @version 2018-12-18 下午3:39:48
 */
@Component
public class WFApplicationContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        set(applicationContext);
    }

    private synchronized void set(ApplicationContext applicationContext) {

        WFApplicationContextUtil.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> requiredType) {

        return applicationContext.getBean(requiredType);
    }
}
