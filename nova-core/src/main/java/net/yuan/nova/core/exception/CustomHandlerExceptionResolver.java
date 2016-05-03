package net.yuan.nova.core.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.yuan.nova.core.vo.JsonVo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

public class CustomHandlerExceptionResolver extends DefaultHandlerExceptionResolver implements MessageSourceAware {

	protected final Logger logger = LoggerFactory.getLogger(CustomHandlerExceptionResolver.class);

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {

		if (shouldApplyTo(request, handler)) {
			logException(ex, request);
			prepareResponse(ex, response);
			ModelAndView modelAndView = doResolveException(request, response, handler, ex);
			if (modelAndView == null) {
				logger.error("Resolving exception from handler [" + handler + "]: ", ex);
				// 用于展示异常详情
				CustomExceptionBean exception = new CustomExceptionBean(ex, false);
				JsonVo<CustomExceptionBean> json = new JsonVo<CustomExceptionBean>();
				json.setSuccess(false);
				json.setMessage("该笔业务没能正常处理");
				json.setResult(exception);

				modelAndView = new ModelAndView("error/error");
				if (ex instanceof MaxUploadSizeExceededException) {
					// 上传文件时超出上传文件限制
					MaxUploadSizeExceededException musee = (MaxUploadSizeExceededException) ex;
					modelAndView = new ModelAndView("error_fileupload");
					json.setMessage("您的文件太大了，允许的最大文件：" + musee.getMaxUploadSize());
				} else if (ex instanceof DataAccessResourceFailureException) {
					json.setMessage("连接数据库异常");
				}
				modelAndView.addObject(json);
			}
			return modelAndView;
		} else {
			logger.error("该笔业务没能正常处理（未处理的异常）", ex);
			return null;
		}
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		// TODO Auto-generated method stub

	}

}
