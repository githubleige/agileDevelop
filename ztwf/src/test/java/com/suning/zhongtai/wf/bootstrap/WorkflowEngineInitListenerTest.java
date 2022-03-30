package com.suning.zhongtai.wf.bootstrap;

import com.suning.zhongtai.wf.core.IWorkflowEngineInitializer;
import com.suning.zhongtai.wf.exception.WorkflowException;
import com.suning.zhongtai.wf.exception.code.WorkflowCode;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import org.springframework.context.event.ContextRefreshedEvent;
import org.testng.annotations.Test;

public class WorkflowEngineInitListenerTest {

    @Tested
    WorkflowEngineInitListener workflowEngineInitListener;

    @Injectable
    IWorkflowEngineInitializer workflowEngineInitializer;
    
    //WorkflowException异常测试
    @Test(expectedExceptions = WorkflowException.class,expectedExceptionsMessageRegExp="MESSAGE")
    public void onApplicationEvent(final @Mocked ContextRefreshedEvent contextRefreshedEvent) {
        new Expectations() {
            {
                contextRefreshedEvent.getApplicationContext().getParent();
                result = null;
                
                workflowEngineInitializer.loadTemplates();
                result = new WorkflowException(WorkflowCode.WF2000 ,"MESSAGE");
            }
        };
        workflowEngineInitListener.onApplicationEvent(contextRefreshedEvent);
    }
    
    //其它异常测试
    @Test(expectedExceptions = RuntimeException.class,expectedExceptionsMessageRegExp="RunTimeMESSAGE")
    public void onApplicationEvent1(final @Mocked ContextRefreshedEvent contextRefreshedEvent) {
        new Expectations() {
            {
                contextRefreshedEvent.getApplicationContext().getParent();
                result = null;
                
                workflowEngineInitializer.loadTemplates();
                result = new RuntimeException("RunTimeMESSAGE");
            }
        };
        workflowEngineInitListener.onApplicationEvent(contextRefreshedEvent);
    }
}
