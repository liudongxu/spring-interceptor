package io.feikuai.controller;

import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.feikuai.annotation.Access;
import io.feikuai.param.PHeader;
import lombok.extern.slf4j.Slf4j;

/**
 * 测试
 * @author liudo
 * @date 2018/12/26
 */
@RequestMapping("/")
@RestController
@Slf4j
public class DemoController {
	/**
	 * 首页
	 * <p>不用登录
	 * @return
	 */
	@Access(isLogin = false)
	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseBody
	public String index() {
		return "hello  interceptor....";
	}

	/**
	 * 登录
	 * @return
	 */
	@Access(isLogin = false)
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public String login() {
		String token = "ef7c525b-08d5-11e9-836d-000c29b3e30b";
		log.info("token:" + token);
		return token;
	}

	/**
	 * 获取信息
	 * <p>:需要登录后访问
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/info", method = RequestMethod.POST)
	@ResponseBody
	public String info(@RequestAttribute PHeader header) {
		log.info("获取新成功，userId:" + header.getUserId());
		log.info("token:" + header.getToken());
		return "success";
	}
}
