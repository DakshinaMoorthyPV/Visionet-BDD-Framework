package com.visionetsystems.framework.stepdefinitions.hooks;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.testng.ITestResult;

import com.relevantcodes.extentreports.LogStatus;
import com.visionetsystems.framework.utils.BaseTestUtil;
import com.visionetsystems.framework.utils.EMailAutomationExecutionReport;
import com.visionetsystems.framework.utils.EndResult;
import com.visionetsystems.framework.utils.TestContext;
import com.visionetsystems.framework.utils.TestReportUtility;
import com.visionetsystems.framework.utils.UIConstantsUtil;
import com.visionetsystems.framework.utils.UtilityHelper;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;

public class Hook {
	private TestContext context = new TestContext();
	private static int numberOfScenarios = 0;
	public static int numberOfFeatures = 0;
	private static int scenarioOutlineCount;
	private static String currentFeatureFile = "";
	private static Map<String, Integer> featureScenarioCounts = new HashMap<>();
	private static Map<String, Integer> executedScenarioCounts = new HashMap<>();
	private static String previousFeature = "";
	public static List<String> SCENARIO_STATUSES = new ArrayList<String>();

	public Hook() {
		this.context = new TestContext(); // Initialize context here
	}

	@BeforeAll
	public static void beforeAllSetUp() throws Exception {
		new UtilityHelper().checkAndInstallFonts();
		TestReportUtility.projectDirectoryManager();
		BaseTestUtil.initializeExtentReports();
		BaseTestUtil.initializeEnvironment();
		BaseTestUtil.extentReportEV();
		UIConstantsUtil.TEST_START_DATE = UIConstantsUtil.DATE_FORMAT.format(Calendar.getInstance().getTime());
		UIConstantsUtil.TEST_START_DATE_TIME = LocalDateTime.now();
	}

	@Before
	public void beforeScenario(Scenario scenario) {
		Path featurePath = Paths.get(scenario.getUri());
		String rawFeatureName = featurePath.getFileName().toString();
		String featureName = rawFeatureName.substring(0, rawFeatureName.lastIndexOf('.'));
		// Call getScenarioCount here for the current feature file
		scenarioOutlineCount = getScenarioCount(featurePath.toString());
		System.out.println("Number of Scenario Outlines: " + scenarioOutlineCount);
		// Update the current feature file if it's different from the last executed
		if (!featureName.equals(currentFeatureFile)) {
			if (!currentFeatureFile.isEmpty()) {
				resetFeatureStatus();
			}
			currentFeatureFile = featureName;
			UIConstantsUtil.FEATURE_TEST = UIConstantsUtil.EXTENT_REPORTS.startTest("Feature: " + featureName);
			UIConstantsUtil.FEATURE_TEST.assignAuthor(System.getProperty("user.name").replaceAll("[^a-zA-Z0-9 ]", " "));
			UIConstantsUtil.FEATURE_TEST.setStartedTime(Calendar.getInstance().getTime());
			// Initialize scenario count for the new feature
			featureScenarioCounts.put(featureName, 0);
			executedScenarioCounts.put(featureName, 0);
			numberOfFeatures++;
		}
		featureScenarioCounts.compute(featureName, (k, v) -> v == null ? 1 : v + 1);
		String scenarioName = scenario.getName();
		UIConstantsUtil.SCENARIO_TEST = UIConstantsUtil.EXTENT_REPORTS.startTest("Scenario: " + scenarioName);
		UIConstantsUtil.SCENARIO_TEST.setStartedTime(Calendar.getInstance().getTime());
		Collection<String> tagsCollection = scenario.getSourceTagNames();
		Set<String> tags = new HashSet<>(tagsCollection);
		Set<String> cleanedTags = tags.stream().map(tag -> tag.replaceFirst("@", "")).collect(Collectors.toSet());
		String[] tagsArray = cleanedTags.toArray(new String[0]);
		UIConstantsUtil.SCENARIO_TEST.assignCategory(tagsArray);
		String featureName1 = scenario.getUri().toString();
		if (!featureName1.equals(previousFeature)) {
			numberOfFeatures++;
			previousFeature = featureName1;
		}
		String currfeatureName = featureName;
		String currscenarioName = scenario.getName();
		context.setCurrentFeature(currfeatureName);
		context.setCurrentScenario(currscenarioName);
		System.out.println(numberOfScenarios);
		numberOfScenarios++;
	}

