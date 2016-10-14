package com.dnk.smart.session;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionInfo {

	private Channel channel;
	private long valid;//有效时长
	private long login;//登录时间
	private volatile long update;//更新时间
	private volatile boolean pass;//是否通过验证

	private SessionInfo() {
	}

	public static SessionInfo build(Channel channel, long valid, long login, long update, boolean pass) {
		if (channel == null || valid < 1 || login < 1 || update < 1) {
			//throw new RuntimeException("params is invalid.");
			return null;
		}
		SessionInfo sessionInfo = new SessionInfo();

		sessionInfo.setChannel(channel);
		sessionInfo.setValid(valid);
		sessionInfo.setLogin(login);
		sessionInfo.setUpdate(update);
		sessionInfo.setPass(pass);

		return sessionInfo;
	}

	public static SessionInfo build(Channel channel, long valid) {
		return build(channel, valid, System.currentTimeMillis(), System.currentTimeMillis(), false);
	}

}
