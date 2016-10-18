package com.dnk.smart.session;

import com.dnk.smart.config.Config;
import com.dnk.smart.kit.CodecKit;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SessionManager {

	private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);

	private static final Map<String, SessionInfo> ACCEPT = new ConcurrentHashMap<>();
	private static final Map<String, SessionInfo> APP = new ConcurrentHashMap<>();
	private static final Map<String, SessionInfo> GATEWAY = new ConcurrentHashMap<>();

	//
	private static String id(Channel channel) {
		return channel.id().asLongText();
	}

	/**
	 * 初始连接时保存数据
	 */
	public static void add(Channel channel) {
		SessionInfo sessionInfo = SessionInfo.init(channel);
		ACCEPT.put(id(channel), sessionInfo);
	}

	/**
	 * 登录后更新数据
	 *
	 * @param type 设备类型
	 * @param sn   设备sn号(网关必要信息udid)
	 */
	public static void pass(Channel channel, int type, String sn) {
		String id = id(channel);

		synchronized (ACCEPT) {
			if (ACCEPT.containsKey(id)) {
				SessionInfo sessionInfo = ACCEPT.remove(id);

				sessionInfo.pass(type, sn);
				switch (sessionInfo.getDevice()) {
					case APP:
						APP.put(id, sessionInfo);
						break;
					case GATEWAY:
						GATEWAY.put(sn, sessionInfo);
						break;
				}
				logger.debug("登录成功");
			}
		}
	}

	/**
	 * 关闭连接并删除数据
	 */
	public static void remove(String id) {
		SessionInfo sessionInfo = null;
		synchronized (APP) {
			if (APP.containsKey(id)) {
				sessionInfo = APP.remove(id);
			} else {
				synchronized (GATEWAY) {
					if (GATEWAY.containsKey(id)) {
						sessionInfo = GATEWAY.remove(id);
					}
				}
			}
		}

		if (sessionInfo != null) {
			Channel channel = sessionInfo.getChannel();
			if (channel != null && channel.isOpen()) {
				channel.close();
			}
		}
	}

	/**
	 * 关闭连接并删除数据
	 */
	public static void remove(Channel channel) {
		remove(id(channel));
	}

	/**
	 * 更新update时间
	 */
	public static void update(Channel channel) {
		synchronized (MAP) {
			if (contains(channel)) {
				SessionInfo sessionInfo = get(channel);
				sessionInfo.setUpdate(System.currentTimeMillis());
				logger.debug("更新成功");
			}
		}
	}

	private static boolean contains(Channel channel) {
		return MAP.containsKey(id(channel));
	}

	/**
	 * 启动线程扫描并移除
	 * 1.登录超时的连接
	 * 2.在线超时的连接
	 */
	public static void monitor() {
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

		Runnable task = () -> {
			while (true) {
				//TODO:TEST
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				logger.debug("当前连接:[" + MAP.size() + "]");
				MAP.forEach((id, sessionInfo) -> {
					long login = sessionInfo.getCreate();

					//登录超时
					if (!sessionInfo.isPass() && CodecKit.validateTime(login, Config.LOGIN_TIME_OUT)) {
						logger.debug("超时未登录");
						remove(id);
					}

					//在线超时
					SessionInfo.Device device = sessionInfo.getDevice();
					if (device != null) {
						long valid = device == SessionInfo.Device.APP ? Config.APP_TIME_OUT : Config.GATEWAY_TIME_OUT;

						if (CodecKit.validateTime(login, valid)) {
							logger.debug("在线时长已到,移除...");
							remove(id);
						}
					}
				});
			}
		};
		service.schedule(task, 0, TimeUnit.SECONDS);
	}

//	/**
//	 * 获取连接信息
//	 */
//	public static SessionInfo get(Channel channel) {
//		return MAP.get(id(channel));
//	}
//
//	/**
//	 * 通过指定sn获取连接信息
//	 */
//	public static SessionInfo get(String sn) {
//		if (sn == null || sn.isEmpty()) {
//			return null;
//		}
//		synchronized (MAP) {
//			for (SessionInfo sessionInfo : MAP.values()) {
//				if (sn.equals(sessionInfo.getSn())) {
//					return sessionInfo;
//				}
//			}
//		}
//		return null;
//	}

}
