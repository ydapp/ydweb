package net.yuan.nova.weather;

import java.io.IOException;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
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
public class HtmlWeatherProvider implements WeatherProvider {

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

	public void today(String cityCode) {
		Document doc = getWeatherDocument(cityCode);
		WeatherData data = new WeatherData();
		if (doc != null) {
			// 获得今天tab元素
			Element tabDaysEl = doc.getElementById("tabDays");
			// 获得数据更新时间
			Elements updataTimeEls = tabDaysEl.getElementsByClass("updataTime");
			if (updataTimeEls.size() > 0) {
				Element updataTimeEl = updataTimeEls.get(0);
				System.out.println(updataTimeEl.html());
			}
			// 获得今天元素
			Elements dataEls = tabDaysEl.select("#today .dn");
			if (dataEls.size() > 1) {
				// 设置日期
				data.setDate(dataEls.get(0).child(0).html());
				// 设置天气
				Elements weaEls = dataEls.select(".wea");
				String weaT = weaEls.get(0).html();
				String weaN = weaEls.get(1).html();
				if (StringUtils.equals(weaT, weaN)) {
					data.setWeather(weaT);
				} else {
					data.setWeather(weaT + "转" + weaN);
				}
				// 设置温度
				Elements temEls = dataEls.select(".tem span");
				data.setTemperature(temEls.get(0).html() + "~" + temEls.get(1).html() + "°C");
				// 风力风向
				Elements winEls = dataEls.select(".win span");
				Element windT = winEls.get(0);
				Element windN = winEls.get(1);
				if (StringUtils.equals(windT.attr("title"), windN.attr("title"))) {
					data.setWind(windT.attr("title") + windT.html());
				} else {
					data.setWind(windT.attr("title") + "转" + windN.attr("title") + windT.html());
				}
				// 日出时间
				Elements sunUpEls = dataEls.get(0).select(".sunUp");
				if (sunUpEls.size() > 0) {
					data.setSunrise(sunUpEls.html());
				}
				Elements sunDownEls = dataEls.get(1).select(".sunDown");
				if (sunDownEls.size() > 0) {
					data.setSunset(sunDownEls.html());
				}
			}
			System.out.println(JSONObject.fromObject(data));
		}
	}

	@Override
	public WeatherInfo weather(String cityName) {
		// TODO Auto-generated method stub
		return null;
	}

}
