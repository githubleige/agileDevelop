package com.suning.zhongtai.wf.core;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * UUID生成器
 * @author: 18040994
 * @date: 2019/1/23 14:40
 */
@Component
public class UuidGenerator implements WFUidGenerator {
    @Override
    public String getUID() {
        return UUID.randomUUID().toString();
    }

}
