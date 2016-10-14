package com.dnk.smart.session;

import com.dnk.smart.kit.CodecKit;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.dnk.smart.config.Config.GATEWAY_TIME_OUT;
import static com.dnk.smart.config.Config.LOGIN_TIME_OUT;

public class GatewayManager {

	private static final Map<String, SessionInfo> GATEWAY_SESSION_MAP = new ConcurrentHashMap<>();

	public static void monitor() {
		System.out.println("当前在线网关:[" + GATEWAY_SESSION_MAP.size() + "]");
		GATEWAY_SESSION_MAP.forEach((sn, sessionInfo) -> {
			long login = sessionInfo.getLogin();
			if (CodecKit.validateTime(login, LOGIN_TIME_OUT) && !sessionInfo.isPass()) {
				System.out.println("超时未登录");
				remove(sn);
			}
			if (CodecKit.validateTime(login, GATEWAY_TIME_OUT)) {
				System.out.println("在线时长已到,移除...");
				remove(sn);
			}
		});
	}

	//默认key:channel id
	public static void add(Channel channel) {
		SessionInfo sessionInfo = SessionInfo.build(channel, GATEWAY_TIME_OUT * 1000);
		if (sessionInfo != null) {
			GATEWAY_SESSION_MAP.put(channel.id().asLongText(), sessionInfo);
		}
	}

	//通过验证
	public static void pass(Channel channel, String id) {
		String key = channel.id().asLongText();
		synchronized (GATEWAY_SESSION_MAP) {
			if (GATEWAY_SESSION_MAP.containsKey(key)) {
				SessionInfo sessionInfo = GATEWAY_SESSION_MAP.get(key);
				if (CodecKit.validateTime(sessionInfo.getLogin(), LOGIN_TIME_OUT)) {
//				System.err.println("登录超时,关闭连接");
//				remove(id);
				} else {
					GATEWAY_SESSION_MAP.remove(key);
					sessionInfo.setPass(true);
					GATEWAY_SESSION_MAP.put(id, sessionInfo);
					System.out.println("网关登录成功");
				}
			}
		}
	}

	//只更新update时间
	public static void update(Channel channel) {
		for (Map.Entry<String, SessionInfo> entry : GATEWAY_SESSION_MAP.entrySet()) {
			SessionInfo sessionInfo = entry.getValue();
			Channel client = sessionInfo.getChannel();
			if (channel.equals(client)) {
				sessionInfo.setUpdate(System.currentTimeMillis());
				break;
			}
		}
	}

	//移除指定sn的网关
	public static void remove(String id) {
		SessionInfo sessionInfo = GATEWAY_SESSION_MAP.get(id);
		Channel channel = sessionInfo.getChannel();
		if (channel.isOpen()) {
			channel.close();
		}
		GATEWAY_SESSION_MAP.remove(id);
	}

	//根据指定(关闭)的连接移除
	public static void remove(Channel channel) {
		for (Map.Entry<String, SessionInfo> entry : GATEWAY_SESSION_MAP.entrySet()) {
			if (channel.equals(entry.getValue().getChannel())) {
				remove(entry.getKey());
				break;
			}
		}
	}
}
