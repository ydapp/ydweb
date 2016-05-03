package net.yuan.nova.web.view;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MappingJsonView extends MappingJackson2JsonView {

	public static final String DEFAULT_IE_CONTENT_TYPE = "text/plain;charset=UTF-8";

	public static final String DEFAULT_IE_JSONP_CONTENT_TYPE = "text/javascript,text/html;charset=UTF-8";

	private Set<String> jsonpParameterNames = new LinkedHashSet<String>(Arrays.asList("jsonp", "callback"));

	private String dateFormatStr = "yyyy-MM-dd HH:mm:ss";

	public MappingJsonView() {
		ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
		SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatStr);
		objectMapper.setDateFormat(dateFormat);
		this.setObjectMapper(objectMapper);
		setContentType(DEFAULT_CONTENT_TYPE);
		setExposePathVariables(false);
	}

	private String getJsonpParameterValue(HttpServletRequest request) {
		if (this.jsonpParameterNames != null) {
			for (String name : this.jsonpParameterNames) {
				String value = request.getParameter(name);
				if (!StringUtils.isEmpty(value)) {
					return value;
				}
			}
		}
		return null;
	}

	@Override
	protected void setResponseContentType(HttpServletRequest request, HttpServletResponse response) {
		if (getJsonpParameterValue(request) != null) {
			// 判断是否是IE，IE不支持“application/javascript”
			if (isIE(request)) {
				response.setContentType(DEFAULT_IE_JSONP_CONTENT_TYPE);
			} else {
				response.setContentType(DEFAULT_JSONP_CONTENT_TYPE);
			}
		} else {
			// 判断是否是IE，IE不支持“application/json”
			if (isIE(request)) {
				response.setContentType(DEFAULT_IE_CONTENT_TYPE);
			} else {
				MediaType mediaType = (MediaType) request.getAttribute(View.SELECTED_CONTENT_TYPE);
				if (mediaType != null && mediaType.isConcrete()) {
					response.setContentType(mediaType.toString());
				} else {
					response.setContentType(getContentType());
				}
			}
		}
	}

	protected boolean isIE(HttpServletRequest request) {
		String agent = request.getHeader("User-Agent");
		return agent != null && (agent.indexOf("MSIE") != -1 || agent.indexOf("rv:11") != -1);
	}

}
