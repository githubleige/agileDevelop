package com.suning.zhongtai.wf.tool;


import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;

/**
 * SCMNode解析工具类<br/>
 * @author: 18040994
 * @date: 2018/12/10 15:45
 */
public class ScmNodeParserUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(ScmNodeParserUtil.class);
	private static Pattern pattern = Pattern.compile("^\\s*$");  //空字符串
	private ScmNodeParserUtil(){}
	/**
	 * 将scm中配置的内容填充到bean中
	 * @param obj ：需要填充的bean
	 * @param val ：SCM中的内容-----整体返回
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static void parseToObject(Object obj,String val){
		if (StringUtils.isBlank(val)){
			return;
		}
		String[] rows = val.split("\n");
		for(String row:rows) {
			if(row.startsWith("#") || pattern.matcher(row).matches()) {
				continue;
			}
			String[] keyVal = row.split("=");
			if (ArrayUtils.isEmpty(keyVal) || keyVal.length < 2){
				continue;
			}
			try{
				BeanUtils.setProperty(obj, keyVal[0], keyVal[1]);
			} catch (InvocationTargetException | IllegalAccessException e) {
				LOGGER.warn("SCM配置解析出错：({})",val,e);
				continue;
			}
		}
	}

}
