package net.yuan.nova.lbs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import net.yuan.nova.commons.HttpUtils;

/**
 * 百度LBS管理器
 * 
 */
@Service
public class BaiduLbsManager {

	private String CREATE_GEOTABLE = "http://api.map.baidu.com/geodata/v3/geotable/create?name={name}&ak=CB862a4720fbaae0f904d655aed4f7af&geotype=1&is_published=1"; // POST请求

	/**
	 * 创建表（create geotable）接口
	 */
	public String createGeotable(String name, String ak) {

		Map<String, Object> param = new HashMap<String, Object>();
		// geotable的中文名称
		param.put("name", name);
		// 是否发布到检索
		param.put("ak", ak);

		RestTemplate restTemplate = HttpUtils.getRestTemplate();
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		messageConverters.add(new StringHttpMessageConverter(Consts.UTF_8));
		restTemplate.setMessageConverters(messageConverters);

		return restTemplate.postForObject(CREATE_GEOTABLE, param, String.class);

	}

	/**
	 * 创建列
	 * 
	 * @param name
	 * @param key
	 * @param type
	 * @param is_sortfilter_field
	 * @param is_search_field
	 * @param is_index_field
	 * @param ak
	 */
	public void createColumn(String name, String key, int type, int is_sortfilter_field, int is_search_field,
			int is_index_field, String ak) {

	}

	/**
	 * 获取列列表
	 * 
	 * @param geotable_id
	 * @param ak
	 */
	public String getColumnList(String geotable_id, String ak) {
		String url = "http://api.map.baidu.com/geodata/v3/column/list?geotable_id=" + geotable_id + "&ak=" + ak;
		HttpClient httpClient = HttpUtils.createHttpClient();
		HttpGet httpgets = new HttpGet(url);
		try {
			HttpResponse response = httpClient.execute(httpgets);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String str = EntityUtils.toString(entity, Consts.UTF_8);
				return str;
			}
		} catch (IOException e) {

		} finally {
			httpgets.releaseConnection();
		}
		return null;
	}

	/**
	 * 查询指定条件的数据（poi）列表接口
	 * 
	 * @param geotable_id
	 * @param ak
	 * @return
	 */
	public String getDataList(String geotable_id, String ak, String page_index, String page_size, String title,
			String tags) {
		String url = "http://api.map.baidu.com/geodata/v3/poi/list?geotable_id=" + geotable_id + "&ak=" + ak;
		if (page_index != null && page_index != "") {
			url += "&page_index=" + page_index;
		}
		if (page_size != null && page_size != "") {
			url += "&page_size=" + page_size;
		}
		if (title != null && title != "") {
			url += "&title=" + title;
		}
		if (tags != null && tags != "") {
			url += "&tags=" + tags;
		}
		HttpClient httpClient = HttpUtils.createHttpClient();
		HttpGet httpgets = new HttpGet(url);
		try {
			HttpResponse response = httpClient.execute(httpgets);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String str = EntityUtils.toString(entity, Consts.UTF_8);
				return str;
			}
		} catch (IOException e) {

		} finally {
			httpgets.releaseConnection();
		}
		return null;
	}

	/**
	 * 按条件删掉poi数据
	 * 
	 * @param geotable_id
	 * @param ak
	 * @param is_total_del
	 * @param id
	 * @return
	 */
	public String deleteData(String geotable_id, String ak, String is_total_del, String id) {
		String url = "http://api.map.baidu.com/geodata/v3/poi/delete";
		HttpClient httpClient = HttpUtils.createHttpClient();
		HttpPost httpPost = new HttpPost(url);
		try {

			// 建立一个NameValuePair数组，用于存储欲传送的参数
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			// 添加参数
			params.add(new BasicNameValuePair("geotable_id", geotable_id));
			params.add(new BasicNameValuePair("ak", ak));
			if (is_total_del != null) {
				params.add(new BasicNameValuePair("is_total_del", is_total_del));
			}
			if (id != null) {
				params.add(new BasicNameValuePair("id", id));
			}
			// 设置编码
			httpPost.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
			// 执行请求
			HttpResponse response = httpClient.execute(httpPost);
			// 获得响应状态
			// 获得响应消息实体
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String str = EntityUtils.toString(entity, Consts.UTF_8);
				return str;
			}

		} catch (IOException e) {

		} finally {
			httpPost.releaseConnection();
		}
		return null;
	}

	/**
	 * 上传数据
	 * 
	 * @param geotable_id
	 * @param ak
	 * @param poi_list
	 * @param timestamp
	 * @return
	 */
	public String uploadData(String geotable_id, String ak, FileBody poi_list) {
		String url = "http://api.map.baidu.com/geodata/v3/poi/upload";
		HttpClient httpClient = HttpUtils.createHttpClient();
		HttpPost httpPost = new HttpPost(url);
		try {
			// MultipartEntity reqEntity = new MultipartEntity();
			// // reqEntity.addPart("file", (ContentBody) poi_list);
			// reqEntity.addPart("ak", new StringBody(ak));
			// reqEntity.addPart("geotable_id", new StringBody(geotable_id));
			// reqEntity.addPart("poi_list", poi_list);
			// httpPost.setEntity(reqEntity);

			MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
			multipartEntityBuilder.addTextBody("ak", ak);
			multipartEntityBuilder.addTextBody("geotable_id", geotable_id);
			multipartEntityBuilder.addPart("poi_list", poi_list);
			httpPost.setEntity(multipartEntityBuilder.build());

			// 执行请求
			HttpResponse response = httpClient.execute(httpPost);
			// 获得响应消息实体
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String str = EntityUtils.toString(entity, Consts.UTF_8);
				return str;
			}

		} catch (IOException e) {

		} finally {
			httpPost.releaseConnection();
		}
		return null;
	}

}
