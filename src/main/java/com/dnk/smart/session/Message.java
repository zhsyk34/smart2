package com.dnk.smart.session;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
final class Message {
	private String dest;//发送目标(key)
	//private String id;//==dest
	private String src;//来源
	private Object data;//发送数据
	private boolean send;//是否发送
	private long time;//开始发送时间
}