package com.suning.zhongtai.wf.exception;

import com.suning.zhongtai.wf.exception.code.WorkflowCode;

/**
 * workflow异常定义
 *
 * @author 13073189
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class WorkflowException extends RuntimeException {
    private static final long serialVersionUID = 1158249743914433731L;
    private WorkflowCode wfErrCode ;
    
    public WorkflowException(WorkflowCode wfErrCode, String message) {
        super(message);
        this.wfErrCode = wfErrCode;
    }

    public WorkflowException(WorkflowCode wfErrCode, String message, Throwable t) {
        super(message, t);
        this.wfErrCode = wfErrCode;
    }

    public WorkflowCode getErrorCode() {
        return wfErrCode;
    }
}
