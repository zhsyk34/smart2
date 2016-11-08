package com.dnk.smart.tcp;

import com.dnk.smart.session.SessionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TCPServerHandler extends ChannelInboundHandlerAdapter {

	//private static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private static final Logger logger = LoggerFactory.getLogger(TCPServerHandler.class);

//	private static final AttributeKey<Long> create = AttributeKey.newInstance("create");

	@Override
	public boolean isSharable() {
		return true;
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.debug(">>>>>>>>>>>>>>>client " + ctx.channel().remoteAddress() + " closed.");
		SessionManager.close(ctx.channel());
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.debug(">>>>>>>>>>>>>>>>>>>>client " + ctx.channel().remoteAddress() + " connected.");

		/*Channel channel = ctx.channel();
		if (!channel.hasAttr(create)) {
			Attribute<Long> attr = channel.attr(create);
			attr.set(System.currentTimeMillis());
		}*/
		SessionManager.add(ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//		cause.printStackTrace();
		logger.error(cause.getMessage());
		logger.debug(">>>>>>>>>>>>>>>>>>>>client " + ctx.channel().remoteAddress() + " error");
		SessionManager.close(ctx.channel());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		Process.process(ctx, msg);
		//TODO:根据数据add / update
		ctx.write(msg);
	}
}
