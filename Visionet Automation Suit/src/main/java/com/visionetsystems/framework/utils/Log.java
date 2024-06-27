package com.visionetsystems.framework.utils;

import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

public class Log {
	private static Logger logger;

	public static void initializeLogger() {
		String logFilePath = Paths.get(TestReportUtility.Logger_FOLDER, TestReportUtility.testName() + ".log")
				.toString();
		System.setProperty("logFilename", logFilePath);
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		context.reconfigure();
		logger = LogManager.getLogger(Log.class);
	}

	public static void info(String message, Object... params) {
		logger.info(message, params);
	}

	public static void warn(String message, Object... params) {
		logger.warn(message, params);
	}

	public static void error(String message, Object... params) {
		logger.error(message, params);
	}

	public static void debug(String message, Object... params) {
		logger.debug(message, params);
	}
}
