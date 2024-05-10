package com.visionetsystems.framework.utils;

import java.awt.Desktop;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class TestReportUtility {
	public static int LATEST_RUN_ID = 0;

	public static String testCaseName;
	static String projectRoot = System.getProperty("user.dir");
	public static String TEST_RUN_ID = "";
	public static String EXTENT_REPORT_FOLDER = "";
	public static String SCREENSHOTS_FOLDER = "";
	public static String Excel_FOLDER = "";
	public static String Logger_FOLDER = "";

	public static void projectDirectoryManager() {
		LATEST_RUN_ID = generateLatestRunFolderId();
		updateLog4j2Configuration();
		File theDir = new File("temp");
		if (!theDir.exists()) {
			theDir.mkdirs();
		}
	}

	private static int generateLatestRunFolderId() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String dateFolderName = now.format(dateFormatter);
		String folderPath = projectRoot + "\\Sprint-Execution-Reports" + File.separator + "screenshots" + File.separator
				+ UIConstantsUtil.APP_CONFIG_MAP.get("TestType") + File.separator + dateFolderName;
		File folder = new File(folderPath);
		File[] files = folder.listFiles(File::isDirectory);
		if (files == null || files.length == 0) {
			folder.mkdirs();
			files = folder.listFiles(File::isDirectory);
		} else {
			Arrays.sort(files, Comparator.comparing(File::getName));
		}

		String testRunId = generateTestRunId(files);

		// String testRunId = generateTestRunId(files);
		String testRunID = testRunId;
		testRunID = testRunID.replaceAll("([a-z])([A-Z])", "$1 $2").replaceAll("-", " ");
		TEST_RUN_ID = testRunID;
		int nextRunId = 001;
		File testRunFolder = new File(projectRoot + "/Sprint-Execution-Reports" + File.separator + "/extent-reports/"
				+ UIConstantsUtil.APP_CONFIG_MAP.get("TestType") + File.separator + dateFolderName + File.separator
				+ testRunId);

		while (testRunFolder.exists()) {
			nextRunId = extractRunIdNumber(testRunId) + 1;
			testRunId = "TestRunID-" + String.format("%03d", nextRunId);
			testRunFolder = new File(projectRoot + "/Sprint-Execution-Reports" + File.separator + "/extent-reports/"
					+ UIConstantsUtil.APP_CONFIG_MAP.get("TestType") + File.separator + dateFolderName + File.separator
					+ testRunId);
		}
		String lastRunFolder = dateFolderName + "/" + testRunId;
		UIConstantsUtil.LATEST_RUN_ID = String.valueOf(testRunId.replaceAll("TestRunID-", ""));

		String projectRoot = System.getProperty("user.dir");
		String baseFolder = "Sprint-Execution-Reports";

		// Paths for different directories
		SCREENSHOTS_FOLDER = Paths.get(projectRoot, baseFolder, "screenshots",
				UIConstantsUtil.APP_CONFIG_MAP.get("TestType"), lastRunFolder).toString();
		EXTENT_REPORT_FOLDER = Paths.get(projectRoot, baseFolder, "extent-reports",
				UIConstantsUtil.APP_CONFIG_MAP.get("TestType"), lastRunFolder).toString();
		Excel_FOLDER = Paths.get(projectRoot, baseFolder, "summaryreports",
				UIConstantsUtil.APP_CONFIG_MAP.get("TestType"), lastRunFolder).toString();
		Logger_FOLDER = Paths
				.get(projectRoot, baseFolder, "logs", UIConstantsUtil.APP_CONFIG_MAP.get("TestType"), lastRunFolder)
				.toString();

		// Create directories
		createDirectory(SCREENSHOTS_FOLDER);
		createDirectory(EXTENT_REPORT_FOLDER);
		createDirectory(Excel_FOLDER);
		createDirectory(Logger_FOLDER);
		return nextRunId;
	}

	private static void updateLog4j2Configuration() {
		String log4jConfigContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<Configuration xmlns=\"http://logging.apache.org/log4j/2.0/config\" status=\"WARN\" strict=\"true\">\n"
				+ "    <Appenders>\n" + "        <Console name=\"Console\" target=\"SYSTEM_OUT\">\n"
				+ "            <PatternLayout pattern=\"%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n\" />\n"
				+ "        </Console>\n" + "        <RollingFile name=\"File\" fileName=\"" + Logger_FOLDER + "/"
				+ testName() + ".log\">\n"
				+ "            <PatternLayout pattern=\"%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n\" />\n"
				+ "            <Policies>\n" + "                <SizeBasedTriggeringPolicy size=\"10 MB\"/>\n"
				+ "            </Policies>\n" + "            <DefaultRolloverStrategy max=\"1\"/>\n"
				+ "        </RollingFile>\n" // Added the missing closing tag for RollingFile
				+ "    </Appenders>\n" + "    <Loggers>\n" + "        <Root level=\"DEBUG\">\n"
				+ "            <AppenderRef ref=\"Console\" />\n" + "            <AppenderRef ref=\"File\" />\n"
				+ "        </Root>\n" + "    </Loggers>\n" + "</Configuration>";

		String log4jConfigFilePath = "src/main/resources/log4j.xml";

		Path configFilePath = Paths.get(log4jConfigFilePath);
		if (Files.exists(configFilePath)) {
			try {
				Files.delete(configFilePath);
				System.out.println("Existing Log4j 2 XML configuration file deleted.");
			} catch (IOException e) {
				System.out.println("Failed to delete existing Log4j 2 XML configuration file: " + e.getMessage());
				return;
			}
		}

		try {
			Files.write(configFilePath, log4jConfigContent.getBytes(StandardCharsets.UTF_8));
			System.out.println("Log4j 2 XML configuration file created successfully.");
		} catch (IOException e) {
			System.out.println("Failed to create Log4j 2 XML configuration file: " + e.getMessage());
		}

	}

	public static String testName() {
		String featurePath = UIConstantsUtil.APP_CONFIG_MAP.get("FeatureFilePath");
		String dateStamp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

		// Normalize the path to use forward slashes
		featurePath = featurePath.replace("\\", "/");

		// Debug: print the normalized featurePath
		System.out.println("Normalized Feature Path: " + featurePath);

		// Check for multiple entries
		if (featurePath.contains(",")) {
			// Handling multiple paths (either files or directories)
			return formatMultiplePaths(featurePath, dateStamp);
		} else {
			// Handling a single path (could be file or directory)
			return formatSinglePath(featurePath, dateStamp);
		}
	}

	private static String formatMultiplePaths(String featurePath, String dateStamp) {
		String[] paths = featurePath.split(",");
		StringBuilder label = new StringBuilder("Multiple-");
		for (String path : paths) {
			String lastSegment = getLastSegment(path);
			label.append(lastSegment).append("-");
		}
		label.append("Automation-Test-Log-").append(dateStamp);
		return label.toString();
	}

	private static String formatSinglePath(String path, String dateStamp) {
		String lastSegment = getLastSegment(path);
		return lastSegment + "-Automation-Test-Log-" + dateStamp;
	}

	private static String getLastSegment(String path) {
		path = path.trim();
		String[] segments = path.split("/");
		String lastSegment = segments[segments.length - 1];
		// Remove the ".feature" extension if present
		if (lastSegment.contains(".feature")) {
			lastSegment = lastSegment.substring(0, lastSegment.indexOf(".feature"));
		}
		return lastSegment;
	}

	private static int extractRunIdNumber(String folderName) {
		String[] parts = folderName.split("-");
		if (parts.length > 1) {
			try {
				return Integer.parseInt(parts[1]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	private static String generateTestRunId(File[] files) {
		int highestRunId = 0;
		if (files != null && files.length > 0) {
			for (File file : files) {
				String folderName = file.getName();
				int runId = extractRunIdNumber(folderName);
				if (runId > highestRunId) {
					highestRunId = runId;
				}
			}
		}

		int nextRunId = highestRunId + 1;
		return "TestRunID-" + String.format("%03d", nextRunId);
	}

	private static String createDirectory(String path) {
		File directory = new File(path);
		if (!directory.exists()) {
			boolean isCreated = directory.mkdirs();
			if (isCreated) {
				System.out.println("Directory created: " + path);
				// After creating the directory, set the permissions
				setPermissions(path);
			} else {
				System.out.println("Failed to create directory: " + path);
			}
		} else {
			System.out.println("Directory already exists: " + path);
		}
		return path;
	}

	private static void setPermissions(String path) {
		try {
			String command = "icacls \"" + path + "\" /grant:r \"%username%\":(OI)(CI)F /T";
			Process p = Runtime.getRuntime().exec(command);
			p.waitFor(); // Wait for the command to complete

			System.out.println("Permissions set: " + path);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to set permissions: " + path);
		}
	}

	public static void logTestStep(ExtentTest test, String expectedResult, String actualResult, String status,
			String outputFile) throws Exception {
		String statuscolour = "";

		if ("Pass".equalsIgnoreCase(status)) {
			statuscolour = "rgb(50, 205, 50)";

		} else if ("Fail".equalsIgnoreCase(status)) {
			statuscolour = "rgb(252, 16, 13)";
		} else {
			statuscolour = "rgb(0, 109, 175)";

		}
		String logDetails = "<div style=\"width: 100%; max-height: 200px; overflow-y: scroll; overflow-x: auto; border-collapse: collapse; border-radius: 5px !important; margin-top: 0px !important; padding-top: 0px !important; box-shadow: 0 4px 4px 4px rgba(0,0,0,0.30); border-bottom: solid 1px rgb(53, 59, 95); border-bottom-width: 8px !important;\">\r\n"
				+ "    <table style=\"table-layout: fixed; border-collapse: collapse; border-radius: 13px; border-spacing: 0; width: 100%; max-width: 100%;\">\r\n"
				+ "      <tbody><tr style=\"background: linear-gradient(to bottom, #f7f7f7 0%, #fff 100%) !important;\">\r\n"
				+ "        <td style=\"padding-top: 25px; color: rgb(19, 22, 42)!important; max-height: 100% !important; overflow-y: auto; word-wrap: break-word; max-width: 100% !important; font-weight: 500; border: 1.5px solid rgb(53, 59, 95) !important; font-family: Poppins SemiBold !important;\">Expected Result</td>\r\n"
				+ "        <td style=\"padding-top: 27px; color: rgb(53, 59, 95) !important; max-height: 100% !important; word-wrap: break-word; width: 100% !important; white-space: normal; overflow: auto; height: 100%; overflow-y: auto; overflow-x: hidden; display: block; max-width: 100% !important; position: relative; font-family: Poppins Regular !important; font-size: 14px; line-height: 1.5; border-left: 0px !important; border-right: 0px !important; border-top: 0px !important; border-bottom: 1.5px solid rgb(53, 59, 95) !important; text-align: justify !important;\">"
				+ expectedResult + "</td>\r\n" + "      </tr>\r\n"
				+ "      <tr style=\"background: linear-gradient(to bottom, #f7f7f7 0%, #fff 100%) !important;\">\r\n"
				+ "        <td style=\"padding-top: 25px; color: rgb(19, 22, 42)!important; max-height: 100% !important; overflow-y: auto; word-wrap: break-word; max-width: 100% !important; font-weight: 700; border: 1.5px solid rgb(53, 59, 95) !important; font-family: Poppins SemiBold !important;\">Actual Result</td>\r\n"
				+ "        <td style=\"padding-top: 26px; color: rgb(53, 59, 95) !important; max-height: 100% !important; word-wrap: break-word; width: 100% !important; white-space: normal; overflow: auto; height: 100%; overflow-y: auto; overflow-x: hidden; display: block; max-width: 100% !important; position: relative; font-family: Poppins Regular !important; font-size: 14px; line-height: 1.5; border-left: 0px !important; border-right: 0px !important; border-top: 0px !important; border-bottom: 1.5px solid rgb(53, 59, 95) !important; text-align: justify !important;\">"
				+ actualResult + "</td>\r\n" + "      </tr>\r\n"
				+ " <tr style=\"background: linear-gradient(to bottom, #f7f7f7 0%, #fff 100%) !important;\">\r\n"
				+ "        <td style=\"padding-top: 25px; color: rgb(19, 22, 42)!important; max-height: 100% !important; overflow-y: auto; word-wrap: break-word; max-width: 100% !important; font-weight: 700; border: 1.5px solid rgb(53, 59, 95) !important; font-family: Poppins SemiBold !important;\">Status</td>\r\n"
				+ "        <td style=\"padding-top: 26px; color: " + statuscolour
				+ " !important; max-height: 100% !important; word-wrap: break-word; width: 100% !important; white-space: normal; overflow: auto; height: 100%; overflow-y: auto; overflow-x: hidden; display: block; max-width: 100% !important; position: relative; font-family: Poppins Regular !important; font-size: 14px; line-height: 1.5; border-left: 0px !important; border-right: 0px !important; border-top: 0px !important; border-bottom: 1.5px solid rgb(53, 59, 95) !important; text-align: justify !important; font-weight: 700;\">"
				+ status + "</td>\r\n" + "      </tr>\r\n" + "    </tbody></table>\r\n" + "  </div>";
		String base64ImageString = encodeFileToBase64Binary(outputFile);

		if ("Pass".equalsIgnoreCase(status)) {
			test.log(LogStatus.PASS, logDetails,
					"<div style='color:rgb(53, 59, 95) !important;font-family: Poppins SemiBold !important; font-style:italic;'>Snapshot below: <br></div>"
							+ test.addBase64ScreenShot("data:image/png;base64," + base64ImageString));

		} else if ("Fail".equalsIgnoreCase(status)) {
			test.log(LogStatus.FAIL, logDetails,
					"<div style='color:rgb(53, 59, 95) !important;font-family: Poppins SemiBold !important; font-style:italic;'>Snapshot below: <br></div>"
							+ test.addBase64ScreenShot("data:image/png;base64," + base64ImageString));

		} else {
			test.log(LogStatus.INFO, logDetails,
					"<div style='color:rgb(53, 59, 95) !important;font-family: Poppins SemiBold !important; font-style:italic;'>Snapshot below: <br></div>"
							+ test.addBase64ScreenShot("data:image/png;base64," + base64ImageString));

		}
	}

	private static String encodeFileToBase64Binary(String fileName) throws IOException {
		File file = new File(fileName);
		try (FileInputStream imageInFile = new FileInputStream(file)) {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int nRead;
			byte[] data = new byte[16384]; // Use a buffer of 16KB.

			while ((nRead = imageInFile.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}

			buffer.flush();
			byte[] imageData = buffer.toByteArray();
			return Base64.getEncoder().encodeToString(imageData);
		}
	}

	public static String decodeBase64(String base64String) {
		// Check if the string could be Base64 encoded
		if (isBase64(base64String)) {
			try {
				byte[] decodedBytes = Base64.getDecoder().decode(base64String);
				return new String(decodedBytes, StandardCharsets.UTF_8);
			} catch (IllegalArgumentException e) {
				// If decoding fails, return the original string
				return base64String;
			}
		} else {
			// Return the original string if it's not Base64
			return base64String;
		}
	}

	private static boolean isBase64(String str) {
		// Check if the string length is a multiple of 4 and matches the Base64 pattern
		return (str.length() % 4 == 0) && str.matches("^[A-Za-z0-9+/]*={0,2}$");
	}

	public static String calculateDuration(String startDateStr, String endDateStr) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss.SSS a");
		LocalDateTime startDate = LocalDateTime.parse(startDateStr, dtf);
		LocalDateTime endDate = LocalDateTime.parse(endDateStr, dtf);
		Duration duration = Duration.between(startDate, endDate);
		long differenceInMilliseconds = duration.toMillis();
		long differenceInSeconds = duration.getSeconds();
		long differenceInMinutes = duration.toMinutes();
		long differenceInHours = duration.toHours();
		long differenceInDays = duration.toDays();
		String durationOfExecution = differenceInDays + " Days, " + differenceInHours + " Hrs, " + differenceInMinutes
				+ " Min, " + differenceInSeconds + " Sec and " + differenceInMilliseconds + " MS";
		return durationOfExecution;
	}

	public void deleteFolder(File folder) throws IOException {
		FileUtils.deleteDirectory(folder);
		folder.delete();
	}

	public static void openFolder(String folderPath) {
		try {
			String isRemoteExecutionProp = System.getProperty("remote.execution");
			boolean isRemoteExecution = "true".equalsIgnoreCase(isRemoteExecutionProp);

			if (isRemoteExecution) {
				System.out.println("Executing remotely");
				// Execute Windows-specific command for remote execution
				String shareCommand = "net use \\\\remote-host\\share-folder /user:username password";
				// Replace "remote-host", "share-folder", "username", and "password" with actual
				// values
				executePowerShellCommand(shareCommand);
				String openFolderCommand = "explorer \\\\remote-host\\share-folder";
				// Replace "remote-host" and "share-folder" with actual values
				executePowerShellCommand(openFolderCommand);
			} else {
				System.out.println("Executing locally");
				// Local folder opening
				File folder = new File(folderPath);
				if (Desktop.isDesktopSupported()) {
					Desktop desktop = Desktop.getDesktop();
					try {
						desktop.open(folder);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("Desktop not supported");
				}
			}
		} catch (IOException | InterruptedException e) {
			System.out.println("Error occurred while executing the command: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void executePowerShellCommand(String command) throws IOException, InterruptedException {
		ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe", "-Command", command);
		Process process = processBuilder.start();
		int exitCode = process.waitFor();

		if (exitCode == 0) {
			System.out.println("Successfully executed PowerShell command: " + command);
		} else {
			System.out.println("Failed to execute PowerShell command: " + command);
		}
	}

}