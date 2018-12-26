package io.feikuai.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口访问权注解
 * @author liudo
 * @date 2018/12/26
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Access {
	/**
	 * 是否拦截时间
	 * 
	 * @return
	 */
	boolean isTimestamp() default true;

	/**
	 * 是否拦截签名
	 * 
	 * @return
	 */
	boolean isSign() default true;

	/**
	 * 是否登录
	 * 
	 * @return
	 */
	boolean isLogin() default true;
}
