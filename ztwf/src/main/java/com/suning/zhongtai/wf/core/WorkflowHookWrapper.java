package com.suning.zhongtai.wf.core;

import com.suning.zhongtai.wf.core.context.WorkflowHookContext;

public class WorkflowHookWrapper {
    //钩子代码运行是否影响主流程运行（如果钩子函数执行失败了并且isAffect为true那么主流程直接返回
    //
    // WFReturnValue rv = new WFReturnValue(false,……);返回失败的rv）
    private boolean isAffect;
    //钩子
     private WorkflowHook hook;
     //钩子类型
    private String hookType;

     public WorkflowHookWrapper(WorkflowHook hook, boolean isAffect, String hookType){
         this.isAffect = isAffect;
         this.hook = hook;
         this.hookType = hookType;
     }

     //执行钩子
     public int runHook(WorkflowHookContext hookContext){
         return hook.execute(hookContext);
     }

    public boolean isAffect() {
        return isAffect;
    }

    public void setAffect(boolean affect) {
        isAffect = affect;
    }

    public WorkflowHook getHook() {
        return hook;
    }

    public void setHook(WorkflowHook hook) {
        this.hook = hook;
    }

    public String getHookType() {
        return hookType;
    }

    public void setHookType(String hookType) {
        this.hookType = hookType;
    }
}
