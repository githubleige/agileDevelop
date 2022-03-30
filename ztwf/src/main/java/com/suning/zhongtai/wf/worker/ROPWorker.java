package com.suning.zhongtai.wf.worker;

import com.suning.zhongtai.wf.core.WorkflowInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 提前返回工作线程
 *
 * @author 13073189
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class ROPWorker implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ROPWorker.class);

    private WorkflowInstance wfInstance;

    public ROPWorker(WorkflowInstance wfInstance) {
        this.wfInstance = wfInstance;
    }

    @Override
    public void run() {
        try {
            wfInstance.continueExecute();
        } catch (Exception e) {
            // 记录异常日志
            String wfCode = wfInstance.getWorkflowTemplate().getWfCode();
            String wfInstanceId = wfInstance.getWfInstanceId();
            String bisnessId = wfInstance.getWorkflowDataContext().getBisnessId().toString();
            LOGGER.error("ROPWorker encountered exception，wfCode={}，wfInstanceId={}, bizId={}", wfCode, wfInstanceId, bisnessId, e);
        }
    }
}
