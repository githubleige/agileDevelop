package com.suning.zhongtai.wf.annotation;

import java.lang.annotation.*;


/**
 * 流程引擎注解类，用于标记流程节点
 * @author: 18040994
 * @date: 2019/1/23 11:55
 * @Inherited注解的作用就是让他生成的子类也继承这个注解
 */
@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
//@Inherited
public @interface WorkflowNode {
    /**
     * 节点编码
     */
    public String nodeCode();
}
