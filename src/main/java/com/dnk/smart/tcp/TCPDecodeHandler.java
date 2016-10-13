package com.dnk.smart.tcp;

import com.dnk.smart.debug.Util;
import com.dnk.smart.kit.ByteKit;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.dnk.smart.kit.CodecKit.validateVerify;

public class TCPDecodeHandler extends ByteToMessageDecoder {

	private static final Logger LOGGER = LoggerFactory.getLogger(TCPDecodeHandler.class);

	private static final byte[] HEADER = new byte[]{0x5A, (byte) 0xA5};
	private static final byte[] FOOTER = new byte[]{(byte) 0xA5, 0x5A};

	private static int LENGTH_BYTES = 2, DATA_MIN_BYTES = 5, VERIFY_BYTES = 2;
	private static int MSG_MIN_LENGTH = HEADER.length + LENGTH_BYTES + DATA_MIN_BYTES + VERIFY_BYTES + FOOTER.length;

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		//logger
		LOGGER.debug("--------------------------------------");
		LOGGER.debug("search data from index[" + in.readerIndex() + "] - [" + (in.readerIndex() + in.readableBytes()) + "]");
		LOGGER.debug(Util.readByIndex(in));

		//invalid
		if (in.readableBytes() < MSG_MIN_LENGTH) {
			LOGGER.debug("等待数据中...数据至少应有" + MSG_MIN_LENGTH + "位");
			return;
		}

		//header
		int index = in.indexOf(in.readerIndex(), in.readerIndex() + in.readableBytes() - 1, HEADER[0]);
		if (index == -1) {
			in.clear();
			LOGGER.debug("没有匹配到合法数据,清除已接收到的数据");
			return;
		}

		LOGGER.debug("匹配到第一个帧头位置:" + index);

		if (in.readableBytes() <= MSG_MIN_LENGTH) {
			LOGGER.debug("数据不完整(粗略估计),继续等待中...");
			return;
		}

		in.readerIndex(index + 1);
		if (in.readByte() != HEADER[1]) {
			LOGGER.debug("第二个帧头数据不匹配,丢弃此前数据:");
			in.readerIndex(index + 1);
			return;
		}

		LOGGER.debug("匹配到第二个帧头位置:" + (index + 1));

		//length
		int length = ByteKit.byteArrayToInt(new byte[]{in.readByte(), in.readByte()});
		LOGGER.debug("校验长度:[" + length + "], 数据长度:[" + (length - 4) + "]");
		if (length < 4) {
			LOGGER.debug("长度校验数据校验错误,继续从下个位置:[" + (index + 1) + "]开始查找");
			in.readerIndex(index + 1);
			return;
		}

		if (in.readableBytes() < length) {
			LOGGER.debug("数据不完整(校验长度),继续等待中...");
			in.readerIndex(index);
			return;
		}

		//data
		byte[] data = in.readBytes(length - 4).array();

		//verify
		if (!validateVerify(data, new byte[]{in.readByte(), in.readByte()})) {
			LOGGER.debug("校验值错误");
			return;
		}

		//footer
		if (in.readByte() == FOOTER[0] && in.readByte() == FOOTER[1]) {
			LOGGER.debug("帧尾校验通过,获取数据:" + new String(data));
			out.add(in.slice(index, HEADER.length + length + FOOTER.length).retain());
			return;
		}

		LOGGER.debug("帧尾数据错误,继续从下个位置:[" + (index + 1) + "]开始查找");
		in.readerIndex(index + 1);
	}

}
