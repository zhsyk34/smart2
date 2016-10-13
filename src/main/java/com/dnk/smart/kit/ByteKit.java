package com.dnk.smart.kit;

public class ByteKit {

	private static final int MARK = 0xff;

	public static int byteArrayToInt(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			throw new RuntimeException("byte array is empty.");
		}
		if (bytes.length > 4) {
			throw new RuntimeException("byte array too long.");
		}
		int result = 0, offset = 0;
		for (int i = bytes.length - 1; i >= 0; i--) {
			result |= (bytes[i] & MARK) << (offset++ << 3);
		}
		return result;
	}

	private static byte[] intToByteArray(int i, int length) {
		if (length < 1 || length > 4) {
			throw new RuntimeException("length must > 0 && <= 4");
		}
		byte[] bytes = new byte[length];
		for (int k = 0; k < length; k++) {
			bytes[k] = (byte) (i >> ((length - 1 - k) << 3) & MARK);
		}
		return bytes;
	}

	public static byte[] intToByteArray(int i) {
		return intToByteArray(i, 4);
	}

	public static byte[] smallIntToByteArray(int i) {
		return intToByteArray(i, 2);
	}

	public static byte[] tinyToByteArray(int i) {
		return intToByteArray(i, 1);
	}

	public static String byteArrayToHex(byte[] bytes, String separator) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}

		StringBuilder builder = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			builder.append(String.format("%02x", b & MARK).toUpperCase());
			if (separator != null && !separator.isEmpty()) {
				builder.append(separator);
			}
		}
		return builder.toString();
	}

	public static String byteArrayToHex(byte[] bytes) {
		return byteArrayToHex(bytes, " ");
	}

	public static boolean compare(byte[] src, byte[] dest) {
		if (src == null || dest == null) {
			return src == dest;
		}
		//return new String(src).equals(new String(dest));
		if (src.length != dest.length) {
			return false;
		}

		for (int i = 0; i < src.length; i++) {
			if (src[i] != dest[i]) {
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) {
		byte[] bs = new byte[]{90, -91, 0, 13, 123, 97, 58, 49, 44, 98, 58, 50, 125, 2, -57, -91, 90, 90, -91, 0,};
		System.out.println(byteArrayToHex(bs));
		byte[] em = new byte[0];
		System.out.println(new String(em));
	}

}
