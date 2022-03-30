package com.suning.zhongtai.wf.tool;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 读取属性文件内容
 * @author 13073189
 *
 */
public class PropertyUtil {
	
    private static Logger logger = LoggerFactory.getLogger(PropertyUtil.class);
	private static final Properties envProperties = new Properties();
	  
	private PropertyUtil(){}
	
	static {
		InputStream insEnv = null;
		try{
			insEnv = PropertyUtil.class.getClassLoader().getResourceAsStream("workflowsettings.properties");
			envProperties.load(insEnv);
		}catch(Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}finally{
			if(null != insEnv) {
				try {
					insEnv.close();
				} catch (IOException e) {
					logger.error(ExceptionUtils.getStackTrace(e));
				}
			}
		}
	}

	/**
	 * 获取executable扫描路径
	 * @return 
	 */
	public static String[] getExecutablePath() {

		return envProperties.getProperty("workflow.executable.scan.package").split(",");
	}

	/**
	 * 获取业务侧ApplicationContext路径
	 * @return
	 */
	public  static String[] getContextConfigLocation(){
		return envProperties.getProperty("contextConfigLocation").split(",");
	}
 
}
