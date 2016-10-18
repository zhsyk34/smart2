package com.dnk.smart.session;

import com.dnk.smart.config.Config;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SessionInfo {

	@Getter
	public enum Device {
		GATEWAY(0, "智能网关"), APP(1, "手机应用程序");

		private int type;
		private String description;

		Device(int type, String description) {
			this.type = type;
			this.description = description;
		}

		public static Device get(int type) {
			for (Device device : Device.values()) {
				if (device.getType() == type) {
					return device;
				}
			}
			return null;
		}
	}

	/*public static final Device APP = Device.APP;
	public static final Device GATEWAY = Device.GATEWAY;*/

	private Channel channel;
	private Device device;
	private String sn;//设备号(目前只记录网关)
	private long valid;//有效时长,根据连接类型区分
	private long create;//本次连接的创建时间
	private volatile long update;//连接(接收到数据后)更新时间,主要用于更新心跳时间?
	private volatile boolean pass;//是否通过验证

	private SessionInfo() {
	}

	/**
	 * device,sn,valid在接收登录请求后获取,pass在登录后更新
	 */
	private static SessionInfo build(Channel channel, Device device, String sn, long valid, long create, long update, boolean pass) {
		if (channel == null || create < 1 || update < 1) {
			throw new RuntimeException("params is invalid.");
		}
		SessionInfo sessionInfo = new SessionInfo();

		sessionInfo.setChannel(channel);
		sessionInfo.setDevice(device);
		sessionInfo.setSn(sn);
		sessionInfo.setValid(valid);
		sessionInfo.setCreate(create);
		sessionInfo.setUpdate(update);
		sessionInfo.setPass(pass);

		return sessionInfo;
	}

	/**
	 * 连接时初始化
	 */
	public static SessionInfo build(Channel channel) {
		return build(channel, null, null, -1, System.currentTimeMillis(), System.currentTimeMillis(), false);
	}

	/**
	 * 通过登录后
	 */
	public SessionInfo update(Device device, String sn) {
		this.device = device;
		this.sn = sn;
		this.valid = device == Device.APP ? Config.APP_TIME_OUT : Config.GATEWAY_TIME_OUT;

		this.pass = true;
		return this;
	}

	public SessionInfo update(int type, String sn) {
		Device device = Device.get(type);
		if (device == null) {
			throw new RuntimeException("error type with device");
		}
		return update(device, sn);
	}

}
