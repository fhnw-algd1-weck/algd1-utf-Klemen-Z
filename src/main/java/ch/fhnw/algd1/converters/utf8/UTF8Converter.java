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
		byte firstByte;

		if (byteCount == 2) {
			firstByte = -64;
			firstByte = (byte) (firstByte >>> 5);
		} else if (byteCount == 3) {
			firstByte = -32;
			firstByte = (byte) (firstByte >>> 4);
		} else {
			firstByte = -16;
			firstByte = (byte) (firstByte >>> 3);
		}

		int tempX = x >>> (byteCount*8);
		b[0] = firstByte;

		for (int i = 0; i < byteCount-1; i++) {
			byte nextByte = -128;
			nextByte = (byte) (x >>> 6);

			b[i+1] = nextByte;
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
