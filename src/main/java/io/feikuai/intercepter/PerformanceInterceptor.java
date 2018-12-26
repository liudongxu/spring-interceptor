package io.feikuai.intercepter;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.perf4j.StopWatch;
import org.perf4j.commonslog.CommonsLogStopWatch;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import lombok.extern.slf4j.Slf4j;

/**
 * 接口性能拦截器
 * 
 * @author liudo
 *
 */
@Slf4j
@Component
public class PerformanceInterceptor extends HandlerInterceptorAdapter {

	/**
	 * 性能停止监测器  --不需要调用端传
	 */
	public static final String PERFORMANCE_STOP_WATCH = "performanceStopWatch";
	/**
	 * 慢请求阈值(毫秒)
	 */
	private static final Integer httpRequestSlowTime = 500;
	/**
	 * 日志记录
	 */
	private static final Log LOGGER = LogFactory.getLog(PerformanceInterceptor.class);

	/**
	 * 请求拦截前请求处理
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		log.info("性能拦截器");
		if (RequestMethod.OPTIONS.name().equalsIgnoreCase(request.getMethod())) {
			response.setStatus(200);
			return true;
		}
		// info级别输入性能日志
		if (LOGGER.isInfoEnabled()) {
			StopWatch stopWatch = new CommonsLogStopWatch(LOGGER);
			request.setAttribute(PERFORMANCE_STOP_WATCH, stopWatch);
		}
		return super.preHandle(request, response, handler);
	}

	/**
	 * 处理中
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if (LOGGER.isInfoEnabled()) {
			Object attr = request.getAttribute(PERFORMANCE_STOP_WATCH);
			if (Objects.nonNull(attr)) {
				StopWatch stopWatch = (StopWatch) attr;
				stopWatch.lap(genRequestInfoRecord(request, stopWatch.getElapsedTime()));
			}
		}
		super.postHandle(request, response, handler, modelAndView);
	}

	/**
	 * 生成请求信息记录
	 * 
	 * @param request
	 * @param elapsedTime
	 * @return
	 */
	private String genRequestInfoRecord(HttpServletRequest request, long elapsedTime) {
		StringBuffer sb = new StringBuffer();
		String method;
		if (request.getParameter("_method") != null) {
			method = request.getParameter("_method");
		} else {
			method = request.getMethod();
		}
		sb.append(method);
		sb.append("|");
		sb.append(request.getRequestURI());
		if (elapsedTime >= httpRequestSlowTime) {
			sb.append("|SLOW");
		}
		return sb.toString();
	}
}
