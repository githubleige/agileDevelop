package com.suning.zhongtai.wf.tool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * sql工具方法
 *
 * @author 13073189
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public final class WfSqlUtil {
    /**
     * 
     * in语句的参数构造
     *
     * @param list
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public static Map<String, Object> inSqlParam(List<String> list) {
        Map<String, Object> paramMap = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i <= list.size(); i++) {
            builder.append(":var" + i).append(",");
            paramMap.put("var" + i, list.get(i - 1));
        }
        builder.deleteCharAt(builder.length() - 1);
        paramMap.put("inSql", builder.toString());
        return paramMap;
    }
}
