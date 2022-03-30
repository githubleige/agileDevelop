package com.suning.zhongtai.wf.core;

/**
 * 流程实例id生成器接口
 * @author: 18040994
 * @date: 2019/1/23 14:41
 */
public interface WFUidGenerator {

    /**
     * Get a unique ID
     *
     * @return UID
     * @throws
     */
    String getUID();

}
