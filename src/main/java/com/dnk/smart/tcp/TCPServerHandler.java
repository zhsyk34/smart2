package com.dnk.smart.tcp;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;

public class TCPServerHandler extends ChannelHandlerAdapter {

	//private static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	@Override
	public boolean isSharable() {
		return true;
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//		System.err.println(">>>>>>>>>>>>>>>client " + ctx.channel().remoteAddress() + " closed.");
		//SessionMonitor.AppSessionManager.remove(ctx.channel());
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
//		System.err.println(">>>>>>>>>>>>>>>>>>>>client " + ctx.channel().remoteAddress() + " connected.");
		//SessionMonitor.AppSessionManager.add(ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//		cause.printStackTrace();
//		System.err.println(">>>>>>>>>>>>>>>>>>>>client " + ctx.channel().remoteAddress() + " error");
		ctx.channel().close();
		//SessionMonitor.AppSessionManager.remove(ctx.channel());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//		ByteBuf buf = (ByteBuf) msg;
//		System.out.println("handler ref:" + buf.refCnt());
//		System.err.println(">>>>>>>>>>>>>>>>>>>>>receive " + ctx.channel().remoteAddress() + " data:" + Util.read(msg));
		ctx.write(msg);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
//			System.err.println(">>>>>>>>>>>>>>>>find client " + ctx.channel().remoteAddress() + " idle too long, and remove it");
			ctx.channel().close();
		}
	}
}
