package com.dnk.smart.session;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SessionInfo {
	private Channel channel;
	private Device device;
	private String sn;//设备号(目前只记录网关)
	private long create;//本次连接的创建时间
	private volatile long update;//更新时间(目前只用于网关心跳记录)
	private volatile boolean pass;//是否通过验证

	private SessionInfo() {
	}

	/**
	 * device,sn在接收登录请求后获取,pass在登录后更新
	 */
	//TODO min time
	private static SessionInfo build(Channel channel, Device device, String sn, long create, long update, boolean pass) {
		if (channel == null || create < 1 || update < 1) {
			throw new RuntimeException("params is invalid.");
		}
		SessionInfo sessionInfo = new SessionInfo();

		sessionInfo.setChannel(channel);
		sessionInfo.setDevice(device);
		sessionInfo.setSn(sn);
		sessionInfo.setCreate(create);
		sessionInfo.setUpdate(update);
		sessionInfo.setPass(pass);

		return sessionInfo;
	}

	/**
	 * 连接时初始化
	 */
	public static SessionInfo init(Channel channel) {
		return build(channel, null, null, System.currentTimeMillis(), System.currentTimeMillis(), false);
	}

	/**
	 * 通过登录后
	 */
	public SessionInfo pass(Device device, String sn) {
		if (device == null) {
			throw new RuntimeException("error type with device");
		}
		this.device = device;

		if (device == Device.GATEWAY) {
			if (sn == null || sn.isEmpty()) {
				throw new RuntimeException("gateway sn can't be empty.");
			}
			this.sn = sn;
		}

		this.pass = true;

		return this;
	}

	/**
	 * 更新活跃时间
	 */
	public SessionInfo active(long update) {
		this.update = update;
		return this;
	}

}
