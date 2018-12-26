package io.feikuai.intercepter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import lombok.extern.slf4j.Slf4j;

/**
 * 跨域拦截器类
 * @author liudo
 * @date 2018/11/14
 */
@Slf4j
public class CrossDomainInterceptor extends HandlerInterceptorAdapter {
	/**
	* 处理前
	*
	* @param request HTTP请求
	* @param response HTTP应答
	* @param handler 处理器
	*/
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		log.info("跨域拦截器");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "GET,POST,HEAD,PUT,PATCH,DELETE,OPTION,OPTIONS");
		response.addHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, If-Modified-Since,"
				+ "Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With, timestamp, signature, token");
		response.addHeader("Access-Control-Max-Age", "1600");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		if (request.getMethod().equals(RequestMethod.OPTIONS.name())) {
			response.setStatus(HttpStatus.OK.value());
			return false;
		}
		return true;
	}
}
