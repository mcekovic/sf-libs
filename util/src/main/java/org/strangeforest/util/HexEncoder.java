package org.strangeforest.util;

/**
 * <p>Utility class for <i>Hexadecimal</i> encoding/decoding.</p>
 */
public abstract class HexEncoder {

	/**
	 * Encodes byte array <tt>data</tt> into char array.
	 * @param data byte array to be encoded.
	 * @return encoded char array.
	 */
	public static char[] encode(byte[] data) {
		return encode(data, 0, data.length, null);		
	}

	/**
	 * Encodes byte array <tt>data</tt> into char array.
	 * @param data byte array to be encoded.
	 * @param separator separator to separate bytes.
	 * @return encoded char array.
	 */
	public static char[] encode(byte[] data, char separator) {
		return encode(data, 0, data.length, separator);
	}

	/**
	 * Encodes byte array <tt>data</tt> into char array.
	 * @param data byte array to be encoded.
	 * @param offset offset of byte array start at with encoding.
	 * @param length length of byte array part to be encoded.
	 * @param separator separator character.
	 * @return encoded char array.
	 */
	public static char[] encode(byte[] data, int offset, int length, Character separator) {
		boolean hasSep = separator != null;
		char sep = hasSep ? separator : 0;
		int m = hasSep ? 3 : 2;
		char[] c = new char[length *m - (hasSep ? 1 : 0)];
		for (int i = length -1; i >= 0; i--) {
			byte d = data[offset + i];
			byte d1 = (byte)((d>>4)&15);
			byte d2 = (byte)(d&15);
			int i2 = i*m;
			c[i2] = d1 < 10 ? (char)('0'+d1) : (char)('A'-10+d1);
			c[i2+1] = d2 < 10 ? (char)('0'+d2) : (char)('A'-10+d2);
			if (hasSep && i2 > 0)
				c[i2-1] = sep;
		}
		return c;
	}

	/**
	 * Encodes byte array <tt>data</tt> into String.
	 * @param data byte array to be encoded.
	 * @return encoded String.
	 */
	public static String encodeToString(byte[] data) {
		return new String(encode(data));		
	}

	/**
	 * Encodes byte array <tt>data</tt> into String.
	 * @param data byte array to be encoded.
	 * @param separator separator character.
	 * @return encoded String.
	 */
	public static String encodeToString(byte[] data, char separator) {
		return new String(encode(data, 0, data.length, separator));
	}

	/**
	 * Encodes byte array <tt>data</tt> into String.
	 * @param data byte array to be encoded.
	 * @param offset offset of byte array start at with encoding.
	 * @param length length of byte array part to be encoded.
	 * @param separator separator character.
	 * @return encoded String.
	 */
	public static String encodeToString(byte[] data, int offset, int length, Character separator) {
		return new String(encode(data, offset, length, separator));
	}

	/**
	 * Decodes char array <tt>data</tt> into byte array.
	 * @param data char array to be decoded.
	 * @return byte array of original data.
	 */
	public static byte[] decode(char[] data) {
		int count = data.length/2;
		byte[] b = new byte[count];
		for (int i = count-1; i >= 0; i--) {
			int i2 = i*2;
			char c1 = data[i2];
			char c2 = data[i2+1];
			byte b1 = c1 < 'A' ? (byte)(c1-'0') : (byte)(c1-'A'+10);
			byte b2 = c2 < 'A' ? (byte)(c2-'0') : (byte)(c2-'A'+10);
			b[i] = (byte)((b1<<4)|b2);
		}
		return b;
	}

	/**
	 * Decodes String <tt>data</tt> into byte array.
	 * @param data String to be decoded.
	 * @return byte array of original data.
	 */
	public static byte[] decode(String data) {
		return decode(data.toCharArray());
	}
}