package com.dnk.smart.tcp;

import com.dnk.smart.debug.Util;
import com.dnk.smart.kit.ByteKit;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.dnk.smart.config.MessageFormat.*;
import static com.dnk.smart.kit.CodecKit.validateVerify;

/**
 * 解码接收到的数据
 */
public class TCPDecodeHandler extends ByteToMessageDecoder {

	private static final Logger logger = LoggerFactory.getLogger(TCPDecodeHandler.class);

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		//logger
		logger.debug("--------------------------------------");
		logger.debug("search data from index[" + in.readerIndex() + "] - [" + (in.readerIndex() + in.readableBytes()) + "]");
		logger.debug(Util.readByIndex(in));

		//invalid
		if (in.readableBytes() < MSG_MIN_LENGTH) {
			logger.debug("等待数据中...数据至少应有" + MSG_MIN_LENGTH + "位");
			return;
		}

		//header:1
		int index = in.indexOf(in.readerIndex(), in.readerIndex() + in.readableBytes() - 1, HEADER[0]);
		if (index == -1) {
			in.clear();
			logger.debug("没有匹配到合法数据,清除已接收到的数据");
			return;
		}
		logger.debug("匹配到第一个帧头位置:" + index);

		//length-fuzzy
		if (in.readableBytes() < MSG_MIN_LENGTH) {
			logger.debug("数据不完整(依据数据长度粗略估计),继续等待中...");
			return;
		}

		//header:2
		in.readerIndex(index + 1);
		if (in.readByte() != HEADER[1]) {
			logger.debug("第二个帧头数据不匹配,丢弃此前数据:");
			in.readerIndex(index + 1);
			return;
		}
		logger.debug("匹配到第二个帧头位置:" + (index + 1));

		//length-exact
		int length = ByteKit.byteArrayToInt(new byte[]{in.readByte(), in.readByte()});
		//真正的数据长度
		int actual = length - LENGTH_BYTES - VERIFY_BYTES;
		logger.debug("校验长度:[" + length + "], 数据长度应为:[" + actual + "]");
		if (length < LENGTH_BYTES + DATA_MIN_BYTES + VERIFY_BYTES) {
			logger.debug("长度校验数据校验错误,继续从下个位置:[" + (index + 1) + "]开始查找");
			in.readerIndex(index + 1);
			return;
		}

		if (in.readableBytes() < length) {
			logger.debug("数据不完整(校验长度),继续等待中...");
			in.readerIndex(index);
			return;
		}

		//data
		byte[] data = new byte[actual];
		in.readBytes(actual).getBytes(0, data);

		//verify
		if (!validateVerify(data, new byte[]{in.readByte(), in.readByte()})) {
			logger.debug("校验值错误,继续从下个位置:[" + (index + 1) + "]开始查找");
			in.readerIndex(index + 1);
			return;
		}

		//footer
		if (in.readByte() == FOOTER[0] && in.readByte() == FOOTER[1]) {
			logger.debug("帧尾校验通过,获取数据:" + new String(data));
			out.add(in.slice(index, HEADER.length + length + FOOTER.length).retain());
			return;
		}

		logger.debug("帧尾数据错误,继续从下个位置:[" + (index + 1) + "]开始查找");
		in.readerIndex(index + 1);
	}

}
