package io.feikuai.param;

import java.io.Serializable;

import lombok.Data;

/**
 * 请求头部信息类
 * @author liudo
 * @date 2018/12/26
 */
@Data
public class PHeader implements Serializable {


	private static final long serialVersionUID = -1651894097180313312L;
	/**
	 * 令牌
	 */
	private String token;
	/**
	 * 用户ID
	 */
	private String userId;

}
