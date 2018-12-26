package io.feikuai.intercepter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import io.feikuai.annotation.Access;
import io.feikuai.param.PHeader;
import lombok.extern.slf4j.Slf4j;

/**
 * 登录拦截器
 * 
 * @author liudo
 *
 */
@Slf4j
public class LoginInterceptor extends HandlerInterceptorAdapter {

	/**
	 * 请求拦截前请求处理
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		log.info("登录拦截器");
		log.info(request.getRequestURL() + "'");
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			Access methodAnnotation = handlerMethod.getMethodAnnotation(Access.class);
			if (methodAnnotation != null && !methodAnnotation.isLogin()) {
				return super.preHandle(request, response, handler);
			}
		} else if (handler instanceof ResourceHttpRequestHandler) {
			ResourceHttpRequestHandler handlerMethod = (ResourceHttpRequestHandler) handler;
			log.info(handlerMethod + "");
		}
		// 通过token获取用户ID
		PHeader header = (PHeader) request.getAttribute("header");
		if (header.getUserId() == null) {
			throw new Exception("当前登录已失效,请重新登录");
		}
		return super.preHandle(request, response, handler);
	}

}
