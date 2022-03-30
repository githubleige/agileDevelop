package com.suning.zhongtai.wf.cfg;

import java.io.Serializable;

/**
 * 流程引擎SCM配置节点Bean
 * @author: 18040994
 * @date: 2018/12/10 14:29
 */
public class WFConfigNodeBean implements Serializable {

    private static final long serialVersionUID = -522438341572418751L;

    //流程回滚管理接口执行回滚最小线程数
    private int eopfAsynThreadCount;

    //流程回滚管理接口执行回滚最大线程数
    private int eopfMxAsynThreadCount = -1;

    //CND 并行最小线程数
    private int cndAsynThreadCount;

    //CND 并行最大线程数
    private int cndMxAsynThreadCount = -1;

    //提前返回继续执行最小线程数
    private int ropAsynThreadCount;

    //提前返回继续执行最大线程数
    private int ropMxAsynThreadCount = -1;

    //流程引擎初始化失败处理器全类名
    private String initFailureHandlerClazzName = "";

    //流程引擎热加载触发KEY
    private String hotReload = "";

    /**
     * 获取获取回滚异步处理最小线程数
     */
    public int getEopfAsynThreadCount() {
        return eopfAsynThreadCount;
    }

    /**
     * 设置提前返回异步处理最小线程数
     */
    public void setEopfAsynThreadCount(int eopfAsynThreadCount) {
        this.eopfAsynThreadCount = eopfAsynThreadCount;
    }

    /**
     * 获取CND节点异步处理最小线程数
     */
    public int getCndAsynThreadCount() {
        return cndAsynThreadCount;
    }

    /**
     * 设置提前返回异步处理最小线程数
     */
    public void setCndAsynThreadCount(int cndAsynThreadCount) {
        this.cndAsynThreadCount = cndAsynThreadCount;
    }

    /**
     * 获取提前返回异步处理最小线程数
     */
    public int getRopAsynThreadCount() {
        return ropAsynThreadCount;
    }

    /**
     * 设置提前返回异步处理最小线程数
     */
    public void setRopAsynThreadCount(int ropAsynThreadCount) {
        this.ropAsynThreadCount = ropAsynThreadCount;
    }

    public int getEopfMxAsynThreadCount() {
        return eopfMxAsynThreadCount;
    }

    public void setEopfMxAsynThreadCount(int eopfMxAsynThreadCount) {
        this.eopfMxAsynThreadCount = eopfMxAsynThreadCount;
    }

    public int getCndMxAsynThreadCount() {
        return cndMxAsynThreadCount;
    }

    public void setCndMxAsynThreadCount(int cndMxAsynThreadCount) {
        this.cndMxAsynThreadCount = cndMxAsynThreadCount;
    }

    public int getRopMxAsynThreadCount() {
        return ropMxAsynThreadCount;
    }

    public void setRopMxAsynThreadCount(int ropMxAsynThreadCount) {
        this.ropMxAsynThreadCount = ropMxAsynThreadCount;
    }

    public String getInitFailureHandlerClazzName() {
        return initFailureHandlerClazzName;
    }

    public void setInitFailureHandlerClazzName(String initFailureHandlerClazzName) {
        this.initFailureHandlerClazzName = initFailureHandlerClazzName;
    }

    public String getHotReload() {
        return hotReload;
    }

    public void setHotReload(String hotReload) {
        this.hotReload = hotReload;
    }
}
