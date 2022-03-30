package com.suning.zhongtai.wf.bootstrap;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.suning.zhongtai.wf.core.WorkflowLoadErrorHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.AbstractApplicationContext;

import com.suning.zhongtai.wf.annotation.WorkflowNode;
import com.suning.zhongtai.wf.core.IWorkflowEngineInitializer;
import com.suning.zhongtai.wf.core.step.executable.IWorkflowNodeExecutable;
import com.suning.zhongtai.wf.exception.WorkflowException;
import com.suning.zhongtai.wf.exception.code.WorkflowCode;
import com.suning.zhongtai.wf.tool.WFApplicationContextUtil;

/**
 * 
 * 监听容器启动，加载流程模板
 *
 *这个类实现了对ApplicationContextEvent事件的监听，ApplicationContextEvent事件包括：
 * ContextRefreshedEvent、ContextClosedEvent、ContextStartedEvent、ContextStoppedEvent
 * 这四个任意一个事件触发都会触发onApplicationEvent方法
 * @author 13073189
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 * 这个类虽然没有注释，但是在xml文件中有配置
 */
public class WorkflowEngineInitListener
        implements ApplicationContextAware, ApplicationListener<ApplicationContextEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowEngineInitListener.class);
    //这是一个map,map的key节点@WorkflowNode(nodeCode = "ND_SO_CHK_MN")中的nodeCode属性值，
    //Object就是bean对应的节点对象
    public static final Map<String, Object> EXECUTABLES = new HashMap<>();

    private ApplicationContext context;

    private IWorkflowInitFailureHandler initFailureHandler = null;
    //获取到spring的容器
    @Override
    public void setApplicationContext(ApplicationContext appContext) {
        this.context = appContext;
    }

    //
    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        if (event.getApplicationContext() != this.context) {
            LOGGER.debug("Received a context refreshed event from another application context {}, ignoring it",
                    event.getApplicationContext());
            LOGGER.trace("Leaving onApplicationEvent");
            return;
        }

        //ContextRefreshedEvent是ApplicationContext 被初始化或刷新时，该事件被触发。这也可以在 ConfigurableApplicationContext接口中使用
        // refresh() 方法来发生。此处的初始化是指：所有的Bean被成功装载，后处理Bean被检测并激活，所有Singleton Bean
        // 被预实例化，ApplicationContext容器已就绪可用
        if ((event instanceof ContextRefreshedEvent)) {
            if(null == initFailureHandler){
                //流程引擎初始化失败处理策略
                initFailureHandler = WorkflowLoadErrorHandlerFactory.getWorkflowInitFailureHandler();
            }
            //allBeans是一个map，key是bean的name,value是对应的bean对象
            Map<String, Object> allBeans = getAllBeans(event.getApplicationContext());
            //加载管理基础节点
            for (Map.Entry<String, Object> entry : allBeans.entrySet()) {
                String beanName = (String) entry.getKey();
                Object bean = entry.getValue();
                try {
                    //返回动态生成的对象的class对象（防止使用动态代理,这样去获取原生类）
                    //获取原生类的主要作用就是获取上面的注解，但是因为代理毕竟是targetClass的子类，所以我们可以把这个注解定义为可以继承的就可以
                    //完完全全没有必要像这样麻烦的一个一个的找
                    Class targetClass = AopProxyUtils.ultimateTargetClass(bean);
                    if (targetClass == null) {
                        LOGGER.warn("Can not get targetClass of bean " + beanName);
                    } else {
                        //执行初始化EXECUTABLES这个属性，是一个map对象
                        //EXECUTABLES.put(key, bean);,key节点@WorkflowNode(nodeCode = "ND_SO_CHK_MN")中的
                        //nodeCode属性值，bean就是对应的节点对象,targetClass主要是用来获取注释的
                        loadExecutables(bean, targetClass);
                    }
                }  catch (WorkflowException e) {
                    initFailureHandler.processFailure(e);
                }catch (Exception e) {
                    initFailureHandler.processFailure(new WorkflowException(WorkflowCode.WF2010, "流程引擎加载节点异常", e));
                }
            }

            // 全量初始化流程模板到jvm缓存
            try {
                long begin = System.currentTimeMillis();
                LOGGER.info("开始加载流程引擎...");
                //获取WorkflowEngineInitializer类对象
                IWorkflowEngineInitializer workflowEngineInitializer = WFApplicationContextUtil
                        .getBean(IWorkflowEngineInitializer.class);
                //加载模板
                workflowEngineInitializer.loadTemplates();
                LOGGER.info("流程引擎加载成功,耗时：" + (System.currentTimeMillis() - begin) + "ms");
            } catch (WorkflowException e) {
                LOGGER.error("流程引擎加载异常", e);
                initFailureHandler.processFailure(e);
            } catch (Exception e) {
                LOGGER.error("流程引擎加载出现未知异常", e);
                initFailureHandler.processFailure(new WorkflowException(WorkflowCode.WF2010, "流程引擎加载模板异常", e));
            }
        }
    }

    //存在优化的可能，因为这里是遍历spring容器中所有的bean，但是只有@WorkflowNode注释是有用的
    //下面的代码完全没有必要遍历注解
    private void loadExecutables(Object bean, Class<?> targetClass) {
//        String key=targetClass.getAnnotation(WorkflowNode.class).nodeCode();

        Annotation[] annotations = targetClass.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if ((annotation instanceof WorkflowNode)) {
                if (!(bean instanceof IWorkflowNodeExecutable)) {
                    throw new WorkflowException(WorkflowCode.WF2005,
                            bean.getClass().getName() + "不是IWorkflowNodeExecutable类型");
                }
                String key = ((WorkflowNode) annotation).nodeCode();
                EXECUTABLES.put(key, bean);
            }
        }
    }

    public static Map<String, Object> getAllBeans(ApplicationContext ctx) {
        Map<String, Object> beans = new LinkedHashMap<>();
        //Spring 容器中所有 JavaBean 的名称，返回类型是一个字符串数组。
        String[] all = ctx.getBeanDefinitionNames();
        //获取bean的容器
        BeanFactory factory = ((AbstractApplicationContext) ctx).getBeanFactory();
        for (String name : all){
            try {
                Object obj = factory.getBean(name);
                if (obj != null){
                    beans.put(name, obj);
                }
            } catch (BeansException e) {
            }
        }
        return beans;
    }
}
