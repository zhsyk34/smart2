package com.dnk.smart.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

/**
 * 模拟网关监听UDP端口
 */

public class GatewayUDPTest {
	public static final AttributeKey<InetSocketAddress> TARGET_ADDRESS = AttributeKey.valueOf("TARGET_ADDRESS");

	public void bind(int port) {
		Bootstrap bootstrap = new Bootstrap();
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			bootstrap.group(group).channel(NioDatagramChannel.class);
			bootstrap.option(ChannelOption.SO_BROADCAST, false);

			bootstrap.handler(new SimpleChannelInboundHandler<DatagramPacket>() {
				@Override
				protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
					String data = msg.content().toString(CharsetUtil.UTF_8);
					InetSocketAddress sender = msg.sender();
					System.out.println("receive " + sender + " [" + data + "]");
//					data = data.replaceAll("\r|\n", "");
//

					//TODO:loginReady
//                    if ("loginReady".equals(data)) {
//                        System.out.println("ready to connect tcp server");
//                        //TODO one bind twice or more
//                        new GatewayTCP().start();
//                    }
				}
			});

//			bootstrap.localAddress(new InetSocketAddress(Config.LOCAL_HOST, port));
//			Channel channel = bootstrap.bind().syncUninterruptibly().channel();
			Channel channel = bootstrap.bind(port).syncUninterruptibly().channel();
			System.out.println(port + " bind in gateway udp");
			channel.closeFuture().await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}

	public static void main(String[] args) {
		for (int i = 0; i < 5; i++) {
			final int k = i;
			new Thread(() -> new GatewayUDPTest().bind(50000 + k)).start();
		}
	}
}
