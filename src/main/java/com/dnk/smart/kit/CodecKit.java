package com.dnk.smart.kit;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import static com.dnk.smart.kit.ByteKit.compare;
import static com.dnk.smart.kit.ByteKit.smallIntToByteArray;

public class CodecKit {
	/**
	 * ---------------------以下是编码部分--------------------------
	 */

	//校验码数值
	private static int verify(byte[] data) {
		if (data == null || data.length == 0) {
			throw new RuntimeException("data is empty.");
		}
		int value = data.length;
		for (int i = 0; i < data.length; i++) {
			value += data[i] & 0xff;
		}
		return value;
	}

	//长度编码:数据部分长度+2 字节（长度）+2 字节（校验）
	private static byte[] encodeLength(byte[] data) {
		return smallIntToByteArray(data.length + 4);
	}

	//校验编码：计算内容包括：长度、数据部分，计算方式：求和取低 2 个字节
	private static byte[] encodeVerify(byte[] data) {
		int value = verify(data);
		return smallIntToByteArray(value);
	}

	//编码发送数据
	public static byte[] encode(String cmd) {
		byte[] data = cmd.getBytes();
		ByteBuf buffer = Unpooled.buffer(data.length + 8);

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
}
