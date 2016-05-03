package net.yuan.nova.weather;

import java.io.Serializable;
import java.util.List;

public class WeatherInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 当前城市
	private String currentCity;
	// PM2.5
	private String pm25;
	// 小贴士
	private List<WeatherIndex> index;
	// 天气信息
	private List<WeatherData> weatherData;

	public String getCurrentCity() {
		return currentCity;
	}

	public void setCurrentCity(String currentCity) {
		this.currentCity = currentCity;
	}

	public String getPm25() {
		return pm25;
	}

	public void setPm25(String pm25) {
		this.pm25 = pm25;
	}

	public List<WeatherIndex> getIndex() {
		return index;
	}

	public void setIndex(List<WeatherIndex> index) {
		this.index = index;
	}

	public List<WeatherData> getWeatherData() {
		return weatherData;
	}

	public void setWeatherData(List<WeatherData> weatherData) {
		this.weatherData = weatherData;
	}

}
