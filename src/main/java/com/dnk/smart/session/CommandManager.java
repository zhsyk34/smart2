package com.dnk.smart.session;

import com.dnk.smart.kit.CodecKit;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import static com.dnk.smart.config.Config.MESSAGE_SEND_AWAIT;

public class CommandManager {
	//key:dest
	private static final Map<String, BlockingQueue<Data>> map = new ConcurrentHashMap<>();

	@Getter
	@Setter
	private static class Data {
		private String src;//来源
		private String dest;//发送目标,网关sn
		private Object object;//发送数据
		private boolean send;//是否发送
		private long sendTime;//记录时间
	}

	public static boolean add(Data data) {
		BlockingQueue<Data> queue;
		String dest = data.getDest();

		if (map.containsKey(dest)) {
			queue = map.get(dest);
		} else {
			queue = new LinkedBlockingQueue<>();
		}

		try {
			queue.put(data);
		} catch (InterruptedException e) {
			return false;
		}

		return true;
	}

	public static void monitor() {
		map.forEach((sn, list) -> {
			Data data = list.peek();
			if (data != null && CodecKit.validateTime(data.getSendTime(), MESSAGE_SEND_AWAIT)) {
				list.poll();
			}
		});
	}

}
