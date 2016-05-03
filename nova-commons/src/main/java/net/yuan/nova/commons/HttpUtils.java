package net.yuan.nova.commons;

import java.io.File;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class HttpUtils {

	protected final static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

	private static RestTemplate restTemplate = null;
	private static HttpClientBuilder builder = null;

	/**
	 * 获得请求使用的RestTemplate对象
	 * 
	 * @return
	 */
	public static RestTemplate getRestTemplate() {
		if (restTemplate == null) {
			HttpClient httpClient = createHttpClient();
			ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
			restTemplate = new RestTemplate(requestFactory);
		}
		return restTemplate;
	}

	public static HttpClient createHttpClient() {
		if (builder == null) {
			builder = HttpClientBuilder.create();
			// 如果需要代理则添加代理访问
			if (Boolean.parseBoolean(SystemConstant.getProperty("proxy.enable", "false"))) {
				logger.info("需要设置代理访问网络");
				String hostname = SystemConstant.getProperty("proxy.hostname");
				String portStr = SystemConstant.getProperty("proxy.port");
				if (StringUtils.isNotBlank(hostname) && StringUtils.isNotBlank(portStr)) {
					logger.info("当前使用的代理是：{}:{}", hostname, portStr);
					HttpHost proxy = new HttpHost(hostname, NumberUtils.toInt(portStr));
					builder.setProxy(proxy);
				} else {
					logger.warn("代理配置有误，请留意");
				}
			}
			// builder.setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			TrustManager[] managers = { new X509TrustManager() {

				/**
				 * 该方法体为空时信任所有客户端证书
				 */
				@Override
				public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

				}

				/**
				 * 该方法体为空时信任所有服务器证书
				 */
				@Override
				public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

				}

				/**
				 * 返回信任的证书
				 */
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

			} };// 证书过滤
			SSLContext sslContext = null;
			try {
				sslContext = SSLContext.getInstance("TLS");
				sslContext.init(null, managers, new SecureRandom());
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (KeyManagementException e) {
				e.printStackTrace();
			}
			builder.setSSLContext(sslContext);
		}
		HttpClient httpClient = builder.build();

		return httpClient;
	}

	/**
	 * get请求，获得相应结果
	 * 
	 * @param url
	 * @return
	 */
	public static String getForString(String url) {
		RestTemplate restTemplate = HttpUtils.getRestTemplate();
		URI uri = URI.create(url);
		return restTemplate.getForObject(uri, String.class);
	}

	/**
	 * @param url
	 * @param filePath
	 * @param uriVariables
	 * @return
	 */
	public static String upload(String url, File file, Map<String, ?> uriVariables) {
		return upload(url, file, "file", uriVariables);
	}

	/**
	 * @param url
	 * @param filePath
	 * @param uriVariables
	 * @return
	 */
	public static String upload(String url, File file, String fileParamName, Map<String, ?> uriVariables) {
		RestTemplate restTemplate = HttpUtils.getRestTemplate();
		FileSystemResource resource = new FileSystemResource(file);
		MultiValueMap<String, Object> param = new LinkedMultiValueMap<String, Object>();
		param.add(fileParamName, resource);
		String fileName = (String) uriVariables.get("fileName");
		if (StringUtils.isBlank(fileName)) {
			fileName = file.getName();
		}
		param.add("fileName", fileName);

		return restTemplate.postForObject(url, null, String.class, param);
	}

	/**
	 * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址;
	 * 
	 * @param request
	 * @return
	 */
	public static String getIp(HttpServletRequest request) {
		// 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
		String ip = request.getHeader("X-Forwarded-For");
		if (logger.isInfoEnabled()) {
			logger.info("getIpAddress(HttpServletRequest) - X-Forwarded-For - String ip=" + ip);
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
				if (logger.isInfoEnabled()) {
					logger.info("getIpAddress(HttpServletRequest) - Proxy-Client-IP - String ip=" + ip);
				}
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
				if (logger.isInfoEnabled()) {
					logger.info("getIpAddress(HttpServletRequest) - WL-Proxy-Client-IP - String ip=" + ip);
				}
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
				if (logger.isInfoEnabled()) {
					logger.info("getIpAddress(HttpServletRequest) - HTTP_CLIENT_IP - String ip=" + ip);
				}
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
				if (logger.isInfoEnabled()) {
					logger.info("getIpAddress(HttpServletRequest) - HTTP_X_FORWARDED_FOR - String ip=" + ip);
				}
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
				if (logger.isInfoEnabled()) {
					logger.info("getIpAddress(HttpServletRequest) - getRemoteAddr - String ip=" + ip);
				}
			}
		} else if (ip.length() > 15) {
			String[] ips = ip.split(",");
			for (int index = 0; index < ips.length; index++) {
				String strIp = (String) ips[index];
				if (!("unknown".equalsIgnoreCase(strIp))) {
					ip = strIp;
					break;
				}
			}
		}
		return ip;
	}

	/**
	 * 得到请求的根目录
	 * 
	 * @param request
	 * @return
	 */
	public static String getBasePath(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		sb.append(request.getScheme()).append("://").append(request.getServerName());
		int serverPort = request.getServerPort();
		if (serverPort == 80 || serverPort == 443) {
			logger.debug("不需要端口");
		} else {
			sb.append(":").append(serverPort);
		}
		sb.append(request.getContextPath());
		return sb.toString();
	}

	/**
	 * 得到结构目录
	 * 
	 * @param request
	 * @return
	 */
	public static String getContextPath(HttpServletRequest request) {
		String path = request.getContextPath();
		return path;
	}

	/**
	 * @return
	 */
	public static String getRealPath() {
		return System.getProperty(SystemConstant.WEBAPP_ROOT);
	}
}
