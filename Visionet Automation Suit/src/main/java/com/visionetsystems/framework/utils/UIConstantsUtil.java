package com.visionetsystems.framework.utils;

import java.io.FileInputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;

import io.appium.java_client.service.local.AppiumDriverLocalService;

/**
 * Utility class to hold UI constants and utilities for handling test data and
 * configurations.
 */
public class UIConstantsUtil {
	public static TreeMap<String, String> APP_CONFIG_MAP = new TreeMap<>();
	public static WebDriver WEB_DRIVER;

	public static ExtentReports EXTENT_REPORTS;
	public static ExtentTest FEATURE_TEST;
	public static ExtentTest SCENARIO_TEST;
	public static String TEST_TYPE = "";
	public static Properties PROPERTIES = new Properties();
	public static FileInputStream FILE_INPUT_STREAM;
	public static final long IMPLICIT_TIMEOUT = 10; // Implicit Timeout, set this to your requirement
	public static String BASE_URL = "";
	public static String BROWSER_VERSION = "";
	public static String TEST_START_DATE = "";
	public static String OPERATING_SYSTEM = "";
	public static LocalDateTime TEST_START_DATE_TIME;
	public static String TEST_END_DATE = "";
	public static LocalDateTime TEST_END_DATE_TIME = null;
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss.sss aa");
	public static final String PDFPATH = null;
	public static Integer TOTAL_TEST_CASE_COUNT = 0;
	public static Integer PASSED_TEST_CASE_COUNT = 0;
	public static Integer FAILED_TEST_CASE_COUNT = 0;
	public static Integer SKIPPED_TEST_CASE_COUNT = 0;
	public static String TOTAL_DURATION;
	public static String TEST_REPORT_TITLE = "Automation_Execution_Report";
	public static String MAIL_SUBJECT = StringUtils
			.capitalize("🤖🔍 UI Automation Execution Summary 📊 | " + formatDateWithSuffix(LocalDate.now()));
	public static int SCENARIO_OUTLINE_COUNT;
	public static String LATEST_RUN_ID = "";
	public static AppiumDriverLocalService appiumService;
	public static URL appiumServerUrl;
	public static String currentFeatureFile = "";
	public static String ReportFile;

	/**
	 * Formats a LocalDate with a suffix for the day, full month name and year.
	 *
	 * @param date the date to format
	 * @return formatted string
	 */
	public static String formatDateWithSuffix(LocalDate date) {
		String dayWithSuffix = getDayWithSuffix(date.getDayOfMonth());
		String month = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
		int year = date.getYear();
		return dayWithSuffix + " " + month + " " + year;
	}

	/**
	 * Adds a suffix to a day number to form '1st', '2nd', '3rd', etc.
	 *
	 * @param day the day of the month
	 * @return day with suffix
	 */
	private static String getDayWithSuffix(int day) {
		if (day >= 11 && day <= 13) {
			return day + "th";
		}
		switch (day % 10) {
		case 1:
			return day + "st";
		case 2:
			return day + "nd";
		case 3:
			return day + "rd";
		default:
			return day + "th";
		}
	}
}
