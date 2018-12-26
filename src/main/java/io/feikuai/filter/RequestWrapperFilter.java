package io.feikuai.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import io.feikuai.wrapper.BodyReaderHttpServletRequestWrapper;


/**
 * 过滤器：解决二次读取Body的问题
 * 
 * @author liudo
 * @date 2018/11/10
 */
@Component
@ServletComponentScan
@WebFilter(urlPatterns = "/*")
@Order(1)
public class RequestWrapperFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		if (httpServletRequest.getMethod().equalsIgnoreCase(RequestMethod.GET.name())) {
			chain.doFilter(request, response);
		} else {
			// 其他请求，把request转换为BodyReaderHttpServletRequestWrapper
			ServletRequest requestWrapper = new BodyReaderHttpServletRequestWrapper(httpServletRequest);
			chain.doFilter((HttpServletRequest) requestWrapper, response);
		}
	}

}
