package com.suning.zhongtai.wf.constant;

import java.util.regex.Pattern;

/**
 * 流程常量类
 */
public class WorkflowConstant {

    /**
     * 1 就是Y (成功)
     */
    public static final int WORKFLOW_FLAG_Y = 1;

    /**
     * 0 就是N (失败或异常)
     */
    public static final int WORKFLOW_FLAG_N = 0;

    /**
     * 节点类型--标准节点
     */
    public static final int WORKFLOW_NODE_TYPE_BND = 0;

    /**
     * 节点类型--组合节点
     */
    public static final int WORKFLOW_NODE_TYPE_CND = 1;
    
    /**
     * 节点类型--组合节点
     */
    public static final String WORKFLOW_NODE_TYPE_CND_STR = "CND";

    /**
     * 节点编码--提前返回节点
     */
    public static final String WORKFLOW_NODE_CODE_ROP = "ROP";

    /**
     * 节点编码--结束节点(不回滚)
     */
    public static final String WORKFLOW_NODE_CODE_EOP = "EOP";

    /**
     * 节点编码--结束节点(回滚)
     */
    public static final String WORKFLOW_NODE_CODE_EOPF = "EOPF";

    /**
     * 节点编码--开始节点(回滚)
     */
    public static final String WORKFLOW_NODE_CODE_SOP = "SOP";

    /**
     * 节点状态--禁用
     */
    public static final int WORKFLOW_NODE_STATUS_DISABLE = 0;

    /**
     * 节点状态--启用
     */
    public static final int WORKFLOW_NODE_STATUS_ENABLE = 1;
    
    /**
     * 节点回滚标识：1支持回滚
     */
    public static final int WORKFLOW_NODE_ROLLBACK_FLAG = 1;

    /**
     * 组合节点执行类型--串行
     */
    public static final int WORKFLOW_CND_EXECUTE_TYPE_SERL = 0;

    /**
     * 组合节点执行类型--并行
     */
    public static final int WORKFLOW_CND_EXECUTE_TYPE_PARL = 1;

    public static final String STEP_SEPARATOR = "-";

    public static final String COMMA_SEPARATOR = ",";

    /**
     * CLM日志类型
     */
    public static final String WORKFLOW_CLM_LOG_TYPE = "workflow";
    /**
     * CLM日志类
     */
    public static final String WORKFLOW_CLM_LOG_MONITOR = "workflowLogMonitor";

    /**
     * 钩子执行失败
     */
    public static final int WORKFLOW_HOOK_RUN_FAILED= 0;

    /**
     * 钩子执行成功
     */
    public static final int WORKFLOW_HOOK_RUN_SUCCESS= 1;

    /**
     * 实例启动钩子标识
     */
    public static final String WORKFLOW_INSTANCE_START = "i_s_h";

    /**
     * 实例主线程结束钩子标识
     */
    public static final String WORKFLOW_MAIN_THREAD_END = "i_r_h";

    /**
     * 实例结束钩子标识
     */
    public static final String WORKFLOW_INSTANCE_END = "i_f_h";

    /**
     * 实例正常结束标识
     */
    public static final int WORKFLOW_NORMAL_END = 1;

    /**
     * 实例异常结束标识
     */
    public static final int WORKFLOW_ABNORMAL_END = 0;

    /**
     * 实例运行中
     */
    public static final int WORKFLOW_RUNING = -1;

    /**
     * 增量加载配置项Pattern
     */
    public static final Pattern HOTRELOAD_PATTERN = Pattern.compile("\\w_\\d{8}_\\d{2,4}&\\d{14}|\\d{1,3}(\\.\\d{1,3}){3,5}&[12]");

    /**
     * 增量数据导入标识
     */
    public static final String DATA_IMPORT = "1";

    /**
     * 增量数据生效标识
     */
    public static final String TAKE_EFFECT = "2";


    /**
     * 增量数据生效状态
     */
    public enum effectiveStatus {
        /**
         * 生效中
         */
        BECOMING_EFFECTIVE(0),
        /**
         * 生效完成
         */
        EFFECTIVE_SUCCCESS(1),
        /**
         * 生效失败
         */
        EFFECTIVE_FAIL(2),
        /**
         * 重启后生效
         */
        EFFECTIVE_4RESTART(3),
        /**
         * 数据入运行时表成功
         */
        IMPORT_RUNING_TABLE_SUCCESS(4),
        /**
         * 数据入运行时表失败
         */
        IMPORT_RUNING_TABLE_FAIL(5);

        private int value;

        private effectiveStatus(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }
    }

    /**
     * 增量数据导入状态
     */
    public enum importStatus {
        /**
         * 预加载中
         */
        PRELOADING(0),
        /**
         * 预加载完成
         */
        PRELOAD_SUCCCESS(1),
        /**
         * 预加载失败
         */
        PRELOAD_FAIL(2),
        /**
         * 数据导入成功
         */
        DATA_IMPORT_SUCCESS(3),
        /**
         * 数据导入失败
         */
        DATA_IMPORT_FAIL(4);

        private int value;

        private importStatus(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }
    }

    /**
     * 启用状态标识
     */
    public static final int ACTIVE_STATUS = 1;

    /**
     * 未启用状态标识
     */
    public static final int DEACTIVE_STATUS = 0;

    public WorkflowConstant() {

    }
}
