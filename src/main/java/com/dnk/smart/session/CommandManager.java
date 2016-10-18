package com.dnk.smart.session;

import com.dnk.smart.config.Config;
import com.dnk.smart.kit.CodecKit;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.*;

public class CommandManager {

	private static final Map<String, BlockingQueue<Message>> MAP = new ConcurrentHashMap<>();

	@Getter
	@Setter
	private static class Message {
		private String id;//
		private String src;//来源
		private String dest;//发送目标
		private Object data;//发送数据
		private boolean send;//是否发送
		private long time;//开始发送时间
	}

	public static boolean add(Message message) {
		BlockingQueue<Message> queue;
		String dest = message.getDest();

		synchronized (MAP) {
			if (MAP.containsKey(dest)) {
				queue = MAP.get(dest);
			} else {
				queue = new LinkedBlockingQueue<>();
			}
		}

		try {
			queue.put(message);
		} catch (InterruptedException e) {
			return false;
		}

		return true;
	}

	/**
	 * 移除响应时间超时(自发送起**秒内未及时回复)的消息
	 */
	public static void monitor() {
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		Runnable task = () -> {
			while (true) {
				MAP.forEach((dest, list) -> {
					Message data = list.peek();
					if (data != null && data.isSend() && CodecKit.validateTime(data.getTime(), Config.MESSAGE_SEND_AWAIT)) {
						list.poll();
					}
				});
			}
		};
		service.schedule(task, 0, TimeUnit.SECONDS);
	}

}
