package net.yuan.nova.commons;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定义系统常量
 * 
 * @author zhangshuai
 * 
 */
public class SystemConstant {

	protected final static Logger log = LoggerFactory.getLogger(SystemConstant.class);

	/**
	 * 由properties文件或的配置信息
	 */
	private static Properties properties;

	private static SystemParamHandle SystemParamHandle;

	/**
	 * 应用部署路径的KEY
	 */
	public static String WEBAPP_ROOT = "webApp.root";

	/**
	 * 超级用户饿用户名
	 */
	public static String ADMIN_USER = "admin";

	/**
	 * 上传文件夹
	 */
	public static String UPLOAD_FOLDER = "upload";

	/**
	 * 备份文件夹
	 */
	public static String BACKUP_FOLDER = "WEB-INF/backup";

	/**
	 * 用户头像文件夹
	 */
	public static String USER_FOLDER = "users";

	/**
	 * 全文检索索引文件位置，默认在“WEB-INF/lucene”下
	 */
	public static String LUCENE_DIR = "WEB-INF/lucene";

	/**
	 * 当前用户key（不可修改）
	 */
	public static final String CURRENT_USER = "CURRENT_USER";

	/**
	 * Session中的用户key（不可修改）
	 */
	public static final String SESSION_USER = "SESSION_USER";

	/**
	 * Session中的用户名key（不可修改）
	 */
	public static final String SESSION_USERNAME = "SESSION_USERNAME";

	/**
	 * WEBSOCKET Session中的用户名 Key
	 */
	public static final String WEBSOCKET_USERNAME = "WS_USERNAME";

	/**
	 * 获得配置的系统常量的值
	 * 
	 * @param key
	 *            the property key.
	 * @return
	 */
	public static String getProperty(String key) {
		if (StringUtils.isBlank(key)) {
			log.warn("传入的参数为空");
			return null;
		}
		if (properties == null) {
			return null;
		}
		return properties.getProperty(key);
	}

	/**
	 * 获得配置的系统常量的值
	 * 
	 * @param key
	 *            the property key.
	 * @param defaultValue
	 *            a default value.
	 * @return
	 */
	public static String getProperty(String key, String defaultValue) {
		if (StringUtils.isBlank(key)) {
			log.warn("传入的参数为空");
			return defaultValue;
		}
		if (properties == null) {
			return null;
		}
		return properties.getProperty(key, defaultValue);
	}

	public static void setProperties(Properties properties) {
		log.info("加载的配置信息：{}", properties.toString());
		SystemConstant.properties = properties;
		UPLOAD_FOLDER = properties.getProperty("UPLOAD_FOLDER", UPLOAD_FOLDER);
		BACKUP_FOLDER = properties.getProperty("BACKUP_FOLDER", BACKUP_FOLDER);
		USER_FOLDER = properties.getProperty("USER_FOLDER", USER_FOLDER);
		ADMIN_USER = properties.getProperty("ADMIN_USER", ADMIN_USER);
	}

	/**
	 * 获得数据库中配置的系统参数
	 * 
	 * @return
	 */
	public static SystemParam getSystemParam(String paramName) {
		return SystemConstant.SystemParamHandle.getSystemParam(paramName);
	}

	public static void setSystemParams(SystemParamHandle SystemParamHandle) {
		SystemConstant.SystemParamHandle = SystemParamHandle;
	}

}
