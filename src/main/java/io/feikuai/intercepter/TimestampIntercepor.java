package io.feikuai.intercepter;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import io.feikuai.annotation.Access;
import lombok.extern.slf4j.Slf4j;


/**
 * 时间戳拦截器
 * @author liudo
 * @date 2018/12/26
 */
@Slf4j
public class TimestampIntercepor extends HandlerInterceptorAdapter {
	/**
	 * 单位秒 --5分钟
	 */
	public static final Integer TIMESTAMP_INTERVAL = 300;

	/**
	 * 请求进入接口前拦截处理
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		log.info("时间戳拦截器");
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			Access methodAnnotation = handlerMethod.getMethodAnnotation(Access.class);
			// 如果显示配置为false 不做时间拦截,默认不配置需要做时间拦截
			if (Objects.nonNull(methodAnnotation) && !methodAnnotation.isTimestamp()) {
				return super.preHandle(request, response, handler);
			}
		}
		String timeStamp = request.getHeader(HeaderInterceptor.TIMESTAMP);
		if (StringUtils.isBlank(timeStamp)) {
			throw new Exception("时间戳不存在");
		}
		long tempTimestamp = 0L;
		try {
			tempTimestamp = Long.parseLong(timeStamp);
		} catch (NumberFormatException e) {
			throw new Exception("时间戳格式错误");
		}
		// 验证时间戳是否过期
		if (Math.abs(System.currentTimeMillis() / 1000 - tempTimestamp) > TIMESTAMP_INTERVAL) {
			throw new Exception("时间戳已过期");
		}
		return super.preHandle(request, response, handler);
	}

	/**
	 * 请求接口处理完成拦截处理
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);
	}

}
