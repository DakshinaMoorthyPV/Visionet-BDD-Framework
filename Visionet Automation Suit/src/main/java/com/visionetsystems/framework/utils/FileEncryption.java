/**
 *
 */
package com.visionetsystems.framework.utils;

import java.util.Base64;

/**
 * @author Dakshina.Moorthy
 *
 */
public class FileEncryption {
	public static void main(String[] args) throws Exception {
		// Getting encoder
		String sample = "Z5BTunqYE7GAC1KPnPai";
		// print actual String
		System.out.println("Sample String:\n" + sample);

		// Encode into Base64 format
		String BasicBase64format = Base64.getEncoder().encodeToString(sample.getBytes());

		// print encoded String
		System.out.println("Encoded String:\n" + BasicBase64format);

	}

	public static String Encrypt(String plainText) {
		return Base64.getEncoder().encodeToString(plainText.getBytes());
	}
}
