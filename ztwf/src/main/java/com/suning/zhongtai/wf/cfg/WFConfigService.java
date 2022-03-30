package com.suning.zhongtai.wf.cfg;

import com.suning.framework.scm.client.SCMClient;
import com.suning.framework.scm.client.SCMClientFactory;
import com.suning.framework.scm.client.SCMListener;
import com.suning.framework.scm.client.SCMNode;
import com.suning.zhongtai.wf.tool.ScmNodeParserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 获取scm上wfConfigs配置文件的内容
 * @author: 18040994
 * @date: 2018/12/10 14:25
 */
@Component
public class WFConfigService {

    SCMClient scmClient = null;
    private String path = "wfConfigs"; // SCM配置文件节点名
    private SCMNode node = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(WFConfigService.class);

    private static WFConfigNodeBean scmConfigNode = new WFConfigNodeBean();// 配置文件内容

    /**
     * 取得配置文件内容
     * @return  WFConfigNodeBean
     */
    @PostConstruct
    public WFConfigNodeBean getConfig() {
        synchronized (scmConfigNode) {
            // 第一次访问时
            if (null == node) {
                scmClient = SCMClientFactory.getSCMClient();
                node = scmClient.getConfig(path);
                node.sync();
                ScmNodeParserUtil.parseToObject(scmConfigNode, node.getValue());
                // 监听变化
                node.monitor(node.getValue(), new SCMListener() {
                    @Override
                    public void execute(String oldValue, String newValue) {
                        LOGGER.info("========SCM配置 wfConfigs change==========");
                        ScmNodeParserUtil.parseToObject(scmConfigNode, newValue);
                        ScmWfConfigsEventTrigger.trig(scmConfigNode, oldValue);
                    }
                });
            }
        }

        return scmConfigNode;
    }

    @PreDestroy
    public void destroyResource() {
        if (null != node) {
            node.destroy();
        }
        if (null != scmClient) {
            scmClient.destroy();
        }
    }
}
