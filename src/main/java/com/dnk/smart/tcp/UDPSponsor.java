package com.dnk.smart.tcp;

import com.dnk.smart.config.Config;
import com.dnk.smart.kit.CodecKit;
import com.dnk.smart.kit.GsonKit;
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
 * TCP-SERVER 通UDP单播唤醒(通知)网关登录(主动发起到本服务器的TCP连接)
 */
public class UDPSponsor {

	private static final Logger LOGGER = LoggerFactory.getLogger(UDPSponsor.class);

	private static final String ACTION = "action";
	private static final String LOGIN_REQ = "loginReady";

	private static final byte[] LOGIN_COMMAND = CodecKit.encode(GsonKit.toJson(ACTION, LOGIN_REQ));

	public static void send(String host, int port, Object msg) {
		Bootstrap bootstrap = new Bootstrap();
		EventLoopGroup group = new NioEventLoopGroup();

		bootstrap.group(group).channel(NioDatagramChannel.class);
		bootstrap.option(ChannelOption.SO_BROADCAST, false);

		bootstrap.handler(new SimpleChannelInboundHandler<DatagramPacket>() {
			@Override
			protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
				//Do nothing.
			}
		});

		if (host == null || host.isEmpty()) {
			host = Config.BROADCAST_HOST;
			LOGGER.warn("use broadcast :" + host);
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