	@After
	public static void afterScenario(Scenario scenario) {
		String status = null;
		System.out.println(scenario.getStatus());
		System.out.println(String.valueOf(ITestResult.SUCCESS));
		System.out.println(LogStatus.PASS.toString());
		if (scenario.isFailed()) {
			UIConstantsUtil.FAILED_TEST_CASE_COUNT++;
			status = "FAILED";
		} else if (scenario.getStatus().toString().equalsIgnoreCase(LogStatus.SKIP.name())
				|| scenario.getStatus().toString().equalsIgnoreCase("SKIPPED")) {
			UIConstantsUtil.SKIPPED_TEST_CASE_COUNT++;
			status = "SKIPPED";
		} else if (scenario.getStatus().toString().equalsIgnoreCase(LogStatus.PASS.name())
				|| scenario.getStatus().toString().equalsIgnoreCase("PASSED")) {
			UIConstantsUtil.PASSED_TEST_CASE_COUNT++;
			status = "PASSED";
		} else if (scenario.getStatus().toString().equalsIgnoreCase(LogStatus.UNKNOWN.name())
				|| scenario.getStatus().toString().equalsIgnoreCase("UNKNOWN")) {
			UIConstantsUtil.SKIPPED_TEST_CASE_COUNT++;
			status = "UNKNOWN";
		}
		// Add the status to the list
		SCENARIO_STATUSES.add(status);
		System.out.println("******************* " + scenario + " ********************");
		UIConstantsUtil.FEATURE_TEST.appendChild(UIConstantsUtil.SCENARIO_TEST);
		UIConstantsUtil.EXTENT_REPORTS.endTest(UIConstantsUtil.SCENARIO_TEST);
		if (SCENARIO_STATUSES.stream().anyMatch(item -> item.contains("FAILED"))) {
			UIConstantsUtil.FEATURE_TEST.getTest().setStatus(com.relevantcodes.extentreports.LogStatus.FAIL);
		} else if (SCENARIO_STATUSES.stream().anyMatch(item -> item.contains("SKIPPED"))) {
			UIConstantsUtil.FEATURE_TEST.getTest().setStatus(com.relevantcodes.extentreports.LogStatus.SKIP);
		} else if (SCENARIO_STATUSES.stream().anyMatch(item -> item.contains("UNKNOWN"))) {
			UIConstantsUtil.FEATURE_TEST.getTest().setStatus(com.relevantcodes.extentreports.LogStatus.UNKNOWN);
		} else if (SCENARIO_STATUSES.stream().anyMatch(item -> item.contains("PASSED"))) {
			UIConstantsUtil.FEATURE_TEST.getTest().setStatus(com.relevantcodes.extentreports.LogStatus.PASS);
		}
		UIConstantsUtil.FEATURE_TEST.getTest().setEndedTime(Calendar.getInstance().getTime());
		if (UIConstantsUtil.FEATURE_TEST != null) {
			System.out.println("******************* " + UIConstantsUtil.FEATURE_TEST.getTest().getName()
					+ " ********************");
			UIConstantsUtil.EXTENT_REPORTS.endTest(UIConstantsUtil.FEATURE_TEST);
		}
	}

	@AfterAll
	public static void teardownSetup() throws Exception {
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

	public static int getScenarioCount(String featureFilePath) {
		int count = 0;
		boolean inExamplesSection = false;
		boolean isExampleHeader = false;

		try {
			Path path = Paths.get(featureFilePath);
			List<String> fileContent = Files.readAllLines(path, StandardCharsets.UTF_8);
			for (String line : fileContent) {
				String trimmedLine = line.trim();
				if (trimmedLine.startsWith("Scenario:")) {
					count++; // Count each Scenario as one
				} else if (trimmedLine.startsWith("Scenario Outline:")) {
					isExampleHeader = true;
				} else if (trimmedLine.startsWith("Examples:")) {
					inExamplesSection = true;
				} else if (inExamplesSection && trimmedLine.startsWith("|") && !isExampleHeader) {
					count++; // Count each example as one scenario
				} else if (isExampleHeader && trimmedLine.startsWith("|")) {
					isExampleHeader = false; // Skip the header row of the examples table
				} else if (!trimmedLine.startsWith("|") && inExamplesSection) {
					inExamplesSection = false; // Exit the examples section
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return count;
	}

	public static void resetFeatureStatus() {
		SCENARIO_STATUSES.clear(); // Ensure this is called after a feature is fully processed
		SCENARIO_STATUSES = new ArrayList<>();
		numberOfScenarios = 0;
	}
}
