package com.suning.zhongtai.wf.cfg;

import com.suning.zhongtai.wf.constant.WorkflowConstant;
import com.suning.zhongtai.wf.tool.NetUtils;
import com.suning.zhongtai.wf.tool.ScmNodeParserUtil;
import com.suning.zhongtai.wf.tool.WFApplicationContextUtil;
import com.suning.zhongtai.wf.tool.WorkFlowThreadPoolHolder;
import com.suning.zhongtai.wf.core.WorkflowHotReloadHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.regex.Matcher;

/**
 * scm配置有变化时触发此类中的方法
 * @author: 18040994
 * @date: 2018/12/10 15:56
 */
public class ScmWfConfigsEventTrigger {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScmWfConfigsEventTrigger.class);
    private ScmWfConfigsEventTrigger() {
        throw new IllegalAccessError("Utility class");
    }

    public static void trig(WFConfigNodeBean newBean,String oldValue) {
        WFConfigNodeBean oldBean = new WFConfigNodeBean();
        ScmNodeParserUtil.parseToObject(oldBean, oldValue);

        //判断回滚异步处理线程数是否做了修改
        int eopfMsThreadCountNew = newBean.getEopfAsynThreadCount();
        int eopfMxThreadCountNew = newBean.getEopfMxAsynThreadCount();
        int eopfMsThreadCountOld = oldBean.getEopfAsynThreadCount();
        int eopfMxThreadCountOld = oldBean.getEopfMxAsynThreadCount();
        if(eopfMsThreadCountNew != eopfMsThreadCountOld||eopfMxThreadCountNew!=eopfMxThreadCountOld) {
            LOGGER.info("回滚异步处理线程数发生修改，oldMsValue={}, newMsValue={}, oldMxValue={}, newMxValue={}",
                    eopfMsThreadCountOld, eopfMsThreadCountNew,eopfMxThreadCountOld, eopfMxThreadCountNew);
            WorkFlowThreadPoolHolder.changeEopfAsynThreadCount(eopfMsThreadCountNew,eopfMxThreadCountNew);
        }
        //判断组合节点并行处理线程数是否做了修改
        int cndMsThreadCountNew = newBean.getCndAsynThreadCount();
        int cndMxThreadCountNew = newBean.getCndMxAsynThreadCount();
        int cndMsThreadCountOld = oldBean.getCndAsynThreadCount();
        int cndMxThreadCountOld = oldBean.getCndMxAsynThreadCount();
        if(cndMsThreadCountNew != cndMsThreadCountOld||cndMxThreadCountNew != cndMxThreadCountOld) {
            LOGGER.info("组合节点并行处理线程数发生修改，oldMsValue={}, newMsValue={}, oldMxValue={}, newMxValue={}",
                    cndMsThreadCountOld, cndMsThreadCountNew,cndMxThreadCountOld,cndMxThreadCountNew);
            WorkFlowThreadPoolHolder.changeCndAsynThreadCount(cndMsThreadCountNew,cndMxThreadCountNew);
        }
        //判断提前返回异步处理线程数是否做了修改
        int ropMsThreadCountNew = newBean.getRopAsynThreadCount();
        int ropMxThreadCountNew = newBean.getRopMxAsynThreadCount();
        int ropMsThreadCountOld = oldBean.getRopAsynThreadCount();
        int ropMxThreadCountOld = oldBean.getRopMxAsynThreadCount();
        if(ropMsThreadCountNew != ropMsThreadCountOld||ropMxThreadCountNew != ropMxThreadCountOld) {
            LOGGER.info("提前返回异步处理线程数发生修改，oldMsValue={}, newMsValue={},oldMxValue={}, newMxValue={}",
                    ropMsThreadCountOld, ropMsThreadCountNew,ropMxThreadCountOld,ropMxThreadCountNew);
            WorkFlowThreadPoolHolder.changeRopAsynThreadCount(ropMsThreadCountNew,ropMxThreadCountNew);
        }
        //判断热加载KEY是否做了修改
        String hotReloadNew = newBean.getHotReload();
        String hotReloadOld = oldBean.getHotReload();
        Matcher matcher = WorkflowConstant.HOTRELOAD_PATTERN.matcher(hotReloadNew);
        if(!hotReloadNew.equals(hotReloadOld)&&matcher.find()){
            LOGGER.info("触发增量加载，hotReloadOld={}, hotReloadNew={}",hotReloadOld,hotReloadNew);
            WorkflowHotReloadHelper hotReloadHelper = WFApplicationContextUtil.getBean(WorkflowHotReloadHelper.class);
            String hotFlag = hotReloadNew.split("&")[2];
            if(WorkflowConstant.DATA_IMPORT.equals(hotFlag)){
                //导入
                hotReloadHelper.hotReload();
            }
            if(WorkflowConstant.TAKE_EFFECT.equals(hotFlag)){
                String middle = hotReloadNew.split("&")[1];
                if(NetUtils.ipCheck(middle)){
                    //生效指定服务器
                    hotReloadHelper.takeEffect(middle);
                }
                else{
                    hotReloadHelper.takeEffect();
                }
            }
        }
    }
}
