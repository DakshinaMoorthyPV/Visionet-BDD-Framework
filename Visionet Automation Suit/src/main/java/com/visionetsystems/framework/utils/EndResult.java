package com.visionetsystems.framework.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.visionetsystems.framework.utils.otherpages.Block;
import com.visionetsystems.framework.utils.otherpages.Board;
import com.visionetsystems.framework.utils.otherpages.Table;

import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;

public class EndResult {
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss.SSS aa");
	private static String client = capitalizeFully(UtilityHelper.extractCompanyName());
	private static String company;
	private static List<String> t1Headers;
	private static List<List<String>> t1Rows;
	private static String t2Desc;
	private static List<String> t2Headers;
	private static List<List<String>> t2Rows = new ArrayList<>();
	private static List<Integer> t2ColWidths;
	private static String t3Desc;
	private static List<String> t3Headers;
	private static List<List<String>> t3Rows;
	private static String summary;
	private static String summaryVal;
	private final static String sign1 = System.getProperty("user.name")
			+ "\n---------------------\nVISIONET INTERNAL\n";
	private final static String sign2 = "---------------------\n" + client.toUpperCase() + "\n";
	private final static String advertise = "© " + java.time.Year.now().getValue()
			+ " Visionet Systems. All rights reserved";

	public void createExecutionPrintSummary() throws IOException, Exception {
		SimpleDateFormat dtf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss.SSS aa");
		Date d1 = dtf.parse(UIConstantsUtil.TEST_START_DATE);
		Date d2 = dtf.parse(UIConstantsUtil.TEST_END_DATE);
		long difference_In_Time = d2.getTime() - d1.getTime();
		long difference_In_Seconds = (difference_In_Time / 1000) % 60;
		long difference_In_Minutes = (difference_In_Time / (1000 * 60)) % 60;
		long difference_In_Hours = (difference_In_Time / (1000 * 60 * 60)) % 24;
		long difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24)) % 365;
		UIConstantsUtil.TOTAL_DURATION = difference_In_Days + " Days, " + difference_In_Hours + " Hrs, "
				+ difference_In_Minutes + " Min, " + difference_In_Seconds + " Sec";

		company = "VISIONET SYSTEMS\nwww.visionet.com\n\nAUTOMATION EXECUTION SUMMARY REPORT\n\n";
		t1Headers = Arrays.asList("REPORT INFO", "CLIENT");
		t1Rows = Arrays.asList(Arrays.asList("DATE: " + dateFormat.format(new Date()), client),
				Arrays.asList(
						"Test scope: "
								+ TestReportUtility.testName().replaceAll("Log", "").replaceAll("(Test).*", "$1"),
						"www." + client.toLowerCase() + ".com"));
		t2Desc = "EXECUTION SUMMARY";
		t2Headers = Arrays.asList("PARAM", "VALUE");

		if ("Web".equalsIgnoreCase(UIConstantsUtil.APP_CONFIG_MAP.get("TestType"))
				|| "Hybrid".equalsIgnoreCase(UIConstantsUtil.APP_CONFIG_MAP.get("TestType"))) {
			t2Rows.add(Arrays.asList("Browser Name",
					((RemoteWebDriver) UIConstantsUtil.WEB_DRIVER).getCapabilities().getBrowserName() + " - " + UIConstantsUtil.BROWSER_VERSION));
		}
		else if ("API".equalsIgnoreCase(UIConstantsUtil.APP_CONFIG_MAP.get("TestType"))) {
			t2Rows.add(Arrays.asList("Test Type","API Automation"));
		}
		
		else {
			String apkPath = "Android".equalsIgnoreCase(UIConstantsUtil.APP_CONFIG_MAP.get("Platform"))
					? UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_AndroidApk")
					: UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_iOSApp");
			try (ApkFile apkFile = new ApkFile(new File(apkPath))) {
				ApkMeta apkMeta = apkFile.getApkMeta();
				t2Rows.add(Arrays.asList("App Package", apkMeta.getPackageName()));
			}
		}

		t2Rows.addAll(Arrays.asList(Arrays.asList("Start Date", dateFormat.format(d1)),
				Arrays.asList("Start Time", timeFormat.format(d1)), Arrays.asList("End Date", dateFormat.format(d2)),
				Arrays.asList("End Time", timeFormat.format(d2)),
				Arrays.asList("Total Time Taken", UIConstantsUtil.TOTAL_DURATION)));

		Integer totalTests = UIConstantsUtil.PASSED_TEST_CASE_COUNT + UIConstantsUtil.FAILED_TEST_CASE_COUNT
				+ UIConstantsUtil.SKIPPED_TEST_CASE_COUNT;
		t2ColWidths = Arrays.asList(39, 40);
		t3Desc = "Run: Documentation";
		t3Headers = Arrays.asList("PARAM", "COUNT");
		t3Rows = Arrays.asList(Arrays.asList("Total Test Case", String.valueOf(totalTests)),
				Arrays.asList("Passed", String.valueOf(UIConstantsUtil.PASSED_TEST_CASE_COUNT)),
				Arrays.asList("Failed", String.valueOf(UIConstantsUtil.FAILED_TEST_CASE_COUNT)),
				Arrays.asList("Skipped", String.valueOf(UIConstantsUtil.SKIPPED_TEST_CASE_COUNT)));

		summary = "\nPass Percentage\nFail Percentage\nSkip Percentage\n";
		summaryVal = String.format("\n%1$.2f%%\n%2$.2f%%\n%3$.2f%%\n",
				100.0 * UIConstantsUtil.PASSED_TEST_CASE_COUNT / totalTests,
				100.0 * UIConstantsUtil.FAILED_TEST_CASE_COUNT / totalTests,
				100.0 * UIConstantsUtil.SKIPPED_TEST_CASE_COUNT / totalTests);

		printExecutionSummary();
	}

	public void printExecutionSummary() {
		Board b = new Board(84);
		Block initialBlock = new Block(b, 80, 4, company).allowGrid(true).setBlockAlign(Block.BLOCK_LEFT)
				.setDataAlign(Block.DATA_CENTER);
		b.setInitialBlock(initialBlock);
		b.appendTableTo(0, Board.APPEND_BELOW, new Table(b, 82, t1Headers, t1Rows));
		Block t2DescBlock = new Block(b, 80, 1, t2Desc).setDataAlign(Block.DATA_CENTER);
		b.getBlock(3).setBelowBlock(t2DescBlock);
		b.appendTableTo(5, Board.APPEND_BELOW, new Table(b, 79, t2Headers, t2Rows, t2ColWidths));
		Block t3DescBlock = new Block(b, 80, 1, t3Desc).setDataAlign(Block.DATA_CENTER);
		b.getBlock(8).setBelowBlock(t3DescBlock);
		b.appendTableTo(10, Board.APPEND_BELOW, new Table(b, 70, t3Headers, t3Rows, t2ColWidths));
		Block summaryBlock = new Block(b, 70, 4, summary).allowGrid(false).setDataAlign(Block.DATA_MIDDLE_RIGHT);
		b.getBlock(13).setBelowBlock(summaryBlock);
		Block summaryValBlock = new Block(b, 12, 4, summaryVal).allowGrid(false).setDataAlign(Block.DATA_MIDDLE_RIGHT);
		summaryBlock.setRightBlock(summaryValBlock);
		Block sign1Block = new Block(b, 45, 4, sign1).setDataAlign(Block.DATA_BOTTOM_MIDDLE).allowGrid(false);
		summaryBlock.setBelowBlock(sign1Block);
		sign1Block.setRightBlock(new Block(b, 30, 4, sign2).setDataAlign(Block.DATA_BOTTOM_MIDDLE).allowGrid(false));
		sign1Block.setBelowBlock(new Block(b, 84, 3, advertise).setDataAlign(Block.DATA_CENTER).allowGrid(false));
		System.out.println(b.invalidate().build().getPreview());
		System.out.println("");
	}

	public static String capitalizeFully(String str) {
		str = str.toLowerCase();
		String[] words = str.split("\\s");
		for (int i = 0; i < words.length; i++) {
			words[i] = StringUtils.capitalize(words[i]);
		}
		return String.join(" ", words);
	}
}
