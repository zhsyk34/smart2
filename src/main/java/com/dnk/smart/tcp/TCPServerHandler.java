package com.dnk.smart.tcp;

import com.dnk.smart.session.SessionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TCPServerHandler extends ChannelInboundHandlerAdapter {

	//private static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private static final Logger logger = LoggerFactory.getLogger(TCPServerHandler.class);

	@Override
	public boolean isSharable() {
		return true;
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.debug(">>>>>>>>>>>>>>>client " + ctx.channel().remoteAddress() + " closed.");
		SessionManager.remove(ctx.channel());
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.debug(">>>>>>>>>>>>>>>>>>>>client " + ctx.channel().remoteAddress() + " connected.");
		SessionManager.add(ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		logger.error(cause.getMessage());
		logger.debug(">>>>>>>>>>>>>>>>>>>>client " + ctx.channel().remoteAddress() + " error");
		ctx.channel().close();
		SessionManager.remove(ctx.channel());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//		logger.debug(">>>>>>>>>>>>>>>>>>>>>receive " + ctx.channel().remoteAddress() + " data:" + Util.read(msg));

		Process.process(ctx, msg);
		//TODO:根据数据add / update
//		ByteBuf buf = (ByteBuf) msg;
//		for (int i = buf.readerIndex(); i < buf.readableBytes(); i++) {
//			char c = (char) buf.readByte();
//			System.out.println(c);
//
//			if (c == 'a') {
//				GatewayManager.pass(ctx.channel(), "udid001");
//			}
//		}
		//GatewayManager.add(ctx.channel(), "");
		ctx.write(msg);
	}

//	@Override
//	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//		ctx.flush();
//	}

//	@Override
//	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//		if (evt instanceof IdleStateEvent) {
//			logger.debug(">>>>>>>>>>>>>>>>find client " + ctx.channel().remoteAddress() + " idle too long, and remove it");
//			ctx.channel().close();
//		}
//	}
}
