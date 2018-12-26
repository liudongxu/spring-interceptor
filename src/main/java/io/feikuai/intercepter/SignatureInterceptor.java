package io.feikuai.intercepter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSONObject;

import io.feikuai.annotation.Access;
import io.feikuai.wrapper.BodyReaderHttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;

/**
 * 签名拦截器
 * 
 * @author liudo
 *
 */
@Slf4j
public class SignatureInterceptor extends HandlerInterceptorAdapter {
	/**
	 * 签名秘钥
	 */
	public static final String SIGN_SECRET_KEY = "536544c7-08ce-11e9-836d-000c29b3e30b";

	public final String UTF8 = "UTF-8";

	/**
	 * 请求拦截前请求处理
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		log.info("签名拦截器");
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			Access methodAnnotation = handlerMethod.getMethodAnnotation(Access.class);
			if (Objects.nonNull(methodAnnotation) && !methodAnnotation.isSign()) {
				return super.preHandle(request, response, handler);
			}
		}
		String timestamp = request.getHeader(HeaderInterceptor.TIMESTAMP);
		if (StringUtils.isBlank(timestamp)) {
			throw new Exception("时间戳不存在");
		}
		// 终端签名
		String signature = request.getHeader(HeaderInterceptor.SIGNATURE);
		if (StringUtils.isBlank(signature)) {
			throw new Exception("签名不存在");
		}
		String token = request.getHeader(HeaderInterceptor.TOKEN);
		// 验证请求签名
		String tempSignature = "";
		if (request.getMethod().equalsIgnoreCase(RequestMethod.GET.name())) {
			tempSignature = genSignature(request, timestamp, token);
			if (!signature.equalsIgnoreCase(tempSignature)) {
				log.error(MessageFormat.format("签名错误(请求签名:{0},计算签名:{1})", signature, tempSignature));
				throw new Exception("签名错误");
			}
			return super.preHandle(request, response, handler);
		}
		ServletRequest requestWrapper = new BodyReaderHttpServletRequestWrapper(request);
		tempSignature = genPostJSONSignature(requestWrapper, timestamp, token);
		if (!signature.equalsIgnoreCase(tempSignature)) {
			log.error(MessageFormat.format("签名错误--请求签名:{0},服务器签名:{1}", signature, tempSignature));
			throw new Exception("签名错误");
		}
		return super.preHandle((HttpServletRequest) request, response, handler);
	}

	/**
	 * 生成签名 签名规则如下： str=请求参数进行顺序排列再进行拼接 sign=md5(base64(timestamp)+token+私密+str)
	 *
	 * @param request
	 *            HTTP请求
	 * @return 签名
	 * @throws FeikuaiException
	 */
	private String genSignature(HttpServletRequest request, String timestamp, String token) throws Exception {
		// 初始化变量

		StringBuilder sb = new StringBuilder();
		List<String> nameList = new ArrayList<String>();
		final Base64.Encoder encode = Base64.getEncoder();
		try {
			sb.append(encode.encodeToString(timestamp.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (StringUtils.isNotBlank(token)) {
			sb.append(token);
		}
		sb.append(SIGN_SECRET_KEY);
		// 进行名称排序
		Enumeration<String> nameEnum = request.getParameterNames();
		while (nameEnum.hasMoreElements()) {
			nameList.add(nameEnum.nextElement());
		}
		Collections.sort(nameList);
		// 组装签名字符串
		for (String name : nameList) {
			if (!HeaderInterceptor.SIGNATURE.equals(name)) {
				sb.append(name);
				sb.append(request.getParameter(name));
			}
		}
		// 处理特殊字符
		String signature = sb.toString();
		// 计算并返回签名
		try {
			String md5Signature = DigestUtils.md5DigestAsHex(signature.getBytes(UTF8));
			log.info("签名字符串为:" + signature + " 签名为：" + md5Signature);
			return md5Signature;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new Exception("签名编码异常", e);
		}
	}

	/**
	 * 服务器端生成签名: 生成签名规则 str=md5(base64(timestamp)+token+SIGN_SECRET_KEY)
	 * 
	 * @param request
	 * @param timestamp
	 * @param token
	 * @return
	 */
	private String genPostJSONSignature(ServletRequest request, String timestamp, String token) throws Exception {
		StringBuffer sb = new StringBuffer();
		try {
			sb.append(Base64.getEncoder().encodeToString(timestamp.getBytes(UTF8)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new Exception("签名时间戳转换异常", e);
		}
		if (StringUtils.isNotBlank(token)) {
			sb.append(token);
		}
		// 拼接秘钥
		sb.append(SIGN_SECRET_KEY);
		String bodyContent = getBodyContent(request);
		JSONObject jsonObject = JSONObject.parseObject(bodyContent);
		List<String> nameList = null;
		if (Objects.nonNull(jsonObject) && !jsonObject.isEmpty()) {
			nameList = new ArrayList<String>(jsonObject.keySet());
			Collections.sort(nameList);
			for (String name : nameList) {
				if (!HeaderInterceptor.SIGNATURE.equals(name)) {
					sb.append(name);
					sb.append(jsonObject.get(name));
				}
			}
		}
		String signature = sb.toString();
		try {
			String md5Signature = DigestUtils.md5DigestAsHex(signature.getBytes(UTF8));
			log.info("签名字符串为:" + signature + " 签名为：" + md5Signature);
			return md5Signature;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new Exception("签名编码异常", e);
		}
	}

	/**
	 * 获取请求Body
	 *
	 * @param request
	 * @return
	 * @throws FeikuaiException
	 */
	public static String getBodyContent(ServletRequest request) throws Exception {
		StringBuilder sb = new StringBuilder();
		InputStream inputStream = null;
		BufferedReader reader = null;
		try {
			inputStream = request.getInputStream();
			reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
			String line = "";
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw new Exception("签名-获取bodyContent错误", e);
				}
			}
		}
		return sb.toString();
	}
}
