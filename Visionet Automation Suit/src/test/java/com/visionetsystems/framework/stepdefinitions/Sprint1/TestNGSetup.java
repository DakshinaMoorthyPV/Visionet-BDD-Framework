package com.visionetsystems.framework.stepdefinitions.Sprint1;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.visionetsystems.framework.utils.BaseTestUtil;
import com.visionetsystems.framework.utils.EMailAutomationExecutionReport;
import com.visionetsystems.framework.utils.EndResult;
import com.visionetsystems.framework.utils.TestReportUtility;
import com.visionetsystems.framework.utils.UIConstantsUtil;
import com.visionetsystems.framework.utils.UtilityHelper;

public class TestNGSetup {
	@BeforeSuite(alwaysRun = true)
	public synchronized void beforeSuiteSetup() throws Exception {
		new UtilityHelper().checkAndInstallFonts();
		TestReportUtility.projectDirectoryManager();
		BaseTestUtil.initializeExtentReports();
		BaseTestUtil.initializeEnvironment();
		BaseTestUtil.extentReportEV();
		UIConstantsUtil.TEST_START_DATE = UIConstantsUtil.DATE_FORMAT.format(Calendar.getInstance().getTime());
		UIConstantsUtil.TEST_START_DATE_TIME = LocalDateTime.now();
	}

	@AfterSuite(alwaysRun = true)
	public void afterSuiteSetup() throws Exception {
		// Flush the report to write the scenario results
		UIConstantsUtil.EXTENT_REPORTS.flush();
		BaseTestUtil.modifyExtentReport();
		UIConstantsUtil.TEST_END_DATE = UIConstantsUtil.DATE_FORMAT.format(Calendar.getInstance().getTime());
		UIConstantsUtil.TEST_END_DATE_TIME = LocalDateTime.now();
		UIConstantsUtil.TOTAL_DURATION = TestReportUtility.calculateDuration(UIConstantsUtil.TEST_START_DATE,
				UIConstantsUtil.TEST_END_DATE);
		if ("web".equalsIgnoreCase(UIConstantsUtil.APP_CONFIG_MAP.get("TestType"))
				&& "Local".equalsIgnoreCase(UIConstantsUtil.APP_CONFIG_MAP.get("ExecutionLocation"))) {
			if (UIConstantsUtil.WEB_DRIVER != null) {
				UIConstantsUtil.WEB_DRIVER.quit();
			}
		} else if ("API".equalsIgnoreCase(UIConstantsUtil.APP_CONFIG_MAP.get("TestType"))) {

		} else if ("Emulator".equalsIgnoreCase(UIConstantsUtil.APP_CONFIG_MAP.get("ExecutionLocation"))
				|| "Simulator".equalsIgnoreCase(UIConstantsUtil.APP_CONFIG_MAP.get("ExecutionLocation"))) {
			if (UIConstantsUtil.appiumService.isRunning()) {
				BaseTestUtil.stopRunningAppiumServer();
			}
			if ("Emulator".equalsIgnoreCase(UIConstantsUtil.APP_CONFIG_MAP.get("ExecutionLocation"))) {
				BaseTestUtil.stopEmulator();
			} else if ("Simulator".equalsIgnoreCase(UIConstantsUtil.APP_CONFIG_MAP.get("ExecutionLocation"))) {
				BaseTestUtil.stopSimulator();
			}
		}
		new EndResult().createExecutionPrintSummary();
		String sendMailReport = UIConstantsUtil.APP_CONFIG_MAP.get("SendReportEmail");
		if (sendMailReport != null && sendMailReport.equalsIgnoreCase("Yes")) {
			new EMailAutomationExecutionReport().sendEmail();
		}
		String folderPath = TestReportUtility.EXTENT_REPORT_FOLDER;
		TestReportUtility.openFolder(folderPath);
		File txtfiles1 = new File("temp").getAbsoluteFile();
		FileUtils.cleanDirectory(txtfiles1);
		Thread.sleep(2000);
		PrintWriter writer1 = new PrintWriter(txtfiles1 + "\\.gitkeep", "UTF-8");
		writer1.println(".gitkeep");
		writer1.close();
	}
}