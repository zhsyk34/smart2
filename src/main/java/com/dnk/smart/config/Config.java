package com.dnk.smart.config;

public class Config {

	public static final String LOCAL_HOST = "127.0.0.1";

	public static final String BROADCAST_HOST = "255.255.255.255";

	//

	public static final int TCP_SERVER_PORT = 15999;

	public static final int UDP_SERVER_PORT = 15998;

	public static final int SERVER_BACKLOG = 1024;

	public static final int DEFAULT_UDP_CLIENT_PORT = 3002;

	/**
	 * -----------------以下为超时设置,单位秒------------------
	 */
	public static final int APP_TIME_OUT = 10;

	//登录用时
	public static final int LOGIN_TIME_OUT = 5;

	public static final int GATEWAY_TIME_OUT = 10;

	//信息发送后最长等待时间
	public static final int MESSAGE_SEND_AWAIT = 15;

}
