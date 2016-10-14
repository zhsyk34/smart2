package com.dnk.smart.tcp;

import com.dnk.smart.debug.Util;
import com.dnk.smart.session.GatewayManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TCPServerHandler extends ChannelHandlerAdapter {

	//private static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private static final Logger LOGGER = LoggerFactory.getLogger(TCPServerHandler.class);

	@Override
	public boolean isSharable() {
		return true;
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		LOGGER.debug(">>>>>>>>>>>>>>>client " + ctx.channel().remoteAddress() + " closed.");
		GatewayManager.remove(ctx.channel());
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		LOGGER.debug(">>>>>>>>>>>>>>>>>>>>client " + ctx.channel().remoteAddress() + " connected.");
		GatewayManager.add(ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		//super.exceptionCaught(ctx, cause);
		LOGGER.error(cause.getMessage());
		LOGGER.debug(">>>>>>>>>>>>>>>>>>>>client " + ctx.channel().remoteAddress() + " error");
		ctx.channel().close();
		GatewayManager.remove(ctx.channel());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		LOGGER.debug(">>>>>>>>>>>>>>>>>>>>>receive " + ctx.channel().remoteAddress() + " data:" + Util.read(msg));

		//TODO:根据数据add / update
		ByteBuf buf = (ByteBuf) msg;
		for (int i = buf.readerIndex(); i < buf.readableBytes(); i++) {
			char c = (char) buf.readByte();
			System.out.println(c);

			if (c == 'a') {
				GatewayManager.pass(ctx.channel(), "udid001");
			}
		}
		//GatewayManager.add(ctx.channel(), "");
		ctx.write(msg);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

//	@Override
//	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//		if (evt instanceof IdleStateEvent) {
//			LOGGER.debug(">>>>>>>>>>>>>>>>find client " + ctx.channel().remoteAddress() + " idle too long, and remove it");
//			ctx.channel().close();
//		}
//	}
}
