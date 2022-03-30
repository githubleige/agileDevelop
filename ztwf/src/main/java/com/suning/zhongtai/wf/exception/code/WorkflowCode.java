package com.suning.zhongtai.wf.exception.code;

public enum WorkflowCode {
    WF0000("WF0000", "WorkFlow执行正常"),
    WF0001("WF0001", "WorkFlow提前返回"),
    WF1000("WF1000", "业务节点运行异常"),
    WF1001("WF1001", "业务钩子运行异常"),
    WF1002("WF1002", "业务钩子(i_s_h)执行失败"),
    WF2000("WF2000", "WorkFlow内部异常"),
    WF2001("WF2001", "模板不存在"),
    WF2002("WF2002", "节点不存在"),
    WF2003("WF2003", "节点类型不存在"),
    WF2004("WF2004", "WorkFlow回滚异常"),
    WF2005("WF2005", "根据节点编码找不到IWorkflowNodeExecutable实现"),
    WF2006("WF2006", "组合节点的执行类型非法"),
    WF2007("WF2007", "组合节点的子项定义非法"),
    WF2008("WF2008", "提前返回节点多次出现"),
    WF2009("WF2009", "节点编码被重复使用"),
    WF2010("WF2010", "WorkFlow初始化异常"),
    WF2011("WF2011", "WorkFlow增量生效异常，增量模板数据为空"),
    WF2012("WF2012", "WorkFlow增量生效异常，已导入版本与待生效版本不一致");
    private String value;
    private String desc;

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    private WorkflowCode(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
