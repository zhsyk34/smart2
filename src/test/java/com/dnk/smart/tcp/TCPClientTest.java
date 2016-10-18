package com.dnk.smart.tcp;

import com.dnk.smart.config.Config;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.dnk.smart.kit.CodecKit.encode;

public class TCPClientTest {

	private static List<Byte> convert(String cmd) {
		byte[] bytes = encode(cmd);

		Byte[] bs = new Byte[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			bs[i] = bytes[i];
		}

		return Arrays.asList(bs);
	}

	private static ByteBuf right() {
		return Unpooled.copiedBuffer(encode("{heart:bong}"));
	}

	private static ByteBuf msg() {
		List<Byte> list = new ArrayList<>();

//		list.addAll(convert("{a:1,b:2}"));
//		//header
//		list.add((byte) 90);
//		list.add((byte) -91);
//		//length=data+4
//		list.add((byte) 0);
//		list.add((byte) 5);
//		//data
//		list.add((byte) 123);//{
//		list.add((byte) 97);//a
//		list.add((byte) 58);//:
//		list.add((byte) 49);//1
//		list.add((byte) 125);//}
//		//verify
//		list.add((byte) 0);
//		list.add((byte) 102);
//		//footer
//		list.add((byte) -91);
//		list.add((byte) 90);

		int length = 1 + new Random().nextInt(50);
		System.err.println("send data length:" + length);
		for (int i = 0; i < length; i++) {
			if (i == 5) {
				list.add((byte) 0x5a);
			}
			list.add((byte) new Random().nextInt(127));
		}

		byte[] bs = new byte[list.size()];
		for (int i = 0; i < bs.length; i++) {
			bs[i] = list.get(i);
		}
		return Unpooled.copiedBuffer(bs);
	}

	private static volatile AtomicInteger count = new AtomicInteger(0);

	public static void start() {
		Bootstrap bootstrap = new Bootstrap();
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			bootstrap.group(group).channel(NioSocketChannel.class);
			bootstrap.option(ChannelOption.TCP_NODELAY, true);

			bootstrap.handler(new SimpleChannelInboundHandler() {

				@Override
				public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
					count.incrementAndGet();
					if (count.get() != 0 && count.get() % 100 == 0) {
						System.out.println("-----------" + (count.get()) + " schedule end.-------------------");
					}
					ctx.channel().close();
				}
			});

			ChannelFuture future = bootstrap.connect(Config.LOCAL_HOST, Config.TCP_SERVER_PORT).sync();
			Channel channel = future.channel();

			channel.writeAndFlush(right());
			channel.closeFuture().sync();

//			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//			while (true) {
//				String line = in.readLine();
//
//				if (line.equals("y")) {
//					channel.writeAndFlush(right());
//				}
//				if (line.equals("n")) {
//					channel.writeAndFlush(msg());
//				}
//			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}

	public static void main(String[] args) {

		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

		Runnable taskGroup = () -> {
			System.err.println("-----------schedule begin.-------------------");
			ExecutorService service = Executors.newCachedThreadPool();

			for (int i = 0; i < 200; i++) {
				service.submit(() -> start());
			}

			service.shutdown();
			if (service.isTerminated()) {
				System.err.println("-----------schedule end.-------------------");
			}
		};

		executorService.scheduleAtFixedRate(taskGroup, 2, 30, TimeUnit.SECONDS);

	}
}
