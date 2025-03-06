package ch.fhnw.algd1.converters.utf8;

/*
 * Created on 05.09.2014
 */

import java.util.Arrays;

/**
 * @author 
 */
public class UTF8Converter {
	public static byte[] codePointToUTF(int x) {
		byte[] b;

		if (x < 0x80) {
			b = new byte[1];
			b[0] = (byte) x;
			return b;
		}

		int byteCount = 1;
		for (int i = 0; i < 4; i++) {
			if ((x-127) >>> (i*8) != 0) {
				byteCount++;
			}
		}

		b = new byte[byteCount];

		if (byteCount == 2) {
			b[0] = (byte) (0xC0 | ((x >> 6) & 0x1F));
			b[1] = (byte) (0x80 | ((x >> 18) & 0x3F));
		} else if (byteCount == 3) {
			b[0] = (byte) (0xE0 | ((x >> 12) & 0x0F));
			b[1] = (byte) (0x80 | ((x >> 6) & 0x3F));
			b[2] = (byte) (0x80 | (x & 0x3F));
		} else {
			b[0] = (byte) (0xF0 | ((x >> 18) & 0x07));
			b[1] = (byte) (0x80 | ((x >> 12) & 0x3F));
			b[2] = (byte) (0x80 | ((x >> 6) & 0x3F));
			b[3] = (byte) (0x80 | (x & 0x3F));
		}

		// UTF-8 encoding of code point x. b[0] shall contain the first byte.
		return b;
	}

	public static int UTFtoCodePoint(byte[] bytes) {
		if (isValidUTF8(bytes)) {
			if (bytes.length < 2) {
				return bytes[0];
			}

			bytes[0] <<= bytes.length;
			bytes[0] >>>= bytes.length;

			int codePoint = bytes[0];

			for (int i = 1; i < bytes.length; i++) {
				bytes[i] <<= 1;
				bytes[i] >>>= 1;

				codePoint <<= 6;
				codePoint |= bytes[i];
			}

			// UTF-8 encoded in array bytes. bytes[0] contains the first byte
			return codePoint;
		} else return 0;
	}

	private static boolean isValidUTF8(byte[] bytes) {
		if (bytes.length == 1) return (bytes[0] & 0b1000_0000) == 0;
		else if (bytes.length == 2) return ((bytes[0] & 0b1110_0000) == 0b1100_0000)
				&& isFollowup(bytes[1]);
		else if (bytes.length == 3) return ((bytes[0] & 0b1111_0000) == 0b1110_0000)
				&& isFollowup(bytes[1]) && isFollowup(bytes[2]);
		else if (bytes.length == 4) return ((bytes[0] & 0b1111_1000) == 0b1111_0000)
				&& isFollowup(bytes[1]) && isFollowup(bytes[2]) && isFollowup(bytes[3]);
		else return false;
	}

	private static boolean isFollowup(byte b) {
		return (b & 0b1100_0000) == 0b1000_0000;
	}
}
