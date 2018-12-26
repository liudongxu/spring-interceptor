package io.feikuai.intercepter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import io.feikuai.param.PHeader;
import lombok.extern.slf4j.Slf4j;

/**
 * header拦截器:
 * <p>:获取header中的参数，并且存储在request 的Attribute中，业务接口中需要就从request中获取
 * @author liudo
 *
 */
@Slf4j
public class HeaderInterceptor extends HandlerInterceptorAdapter {

	/**
	 * 时间戳
	 */
	public static final String TIMESTAMP = "timestamp";

	/**user_id***/
	public final static String USER_ID = "USER_ID";
	/***
	 * 令牌
	 */
	public final static String TOKEN = "token";
	/***
	 * 签名
	 */
	public final static String SIGNATURE = "signature";


	/**
	 * 请求拦截前请求处理
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		log.info("header拦截器");
		PHeader header = new PHeader();
		String token = null;
		if (RequestMethod.GET.name().equalsIgnoreCase(request.getMethod())) {
			token = getCookieValue(request.getCookies(), TOKEN);
		} else {
			token = request.getHeader(TOKEN);
		}
		String userId = getUserIdByToken(token);
		header.setToken(token);
		header.setUserId(userId);
		request.setAttribute("header", header);
		MDC.put(USER_ID, userId);// 把userID打入到日志中
		return super.preHandle(request, response, handler);

	}

	/**
	 * 执行完成
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// 从 threadLocal中移除
		MDC.remove(USER_ID);
		super.afterCompletion(request, response, handler, ex);
	}

	/**
	 * 通过token获取对于的用户ID
	 * @param token
	 * @return
	 */
	private String getUserIdByToken(String token) {
		// 业务处理：在redis或者其他存储中通过token获取对于的userID -- 在此省略
		// ......
		// ......
		// ......
		return token;
	}

	/**
	* 获取Cookie值
	*
	* @param cookies Cookie数组
	* @param name Cookie名称
	* @return Cookie值
	*/
	public static String getCookieValue(Cookie[] cookies, String name) {
		// 检查空值
		if (name == null || name.isEmpty()) {
			return null;
		}
		if (cookies == null || cookies.length == 0) {
			return null;
		}

		// 查找令牌
		for (Cookie cookie : cookies) {
			if (name.equalsIgnoreCase(cookie.getName())) {
				return cookie.getValue();
			}
		}

		// 返回空值
		return null;
	}

}
