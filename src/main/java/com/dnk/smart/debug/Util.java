package com.dnk.smart.debug;

import com.dnk.smart.kit.ByteKit;
import io.netty.buffer.ByteBuf;

public class Util {

	public static String read(Object object) {
		if (object instanceof ByteBuf) {
			ByteBuf buf = (ByteBuf) object;

			if (buf.readableBytes() == 0) {
				return null;
			}
			byte[] bs = new byte[buf.readableBytes()];
			for (int i = 0; i < buf.readableBytes(); i++) {
				bs[i] = buf.getByte(i);
			}
			return ByteKit.byteArrayToHex(bs);
		}
		return null;
	}

	public static String readByIndex(Object object) {
		if (object instanceof ByteBuf) {
			ByteBuf buf = (ByteBuf) object;

			byte[] bs = new byte[buf.readableBytes()];
			for (int i = 0; i < buf.readableBytes(); i++) {
				bs[i] = buf.getByte(i);
			}

			StringBuilder builder = new StringBuilder();

			int begin = buf.readerIndex();
			for (byte b : bs) {
				builder.append((begin++ + ":[" + String.format("%02x", b & 0xff)).toUpperCase()).append("] ");
			}
			return builder.toString();
		}
		return null;
	}
}
