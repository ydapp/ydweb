package net.yuan.nova.weather;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Administrator
 * 
 *         从中国天气网的网页中抓取数据
 *
 */
public class Weather7DaysProvider implements WeatherProvider {

	protected final Logger logger = LoggerFactory.getLogger(HtmlWeatherProvider.class);

	private static final String URL = "http://www.weather.com.cn/weather/{}.shtml";

	private Document getWeatherDocument(String cityCode) {
		Document doc = null;
		try {
			doc = Jsoup.connect(StringUtils.replace(URL, "{}", cityCode)).get();
		} catch (IOException e) {
			logger.error("请求天气页面时出错", e);
		}
		return doc;
	}

	@Override
	public WeatherInfo weather(String cityCode) {
		WeatherInfo weatherInfo = new WeatherInfo();
		List<WeatherData> list = new ArrayList<WeatherData>();
		Document doc = getWeatherDocument(cityCode);
		if (doc != null) {
			Elements provin = doc.select(".cityName h3");
			if (provin.size() > 0) {
				weatherInfo.setCurrentCity(provin.get(0).html());
			}
			// 获得7天tab元素
			Element tabDaysEl = doc.getElementById("7d");
			// 获得数据更新时间
			/*
			 * Elements updataTimeEls =
			 * tabDaysEl.getElementsByClass("updataTime"); if
			 * (updataTimeEls.size() > 0) { Element updataTimeEl =
			 * updataTimeEls.get(0); System.out.println(updataTimeEl.html()); }
			 */
			// 获得七天元素
			Elements dataEls = tabDaysEl.getElementsByClass("t").select("li");
			if (dataEls.size() > 1) {
				for (int i = 0; i < dataEls.size(); i++) {
					WeatherData data = new WeatherData();
					// 设置日期
					data.setDate(dataEls.get(i).select("h1").html() + "," + dataEls.get(i).select("h2").html());
					Elements imgs = dataEls.get(i).select("big");
					// 获取白天图片标识
					String attrD = imgs.get(0).attr("class");
					// 获取晚上图片标识
					String attrN = imgs.get(1).attr("class");
					String[] arrayD = attrD.split(" ");
					String[] arrayN = attrN.split(" ");
					if (arrayD.length < 2) {
						data.setDayPictureUrl(arrayN[1].replace("n", "d"));
					} else {
						data.setDayPictureUrl(arrayD[1]);
					}
					// data.setDayPictureUrl(split[1]);
					data.setNightPictureUrl(arrayN[1]);
					// 设置天气情况
					Elements weaEls = dataEls.get(i).select(".wea");
					data.setWeather(weaEls.get(0).html());
					// 设置温度
					Elements temEls = dataEls.get(i).select(".tem span");
					Elements temElT = dataEls.get(i).select("i");
					if (temEls.size() == 0) {
						data.setTemperature(temElT.get(0).html() + "<br/>" + temElT.get(0).html());
					} else {
						if (temEls.get(0).html().indexOf("℃") == -1) {
							data.setTemperature(temEls.get(0).html() + "℃" + "<br/>" + temElT.get(0).html());
						} else {
							data.setTemperature(temEls.get(0).html() + "<br/>" + temElT.get(0).html());
						}
					}
					// 风力风向
					Elements winEls = dataEls.get(i).select(".win i");
					data.setWind(winEls.html());
					/*
					 * if(winEls.size() >= 2){ Element windT = winEls.get(0);
					 * Element windN = winEls.get(1); if
					 * (StringUtils.equals(windT.attr("title"),
					 * windN.attr("title"))) { data.setWind(windT.attr("title")
					 * + windT.html()); } else {
					 * data.setWind(windT.attr("title") + "转" +
					 * windN.attr("title") + windT.html()); } }else{ Element
					 * windT = winEls.get(0); data.setWind(windT.attr("title"));
					 * }
					 */

					list.add(data);
				}
				weatherInfo.setWeatherData(list);
			}
		}
		return weatherInfo;
	}

	public List<Map<String, String>> get(String url) throws IOException {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String regex = "^[a-zA-Z]+$";
		String regexNum = "^[0-9]+$";
		Request get = Request.Get(url);
		get.addHeader("Referer", "http://www.weather.com.cn/weather1d/101020100.shtml");
		Response res = get.execute();
		String contents = res.returnContent().asString();
		if (!"success_jsonpCallback([])".equals(contents)) {
			int start = contents.indexOf("{");
			int end = contents.lastIndexOf("}");
			String content = contents.substring(start, end + 1);
			String[] array = content.split(",");
			for (String s : array) {
				Map<String, String> map = new HashMap<String, String>();
				JSONObject jsonObject = JSONObject.fromObject(s);
				String ref = jsonObject.getString("ref");
				String[] split = ref.split("~");
				map.put("id", split[0]);
				map.put("city", split[2]);
				map.put("province", split[split.length - 1]);

				if (split[0].matches(regexNum)) {
					list.add(0, map);
				} else {
					if (!split[0].matches(regex)) {
						list.add(map);
					}
				}

			}
		}
		return list;
	}

	public static void main(String[] args) {
		Weather7DaysProvider weather7DaysProvider = new Weather7DaysProvider();
		WeatherInfo weather = weather7DaysProvider.weather("101190408");
		System.out.println(JSONObject.fromObject(weather));
	}

}
