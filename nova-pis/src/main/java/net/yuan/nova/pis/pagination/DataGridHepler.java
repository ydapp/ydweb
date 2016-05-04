package net.yuan.nova.pis.pagination;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.support.HttpRequestHandlerServlet;

import com.github.pagehelper.PageInfo;

import net.yuan.nova.core.vo.DataGridData;
import net.yuan.nova.pis.controller.model.CustomerModel;
/**
 * 分页处理帮助类
 * @author leasonlive
 *
 */
public class DataGridHepler {
	/**
	 * 处理分页
	 * @param list
	 * @param modelMap
	 * @return
	 */
	public static ModelMap addDataGrid(List list,ModelMap modelMap){
		DataGridData dgd = new DataGridData();
		dgd.setRows(list);
		dgd.setTotal(new PageInfo(list).getTotal());
		if (modelMap == null){
			modelMap = new ModelMap();
		}
		modelMap.addAttribute("result", dgd);
		return modelMap;
	}
	/**
	 * 从request中获取分页参数
	 * @param request
	 * @return
	 */
	public static PageParam parseRequest(HttpServletRequest request){
		PageParam pageParam = new PageParam();
		pageParam.setPage(NumberUtils.toInt(request.getParameter("page")));
		pageParam.setPageSize(NumberUtils.toInt(request.getParameter("rows")));
		return pageParam;
	}
}
