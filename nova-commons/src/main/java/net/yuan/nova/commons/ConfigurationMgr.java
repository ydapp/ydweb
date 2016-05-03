package net.yuan.nova.commons;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 配置文件管理器，从当前的运行环境中找出需要的配置文件
 * <ul>
 * 默认的查找顺序
 * <li>查找环境变量中的APP_HOME，如果有则取其“/etc”子目录中的配置</li>
 * <li>查找“/WEB-INF”目录下是否有需要的配置</li>
 * </ul>
 * 
 * @author 0027005704（张帅）
 *
 */
public class ConfigurationMgr {

	protected final static Logger log = LoggerFactory.getLogger(ConfigurationMgr.class);

	/** 配置文件存放目录 */
	private static String m_ConfigPath;

	/** 系统 Home目录 */
	private static String m_HomePath = null;
	// 配置文件所在的主目录
	private static final String ETC_PATH = "/etc";
	// 配置文件所在的后背目录
	private static final String CONFIG_PATH = "/config";

	private static final String APP_HOME = "APP_HOME";
	// 默认的配置文件名称
	private static final String DEFAULT_CONFIG_NAME = "system.properties";
	// 后背的配置文件名称
	private static final String BACKUP_CONFIG_NAME = "system.cfg";

	static {
		// 在类被加载的时候初始话
		initHomePath();
	}

	private ConfigurationMgr() {

	}

	/**
	 * 获取APP_HOME，该方法只确定路径，不验证文件是否存在
	 * 
	 */
	private static void initHomePath() {
		if (m_HomePath == null) {
			// 获得系统属性对象
			Properties properties = System.getProperties();
			// 获得系统属性中的APP_HOME
			String homePath = properties.getProperty(APP_HOME, "");
			if (StringUtils.isBlank(homePath)) {
				String os = properties.getProperty("os.name").toLowerCase();
				try {
					InputStream inStream = null;
					if (os.indexOf("windows") > -1) {
						inStream = Runtime.getRuntime().exec("cmd /C set").getInputStream();
					} else if (os.indexOf("unix") > -1) {
						inStream = Runtime.getRuntime().exec("env").getInputStream();
					} else {
						inStream = Runtime.getRuntime().exec("env").getInputStream();
					}
					BufferedReader in = new BufferedReader(new InputStreamReader(inStream, "8859_1"));
					while (true) {
						String line = in.readLine();
						if (line == null) {
							break;
						}
						if (line.indexOf(APP_HOME) >= 0) {
							Pattern pat = Pattern.compile("=(.*)");
							Matcher m = pat.matcher(line);
							while (m.find()) {
								homePath = m.group(1);
								System.setProperty(APP_HOME, homePath);
							}
						}
					}
				} catch (IOException e) {
					log.error("env loader error", e);
				}
			}
			if (StringUtils.isNotBlank(homePath)) {
				// 主目录不为空
				m_HomePath = homePath;
				m_ConfigPath = m_HomePath + ETC_PATH;
			} else {
				// 没有配置则获得当前的classPath
				java.net.URL resource = ConfigurationMgr.class.getResource("/");
				// 获得resource路径
				String path = resource.getPath();
				if (StringUtils.isBlank(path)) {
					// resource路径为空则指向当前路径
					m_HomePath = ".";
					m_ConfigPath = m_HomePath + ETC_PATH;
				} else {
					if (StringUtils.endsWith(path, "WEB-INF/classes/")) {
						// 将路径指向WEB-INF
						path = path.substring(0, path.length() - 9);
					}
					m_HomePath = path;
					m_ConfigPath = m_HomePath;
				}
			}
			if (StringUtils.endsWith(m_ConfigPath, ETC_PATH)) {
				File etcDir = new File(m_ConfigPath);
				if (etcDir.exists() && etcDir.isDirectory()) {
					// 如果“etc”文件存在且为目录，则使用该目录下的配置文件启动
				} else {
					// 如果“etc”不是目录，则使用CONFIG_PATH目录下的配置文件启动
					m_ConfigPath = m_HomePath + CONFIG_PATH;
				}
			}
			log.info("使用该目录下的配置启动：{}", m_ConfigPath);
		}
	}

	/**
	 * 获得主目录的路径
	 * 
	 * @return
	 */
	public static String getHomePath() {
		return m_HomePath;
	}

	/**
	 * 获得配置文件所在目录的路径
	 * 
	 * @return
	 */
	public static String getConfigPath() {
		return m_ConfigPath;
	}

	/**
	 * 返回配置文件的具体路径
	 * 
	 * @return
	 */
	public static String getLocation() {
		String filePath = m_ConfigPath + "/" + DEFAULT_CONFIG_NAME;
		if (new File(filePath).exists()) {
			System.out.println(filePath);
			return filePath;
		}
		filePath = m_ConfigPath + "/" + BACKUP_CONFIG_NAME;
		if (new File(filePath).exists()) {
			System.out.println(filePath);
			return filePath;
		}
		// 如果这两个文件都不存在，抛出异常
		throw new RuntimeException(m_ConfigPath + "目录下找不到需要的配置文件");
	}

}
