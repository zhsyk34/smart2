package com.dnk.smart.tcp;

import com.dnk.smart.kit.CodecKit;
import com.dnk.smart.kit.GsonKit;
import com.dnk.smart.session.SessionManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Process {

	private static final Logger logger = LoggerFactory.getLogger(TCPDecodeHandler.class);

	public static void process(ChannelHandlerContext ctx, Object msg) {

		if (!(msg instanceof ByteBuf)) {
			logger.error("data error.");
		}

		ByteBuf in = (ByteBuf) msg;

		String data = CodecKit.decode(in);
		System.out.println("command data:" + data);

		String action = null;
		try {
			action = GsonKit.getString(data, "action");
			System.out.println(action);
		} catch (Exception e) {
		}

		if (action == null || action.isEmpty()) {
			System.err.println("没有有效的指令");
			in.clear();
		}

		if ("loginReady".equals(action)) {
			System.out.println("登录请求");
			login(ctx.channel());
		}

//		String request = (String) msg;//TODO
//
//		//action:loginReq
//		if ("loginReq".equals(request)) {
//			String sn;
//			String clientType = "";
//			String devSn;
//			String UDPPort;
//
//			int type = Integer.parseInt(clientType);
//			SessionInfo.Device device = SessionInfo.Device.get(type);
//		} else {
//			int type = type(ctx.channel());//TODO 根据channel判断登录类型
//		}

//		ByteBuf buf = (ByteBuf) msg;
//		for (int i = buf.readerIndex(); i < buf.readableBytes(); i++) {
//			char c = (char) buf.getByte(i);
//			if (c != '\n' && c != '\r') {
//				System.out.print(c + " ");
//			}
//		}
//		System.out.println();

//		ctx.write(msg);

		Channel channel = SessionManager.get(ctx.channel()).getChannel();
		channel.writeAndFlush(Unpooled.wrappedBuffer((ByteBuf) msg));
	}

	private static void login(Channel channel) {

	}

	//TODO
	private static int type(Channel channel) {
		return 1;
	}
}
