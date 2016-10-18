package com.dnk.smart.udp;

import com.dnk.smart.config.Config;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * UDP单播服务
 * 用于TCP-SERVER 唤醒(通知)网关登录(主动发起到本服务器的TCP连接)
 */
public class UDPSponsor {

	private static final Logger logger = LoggerFactory.getLogger(UDPSponsor.class);

	private static final String ACTION = "action";
	private static final String LOGIN_REQ = "loginReady";

	//	private static final byte[] LOGIN_COMMAND = CodecKit.encode(GsonKit.toJson(ACTION, LOGIN_REQ));
	private static final String LOGIN_COMMAND = "{\"action\":\"loginReady\"}";

	public static void send(String host, int port, Object msg) {
		Bootstrap bootstrap = new Bootstrap();
		EventLoopGroup group = new NioEventLoopGroup();

		bootstrap.group(group).channel(NioDatagramChannel.class);
		bootstrap.option(ChannelOption.SO_BROADCAST, false);

		bootstrap.handler(new SimpleChannelInboundHandler<DatagramPacket>() {
			@Override
			protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {

			}
		});

		if (host == null || host.isEmpty()) {
//			host = Config.BROADCAST_HOST;
			host = Config.LOCAL_HOST;
			logger.warn("use broadcast :" + host);
		}
		try {
			Channel channel = bootstrap.bind(0).syncUninterruptibly().channel();
			ByteBuf buf;
			if (msg instanceof byte[]) {
				buf = Unpooled.copiedBuffer((byte[]) msg);
			} else if (msg instanceof String) {
				buf = Unpooled.copiedBuffer((String) msg, CharsetUtil.UTF_8);
			} else {
				return;
			}
			channel.writeAndFlush(new DatagramPacket(buf, new InetSocketAddress(host, port))).sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}

	/**
	 * UDP唤醒指定网关
	 */
	public static void awake(String host, int port) {
		send(host, port, LOGIN_COMMAND);
	}
}
