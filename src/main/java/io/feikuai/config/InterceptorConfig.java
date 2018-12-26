package io.feikuai.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.feikuai.intercepter.CrossDomainInterceptor;
import io.feikuai.intercepter.HeaderInterceptor;
import io.feikuai.intercepter.LoginInterceptor;
import io.feikuai.intercepter.PerformanceInterceptor;
import io.feikuai.intercepter.SignatureInterceptor;
import io.feikuai.intercepter.TimestampIntercepor;


/**
 * 拦截器配置
 * 
 * @author liudo
 *
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
	/**
	 * 设置拦截器
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 跨域拦截器类
		registry.addInterceptor(new CrossDomainInterceptor()).addPathPatterns("/**");
		// 性能监控拦截器
		registry.addInterceptor(new PerformanceInterceptor()).addPathPatterns("/**");
		// Header请求控拦截器
		registry.addInterceptor(new HeaderInterceptor()).addPathPatterns("/**");
		// 配置时间戳拦截器
		registry.addInterceptor(new TimestampIntercepor()).addPathPatterns("/**");
		// 配置签名拦截器
		registry.addInterceptor(new SignatureInterceptor()).addPathPatterns("/**");
		// 登录拦截器
		registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**");
	}

}
