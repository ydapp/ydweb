package net.yuan.nova.pis.controller;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;

import net.sf.json.JSONObject;
import net.yuan.nova.core.vo.JsonVo;

@ContextConfiguration(locations = { "classpath:applicationContext.xml"})  
@RunWith(SpringJUnit4ClassRunner.class)  
public abstract class BaseControllerTest extends AbstractTransactionalJUnit4SpringContextTests  {
	MockHttpServletRequest request = new MockHttpServletRequest();  
    MockHttpServletResponse response = new MockHttpServletResponse();  
    private static MockMvc mockMvc;  
    @Before  
    public void setup() {  
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.getController()).build();  
    }  
    public static String mockGet(String uri, String json){
    	MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(uri);
		return mock(builder,mockMvc , uri, json);
    }
    public static String mockPost(String uri, String json){
    	MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(uri);
		return mock(builder,mockMvc , uri, json);
    }
    public static String mockDelete(String uri, String json){
    	MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(uri);
		return mock(builder,mockMvc , uri, json);
    }
    public static String mockPut(String uri, String json){
    	MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(uri);
		return mock(builder,mockMvc , uri, json);
    }
	private static String mock(MockHttpServletRequestBuilder builder, MockMvc mvc, String uri, String json)   {  
		builder.servletPath(uri);
		builder.characterEncoding("UTF-8");
		builder.contentType(MediaType.APPLICATION_JSON);
		if (StringUtils.isNotBlank(json)){
			builder.content(json);
		}
		try {
			MvcResult mvcResult = mvc.perform(builder).andReturn();
			ModelAndView modelAndView = mvcResult.getModelAndView();
			if (modelAndView == null){
				throw new RuntimeException("请求地址未匹配:" + uri);
			}
			Map<String, Object> model = modelAndView.getModel();
         JsonVo jsonVo = (JsonVo)(model.get("result"));
         if (!jsonVo.isSuccess()){
        	 throw new RuntimeException("请求结果失败原因:" + jsonVo.getMessage());
         }
         return JSONObject.fromObject(jsonVo).toString();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }  
	abstract Object getController();
}
