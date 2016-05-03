package net.yuan.nova.uboss;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UbossInterface {

	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/mcallremoteservice.do")
	private Object mcallremote(HttpServletRequest request, HttpServletResponse response) {
		Enumeration<String> names = request.getParameterNames();
		StringBuffer json = new StringBuffer();
		json.append("{");
		String inputJSON = null;
		while (names.hasMoreElements()) {
			if (json.length() > 1) {
				json.append(", ");
			}
			String name = names.nextElement();
			System.out.print(name + " : ");
			String value = request.getParameter(name);
			System.out.println(value);
			json.append("\"").append(name).append("\":\"").append(value).append("\"");
		}
		inputJSON = json.append("}").toString();
		System.out.println("------------------------------");
		System.out.println("inputJSON:" + inputJSON);
		System.out.println("------------------------------");

		return JSONObject.fromObject(inputJSON);
	}
}
