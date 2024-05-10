/**
 *
 */
package com.visionetsystems.framework.utils;

import java.util.Base64;

/**
 * @author Dakshina.Moorthy
 *
 */
public class FileDecryption {
	public static String Decrypt(String encryptedText) {

		// create an encoded String to decode
		String encoded = encryptedText;

		// decode into String from encoded format
		byte[] actualByte = Base64.getDecoder().decode(encoded);

		String actualString = new String(actualByte);

		// print actual String
		return actualString;
	}
}
