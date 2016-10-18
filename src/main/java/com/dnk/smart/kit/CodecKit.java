package com.dnk.smart.kit;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

import static com.dnk.smart.config.MessageFormat.*;
import static com.dnk.smart.kit.ByteKit.compare;
import static com.dnk.smart.kit.ByteKit.smallIntToByteArray;

public class CodecKit {

	private static final int MARK = 0xff;
	private static final int REDUNDANT = HEADER.length + LENGTH_BYTES + VERIFY_BYTES + FOOTER.length;

	/**
	 * ---------------------以下是编码部分--------------------------
	 */

	//长度编码:数据部分长度+2字节(长度)+2字节(校验)
	private static byte[] encodeLength(byte[] data) {
		return smallIntToByteArray(data.length + LENGTH_BYTES + VERIFY_BYTES);
	}

	//校验码数值
	private static int verify(byte[] data) {
		if (data == null || data.length == 0) {
			throw new RuntimeException("data is empty.");
		}
		int value = data.length;
		for (int i = 0; i < data.length; i++) {
			value += data[i] & MARK;
		}
		return value;
	}

	//校验编码:计算内容包括长度、数据部分(求和取低2个字节)
	private static byte[] encodeVerify(byte[] data) {
		int value = verify(data);
		return smallIntToByteArray(value);
	}

	//编码发送数据
	public static byte[] encode(String cmd) {
		byte[] data = cmd.getBytes();
		ByteBuf buffer = Unpooled.buffer(data.length + REDUNDANT);

		//header:2
		buffer.writeByte(0x5A).writeByte(0xA5);
		//length:2
		buffer.writeBytes(encodeLength(data));
		//data
		buffer.writeBytes(data);
		//verify:2
		buffer.writeBytes(encodeVerify(data));
		//footer:2
		buffer.writeByte(0xA5).writeByte(0x5A);

		return buffer.array();
	}

	/**
	 * ----------------解码部分-------------------
	 */

	//验证校验码
	public static boolean validateVerify(byte[] data, byte[] verifyArr) {
		return compare(encodeVerify(data), verifyArr);
	}

	/**
	 * @param refer 参照时间
	 * @param valid 自参照时间起的有效时长
	 * @param unit  有效时长单位
	 * @return 是否在有效时间内
	 */
	public static boolean validateTime(long refer, long valid, TimeUnit unit) {
		return System.currentTimeMillis() - refer > unit.toMillis(valid);
	}

	public static boolean validateTime(long refer, long valid) {
		return validateTime(refer, valid, TimeUnit.SECONDS);
	}

	//TODO
	public static String decode(byte[] bytes) {
		if (bytes == null || bytes.length < MSG_MIN_LENGTH) {
			return null;
		}
		return new String(bytes, HEADER.length + LENGTH_BYTES, bytes.length - REDUNDANT, CharsetUtil.UTF_8);
	}

	//util
	private static byte[] getBytes(ByteBuf buf) {
		if (buf.hasArray()) {
			return buf.array();
		}
		byte[] bytes = new byte[buf.readableBytes()];
		buf.getBytes(buf.readerIndex(), bytes);
		return bytes;
	}

	public static String decode(ByteBuf buf) {
		if (buf == null || buf.readableBytes() < MSG_MIN_LENGTH) {
			return null;
		}
		return buf.toString(HEADER.length + LENGTH_BYTES, buf.readableBytes() - REDUNDANT, CharsetUtil.UTF_8);
	}

}
