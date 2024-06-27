package com.visionetsystems.framework.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class VersionUtils {

	public static String getChromeVersion(String binaryPath) throws IOException {
		String[] commands = new String[] { binaryPath, "--version" };
		ProcessBuilder builder = new ProcessBuilder(commands);
		Process process = builder.start();
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String version = reader.readLine();
		if (version != null) {
			return version.split(" ")[2];
		}
		throw new IOException("Unable to retrieve Chrome version.");
	}

	public static String getChromedriverVersion(String driverPath) throws IOException {
		String[] commands = new String[] { driverPath, "--version" };
		ProcessBuilder builder = new ProcessBuilder(commands);
		Process process = builder.start();
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String version = reader.readLine();
		if (version != null) {
			return version.split(" ")[1]; // Assumes "ChromeDriver XX.X.XXXX.XX"
		}
		throw new IOException("Unable to retrieve Chromedriver version.");
	}
}
