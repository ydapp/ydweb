package net.yuan.nova.weather;

import java.net.URI;
import java.util.Arrays;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import net.yuan.nova.commons.HttpUtils;

public class BaiduWeatherProvider implements WeatherProvider {

	protected final Logger logger = LoggerFactory.getLogger(HtmlWeatherProvider.class);

	private static final String url = "http://api.map.baidu.com/telematics/v3/weather?location={location}&output={output}&ak={ak}";

	private String location = "南京";

	private String output = "json";

	private String ak = "CB862a4720fbaae0f904d655aed4f7af";

	/**
	 * 
	 * 请求数据
	 * 
	 * @return
	 */
	private String requestData(String cityName) {
		if (StringUtils.isBlank(cityName)) {
			cityName = this.location;
		}
		RestTemplate tmp = HttpUtils.getRestTemplate();
		UriComponents uriComponents = UriComponentsBuilder.fromUriString(url).build().expand(cityName, output, ak)
				.encode();
		URI uri = uriComponents.toUri();
		return tmp.getForObject(uri, String.class);
	}

	private WeatherInfo parser(String dataStr) {
		if (StringUtils.isBlank(dataStr)) {
			return null;
		}
		JSONObject data = JSONObject.fromObject(dataStr);
		JSONArray results = data.getJSONArray("results");
		if (results != null && !results.isEmpty()) {
			WeatherInfo weatherInfo = new WeatherInfo();

			JSONObject result = results.getJSONObject(0);
			// 当前城市名称
			String currentCity = result.getString("currentCity");
			weatherInfo.setCurrentCity(currentCity);
			// PM2.5
			String pm25 = result.getString("pm25");
			weatherInfo.setPm25(pm25);
			// 获得出行指数
			JSONArray index = result.getJSONArray("index");
			WeatherIndex[] indexArr = (WeatherIndex[]) JSONArray.toArray(index, WeatherIndex.class);
			weatherInfo.setIndex(Arrays.asList(indexArr));
			// 获得天气信息
			JSONArray weather_data = result.getJSONArray("weather_data");
			WeatherData[] dataArr = (WeatherData[]) JSONArray.toArray(weather_data, WeatherData.class);
			weatherInfo.setWeatherData(Arrays.asList(dataArr));
			return weatherInfo;
		}
		return null;
	}

	@Override
	public WeatherInfo weather(String cityName) {
		try {
			String dataStr = this.requestData(cityName);
			return this.parser(dataStr);
		} catch (Exception e) {
			logger.error("请求天气数据出错：{}", e.getMessage());
		}
		return null;
	}

}
