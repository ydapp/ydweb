package net.yuan.nova.weather;

import java.io.IOException;
import java.util.Properties;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import net.yuan.nova.cache.CacheHandle;
import net.yuan.nova.commons.SystemConstant;

/**
 * @author Administrator
 * 
 *         创建天气管理器用于管理不通来源的天气数据
 *
 */
public class WeatherManager extends CacheHandle {

	protected final Logger logger = LoggerFactory.getLogger(WeatherManager.class);

	private WeatherProvider weatherProvider;

	private final static String DEFAULT_CITY_NAME = "南京";

	private Properties properties;

	// 静态内部类
	private static class LazyHolder {
		private static final WeatherManager manager = new WeatherManager();
	}

	public static WeatherManager getInstance() {
		return LazyHolder.manager;
	}

	/**
	 * 默认使用百度天气数据
	 */
	public WeatherManager() {
		weatherProvider = new BaiduWeatherProvider();
		PropertiesFactoryBean pfb = new PropertiesFactoryBean();
		pfb.setLocation(new ClassPathResource("cityCode.properties", this.getClass()));
		try {
			pfb.afterPropertiesSet();
			properties = pfb.getObject();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setWeatherProvider(WeatherProvider weatherProvider) {
		this.weatherProvider = weatherProvider;
	}

	/**
	 * 获得默认城市的天气信息
	 * 
	 * @return
	 */
	public WeatherInfo weather() {
		return weather(getDefaultCityName());
	}

	/**
	 * 根据城市编码获得天气信息
	 * 
	 * @param cityCode
	 * @return
	 */
	public WeatherInfo weather(int cityCode) {
		String cityName = codeToName(cityCode);
		return weather(cityName);
	}

	/**
	 * 根据城市名称获得天气数据
	 * 
	 * @param cityName
	 * @return
	 */
	public WeatherInfo weather(String cityName) {
		Object obj = this.getCacheValue(cityName);
		if (obj != null) {
			logger.debug("获得缓存的天气数据");
			return (WeatherInfo) obj;
		}
		return refresh(cityName);
	}

	/**
	 * 刷新指定城市的天气信息
	 * 
	 * @param cityName
	 * @return
	 */
	public WeatherInfo refresh(String cityName) {
		logger.debug("刷新{}的天气信息", cityName);
		WeatherInfo info = this.weatherProvider.weather(cityName);
		if (info != null) {
			this.setCacheValue(cityName, info);
		}
		return info;
	}

	/**
	 * 刷新默认城市的天气
	 * 
	 * @return
	 */
	public WeatherInfo refresh() {
		String cityName = getDefaultCityName();
		logger.info("刷新默认城市（{}）的天气", cityName);
		WeatherInfo info = refresh(cityName);
		logger.info(JSONObject.fromObject(info).toString());
		return info;
	}

	private String codeToName(int cityCode) {
		if (properties != null) {
			return properties.getProperty(Integer.toString(cityCode));
		}
		return null;
	}

	private String getDefaultCityName() {
		return SystemConstant.getProperty("DEFAULT_CITY_NAME", DEFAULT_CITY_NAME);
	}

}
