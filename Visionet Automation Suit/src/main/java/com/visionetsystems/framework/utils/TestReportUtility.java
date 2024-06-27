package com.visionetsystems.framework.utils;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.parser.Parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
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

	public static void updateLog4j2Configuration() {
		String log4jConfigContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
				+ "<Configuration xmlns=\"http://logging.apache.org/log4j/2.0/config\" status=\"WARN\" strict=\"true\">\r\n"
				+ "    <Appenders>\r\n" + "        <Console name=\"Console\" target=\"SYSTEM_OUT\">\r\n"
				+ "            <PatternLayout pattern=\"%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n\" />\r\n"
				+ "        </Console>\r\n" + "        <RollingFile name=\"File\" fileName=\"${sys:logFilename}\"\r\n"
				+ "                     filePattern=\"${sys:logFilename}-%d{yyyy-MM-dd}-%i.log.gz\">\r\n"
				+ "            <PatternLayout pattern=\"%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n\" />\r\n"
				+ "            <Policies>\r\n" + "                <SizeBasedTriggeringPolicy size=\"10 MB\"/>\r\n"
				+ "            </Policies>\r\n" + "            <DefaultRolloverStrategy max=\"5\"/>\r\n"
				+ "        </RollingFile>\r\n" + "    </Appenders>\r\n" + "    <Loggers>\r\n"
				+ "        <Root level=\"DEBUG\">\r\n" + "            <AppenderRef ref=\"Console\" />\r\n"
				+ "            <AppenderRef ref=\"File\" />\r\n" + "        </Root>\r\n" + "    </Loggers>\r\n"
				+ "</Configuration>\r\n" + "";

		// new

		String log4jConfigFilePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main"
				+ File.separator + "resources" + File.separator + "log4j2.xml";

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
			System.out.println("Log4j 2 XML configuration file updated successfully.");
		} catch (IOException e) {
			System.out.println("Failed to update Log4j 2 XML configuration file: " + e.getMessage());
			return;
		}

		// Set system property to ensure Log4j2 uses the updated configuration
		System.setProperty("log4j.configurationFile", log4jConfigFilePath);
		System.out.println("Log4j configuration file set to: " + log4jConfigFilePath);

		// Ensure log directory exists
		Path logDirPath = Paths.get(Logger_FOLDER);
		if (!Files.exists(logDirPath)) {
			try {
				Files.createDirectories(logDirPath);
				System.out.println("Log directory created: " + logDirPath);
			} catch (IOException e) {
				System.out.println("Failed to create log directory: " + e.getMessage());
				return;
			}
		}

		// Test logging
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

	public static String createDirectory(String path) {
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
			String username = System.getProperty("user.name");
			String command;
			if (isWindows()) {
				command = "icacls \"" + path + "\" /grant:r \"" + username + "\":(OI)(CI)F /T";
			} else {
				command = "chmod -R 755 \"" + path + "\"";
			}
			executeCommand(command);
			System.out.println("Permissions set: " + path);
		} catch (IOException | InterruptedException e) {
			System.err.println("Failed to set permissions: " + path);
			e.printStackTrace();
		}
	}

	private static void executeCommand(String command) throws IOException, InterruptedException {
		Process process = Runtime.getRuntime().exec(command);
		process.waitFor(); // Wait for the command to complete

		// Output handling
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		}

		// Error stream handling
		try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
			String line;
			while ((line = errorReader.readLine()) != null) {
				System.err.println(line);
			}
		}
	}

	private static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("windows");
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
		String logDetails = "<div style=\"width: 100%; max-height: 213px; overflow-y: scroll; overflow-x: auto; border-collapse: collapse; border-radius: 5px !important; margin-top: 0px !important; padding-top: 0px !important; box-shadow: 0 4px 4px 4px rgba(0,0,0,0.30); border-bottom: solid 5px rgb(53, 59, 95);max-width: 100%;\">\r\n"
				+ "    <table style=\"table-layout: fixed; border-collapse: collapse; border-radius: 13px; border-spacing: 0; width: 100%; max-width: 100%;\">\r\n"
				+ "      <tbody><tr style=\"background: linear-gradient(to bottom, #f7f7f7 0%, #fff 100%) !important;\">\r\n"
				+ "        <td style=\"padding-top: 25px; color: rgb(19, 22, 42)!important; max-height: 100% !important; overflow-y: auto; word-wrap: break-word; max-width: 100% !important; font-weight: 500; border: 1.5px solid rgb(53, 59, 95) !important; font-family: Poppins SemiBold !important;\">Expected Result</td>\r\n"
				+ "        <td style=\"padding-top: 25px; color: rgb(53, 59, 95) !important; max-height: 100% !important; word-wrap: break-word; width: 100% !important; white-space: normal; overflow: auto; height: 100%; overflow-y: auto; overflow-x: hidden; display: block; max-width: 100% !important; position: relative; font-family: Poppins Regular !important; font-size: 14px; line-height: 1.5; border-left: 0px !important; border-right: 0px !important; border-top: 0px !important; border-bottom: 1.5px solid rgb(53, 59, 95) !important; text-align: justify !important;\">"
				+ expectedResult + "</td>\r\n" + "      </tr>\r\n"
				+ "      <tr style=\"background: linear-gradient(to bottom, #f7f7f7 0%, #fff 100%) !important;\">\r\n"
				+ "        <td style=\"padding-top: 25px; color: rgb(19, 22, 42)!important; max-height: 100% !important; overflow-y: auto; word-wrap: break-word; max-width: 100% !important; font-weight: 700; border: 1.5px solid rgb(53, 59, 95) !important; font-family: Poppins SemiBold !important;\">Actual Result</td>\r\n"
				+ "        <td style=\"padding-top: 56px; color: rgb(53, 59, 95) !important; max-height: 100% !important; word-wrap: break-word; width: 100% !important; white-space: normal; overflow: auto; height: 100%; overflow-y: auto; overflow-x: hidden; display: block; max-width: 100% !important; position: relative; font-family: Poppins Regular !important; font-size: 14px; line-height: 1.5; border-left: 0px !important; border-right: 0px !important; border-top: 0px !important; border-bottom: 1.5px solid rgb(53, 59, 95) !important; text-align: justify !important;\">"
				+ actualResult + "</td>\r\n" + "      </tr>\r\n"
				+ " <tr style=\"background: linear-gradient(to bottom, #f7f7f7 0%, #fff 100%) !important;\">\r\n"
				+ "        <td style=\"padding-top: 25px; color: rgb(19, 22, 42)!important; max-height: 100% !important; overflow-y: auto; word-wrap: break-word; max-width: 100% !important; font-weight: 700; border-bottom: 0px !important; font-family: Poppins SemiBold !important; border-bottom-width: 0px !important;\">Status</td>\r\n"
				+ "        <td style=\"padding-top: 25px; color: " + statuscolour
				+ " !important; max-height: 100% !important; word-wrap: break-word; width: 100% !important; white-space: normal; overflow: auto; height: 100%; overflow-y: auto; overflow-x: hidden; display: block; max-width: 100% !important; position: relative; font-family: Poppins Regular !important; font-size: 14px; line-height: 1.5; border-left: 0px !important; border-right: 0px !important; border-top: 0px !important; border-bottom: 0px !important; text-align: justify !important; font-weight: 700;vertical-align: middle;\">"
				+ status + "</td>\r\n" + "      </tr>\r\n" + "    </tbody></table>\r\n" + "  </div>";
		String base64ImageString = encodeFileToBase64Binary(outputFile);

		if ("Pass".equalsIgnoreCase(status)) {
			test.log(LogStatus.PASS, logDetails,
					"<div style='color:rgb(53, 59, 95) !important;font-family: Poppins SemiBold !important; font-style:italic;width: 500px;'>Snapshot below: <br></div>"
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
	
	public static void apilogTestStep(ExtentTest test, String expectedResult, String actualResult, String status,
			String outputFile) throws Exception {
		String statuscolour = "";

		if ("Pass".equalsIgnoreCase(status)) {
			statuscolour = "rgb(50, 205, 50)";

		} else if ("Fail".equalsIgnoreCase(status)) {
			statuscolour = "rgb(252, 16, 13)";
		} else {
			statuscolour = "rgb(0, 109, 175)";

		}
		String logDetails = "<div style=\"width: 100%; max-height: 200px; overflow-y: scroll; overflow-x: auto; border-collapse: collapse; border-radius: 5px !important; margin-top: 0px !important; padding-top: 0px !important; box-shadow: 0 4px 4px 4px rgba(0,0,0,0.30); border-bottom: solid 5px rgb(53, 59, 95);\">\r\n"
				+ "    <table style=\"table-layout: fixed; border-collapse: collapse; border-radius: 13px; border-spacing: 0; width: 100%; max-width: 100%;\">\r\n"
				+ "      <tbody><tr style=\"background: linear-gradient(to bottom, #f7f7f7 0%, #fff 100%) !important;\">\r\n"
				+ "        <td style=\"padding-top: 25px; color: rgb(19, 22, 42)!important; max-height: 100% !important; overflow-y: auto; word-wrap: break-word; max-width: 100% !important; font-weight: 500; border: 1.5px solid rgb(53, 59, 95) !important; font-family: Poppins SemiBold !important;\">Expected Result</td>\r\n"
				+ "        <td style=\"padding-top: 25px; color: rgb(53, 59, 95) !important; max-height: 100% !important; word-wrap: break-word; width: 100% !important; white-space: normal; overflow: auto; height: 100%; overflow-y: auto; overflow-x: hidden; display: block; max-width: 100% !important; position: relative; font-family: Poppins Regular !important; font-size: 14px; line-height: 1.5; border-left: 0px !important; border-right: 0px !important; border-top: 0px !important; border-bottom: 1.5px solid rgb(53, 59, 95) !important; text-align: justify !important;\">"
				+ expectedResult + "</td>\r\n" + "      </tr>\r\n"
				+ "      <tr style=\"background: linear-gradient(to bottom, #f7f7f7 0%, #fff 100%) !important;\">\r\n"
				+ "        <td style=\"padding-top: 25px; color: rgb(19, 22, 42)!important; max-height: 100% !important; overflow-y: auto; word-wrap: break-word; max-width: 100% !important; font-weight: 700; border: 1.5px solid rgb(53, 59, 95) !important; font-family: Poppins SemiBold !important;\">Actual Result</td>\r\n"
				+ "        <td style=\"padding-top: 25px; color: rgb(53, 59, 95) !important; max-height: 100% !important; word-wrap: break-word; width: 100% !important; white-space: normal; overflow: auto; height: 100%; overflow-y: auto; overflow-x: hidden; display: block; max-width: 100% !important; position: relative; font-family: Poppins Regular !important; font-size: 14px; line-height: 1.5; border-left: 0px !important; border-right: 0px !important; border-top: 0px !important; border-bottom: 1.5px solid rgb(53, 59, 95) !important; text-align: justify !important;\">"
				+ actualResult + "</td>\r\n" + "      </tr>\r\n"
				+ " <tr style=\"background: linear-gradient(to bottom, #f7f7f7 0%, #fff 100%) !important;\">\r\n"
				+ "        <td style=\"padding-top: 25px; color: rgb(19, 22, 42)!important; max-height: 100% !important; overflow-y: auto; word-wrap: break-word; max-width: 100% !important; font-weight: 700; border-bottom: 0px !important; font-family: Poppins SemiBold !important; border-bottom-width: 0px !important;\">Status</td>\r\n"
				+ "        <td style=\"padding-top: 25px; color: " + statuscolour
				+ " !important; max-height: 100% !important; word-wrap: break-word; width: 100% !important; white-space: normal; overflow: auto; height: 100%; overflow-y: auto; overflow-x: hidden; display: block; max-width: 100% !important; position: relative; font-family: Poppins Regular !important; font-size: 14px; line-height: 1.5; border-left: 0px !important; border-right: 0px !important; border-top: 0px !important; border-bottom: 0px !important; text-align: justify !important; font-weight: 700;vertical-align: middle;\">"
				+ status + "</td>\r\n" + "      </tr>\r\n" + "    </tbody></table>\r\n" + "  </div>";
		String apioutput = outputFile;

		if ("Pass".equalsIgnoreCase(status)) {
			test.log(LogStatus.PASS, logDetails,
					apioutput);

		} else if ("Fail".equalsIgnoreCase(status)) {
			test.log(LogStatus.FAIL, logDetails,
					apioutput);

		} else {
			test.log(LogStatus.INFO, logDetails,
					apioutput);

		}
	}
	public static String prettyPrintJson(String jsonString) {
        try {
        	if(jsonString != null) {
            ObjectMapper mapper = new ObjectMapper();
            Object json = mapper.readValue(jsonString, Object.class);
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            return writer.writeValueAsString(json);
        }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
		return jsonString;
    }
	 public static String prettyPrintHtml(String htmlString) {
	        try {
	            // Parse the HTML string
	            Document document = Jsoup.parse(htmlString, "", Parser.htmlParser());
	            
	            // Set pretty print settings
	            OutputSettings settings = new OutputSettings();
	            settings.prettyPrint(true);
	            settings.indentAmount(4); // Set the number of spaces for indentation
	            
	            // Apply settings and convert the document back to a string
	            document.outputSettings(settings);
	            return document.outerHtml();
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	 public static String formatXML(String xmlString) {
	        try {
	            // Parse the given XML string to a DOM document
	            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
	            org.w3c.dom.Document document = documentBuilder.parse(new InputSource(new StringReader(xmlString)));

	            // Set up a transformer to convert the DOM document to a string with indentation
	            TransformerFactory transformerFactory = TransformerFactory.newInstance();
	            transformerFactory.setAttribute("indent-number", 4); // Specify indentation
	            Transformer transformer = transformerFactory.newTransformer();
	            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

	            // Transform the document to a string
	            StringWriter stringWriter = new StringWriter();
	            transformer.transform(new DOMSource(document), new StreamResult(stringWriter));

	            return stringWriter.toString();
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	    public static String logApiResponse(String apiUrl, String methodName, String body, int statusCode, String repStatusLine, int expectedResponseHeaderCount, long responseTime, String responseData, Map<String, String> receivedHeaders) {
		    StringBuilder log = new StringBuilder();
		    log.append("API URL: ").append(apiUrl).append("\n");
		    log.append("Method Name: ").append(methodName).append("\n");
		    log.append("Request Body: ").append(body).append("\n");
		    log.append("Status Code: ").append(statusCode).append("\n");
		    log.append("Response Status Line: ").append(repStatusLine).append("\n");
		    log.append("Expected Response Header Count: ").append(expectedResponseHeaderCount).append("\n");
		    log.append("Response Time: ").append(responseTime).append(" ms\n");
		    log.append("Response Data: ").append(responseData).append("\n");
		    String logSummary = "";
		    try {
	            // Simulate dynamic headers based on expectedResponseHeaderCount
		    	Map<String, String> headers = new HashMap<>();

		        for (int i = 0; i < expectedResponseHeaderCount; i++) {
		            headers.put("Custom-Header-" + i, "Value-" + i);
		        }

	            String prettyResponseBody = responseData;
	            int receivedHeaderCount = receivedHeaders.size();

	            	logSummary += "<div style=\"\r\n"
	            			+ "                height: auto;\r\n"
	            			+ "                padding: 10px !important;\r\n"
	            			+ "                overflow-x: auto !important;\r\n"
	            			+ "                float: left;\r\n"
	            			+ "                overflow: scroll;\r\n"
	            			+ "                width: auto !important;\r\n"
	            			+ "                font-weight: normal !important;\r\n"
	            			+ "                /* background-color: #0973B9CC !important; */\r\n"
	            			+ "                font-family: monospace;\r\n"
	            			+ "                margin-right: -237px !important;\r\n"
	            			+ "                padding-right: 90px !important;\r\n"
	            			+ "                white-space: nowrap;\r\n"
	            			+ "                position: relative;\r\n"
	            			+"max-width: 706px;\r\n"
	            			+ "                \">\r\n"
	            			+ "            <div class=\"postman_nav\" style=\"height: 30px;width: 248.5% !important;background-image: url(https://www.visionet.com/sites/default/files/2024-04/about-banner.png?fid=52); !important;\">\r\n"
	            			+ "                    <img src=\"https://www.visionet.com/favicon.ico\" alt=\"Visionet Favicon\" style=\"/* height: 20px; */width: 20px;margin: 7px;padding: 0;border: none;overflow: hidden;padding-top: 7px !important;\">\r\n"
	            			+ "                    <p style=\"\r\n"
	            			+ "                            color: #fff !important;\r\n"
	            			+ "                            height: 30px;\r\n"
	            			+ "                            background-image: url(https://www.visionet.com/sites/default/files/2024-04/about-banner.png?fid=52); !important;\r\n"
	            			+ "                            font-size: 12px;\r\n"
	            			+ "                            font-family: Inter, system-ui, -apple-system, BlinkMacSystemFont, Segoe UI, Roboto, Oxygen, Ubuntu, Cantarell, Fira Sans, Droid Sans, Helvetica, Arial, sans-serif;\r\n"
	            			+ "                            align-items: center;\r\n"
	            			+ "                            line-height: 30px;\r\n"
	            			+ "                            padding-left: 30px;\r\n"
	            			+ "                            margin-top: -29px;\r\n"
	            			+ "                        \">\r\n"
	            			+                         methodName+"\r\n"
	            			+ "                    </p>\r\n"
	            			+ "                </div>\r\n"
	            			+ "                        <div class=\"postman_nav\" style=\"height: 25px;width: 248.5%!important;background-color: #fff !important;border-left: 1px solid rgb(230, 230, 230) !important;\">"
	            			+ "<img src=\"https://www.visionet.com/sites/default/files/2024-05/logo-dark.svg\" alt=\"Visionet Favicon\" style=\"height: 33px;width: 70px;margin: 7px;padding: 0;border: none;overflow: hidden;padding-bottom: 4px !important;\">"
	            			+ "</div>\r\n"
	            			+ "            <table class=\"postman_nav\" style=\"width: 248.5% !important;background-color: #fff !important;margin-top: 0px !important;cursor: pointer;padding-top: 0px;line-height: 0px;white-space: nowrap;overflow: hidden;text-overflow: ellipsis !important;height: 41px;\">\r\n"
	            			+ "            <tbody><tr><td class=\"Post_cleft\" style=\"\r\n"
	            			+ "                                    width: 0px !important;\r\n"
	            			+ "                                    border-top: 1px solid rgb(255, 108, 55);\r\n"
	            			+ "                                    border-left: 1px solid rgb(230, 230, 230) !important;\r\n"
	            			+ "                                    border-bottom: none !important;\r\n"
	            			+ "                                    font-size: 12px;\r\n"
	            			+ "                                    font-family: 'Roboto';\r\n"
	            			+ "                                    cursor: pointer;\r\n"
	            			+ "                                    padding-top: 0px;\r\n"
	            			+ "                                    line-height: 0px;\r\n"
	            			+ "                                    white-space: nowrap;\r\n"
	            			+ "                                    overflow: hidden;\r\n"
	            			+ "                                    text-overflow: ellipsis !important;\r\n"
	            			+ "                                    font-style: italic;\r\n"
	            			+ "                                    height: 41px;\r\n"
	            			+ "                                    flex-direction: row;\r\n"
	            			+ "                                    min-width: 0;\r\n"
	            			+ "                                    overflow-x: scroll;\r\n"
	            			+ "                                    scrollbar-width: none;\r\n"
	            			+ "                                    align-items: center;\r\n"
	            			+ "                                    /* width: 800px !important; */\r\n"
	            			+ "                                \">\r\n"
	            			+ "                                \r\n"
	            			+ "                                \r\n"
	            			+ "                    <span class=\"post_method\" style=\"color: #B3205C;font-size: 10px;font-family: Inter, system-ui, -apple-system, BlinkMacSystemFont, Segoe UI, Roboto, Oxygen, Ubuntu, Cantarell, Fira Sans, Droid Sans, Helvetica, Arial, sans-serif;align-items: center;font-weight: bold;\">\r\n"
	            			+ methodName+"\r\n"
	            			+ "                    </span>\r\n"
	            			+ "                     <span class=\"post_url\" style=\"\r\n"
	            			+ "                        \r\n"
	            			+ "                         /* Set the width you want */\r\n"
	            			+ "                        text-overflow: ellipsis !important;\r\n"
	            			+ "                        overflow: hidden;\r\n"
	            			+ "                        white-space: nowrap;\r\n"
	            			+ "                        width: 100%;\r\n"
	            			+ "                        max-width: 200px; /* Adjust the width as needed */\r\n"
	            			+ "                        font-style: italic;\r\n"
	            			+ "                        height: 41px;\r\n"
	            			+ "                        \">\r\n"
	            			+ "                        &nbsp;&nbsp;"+apiUrl+"\r\n"
	            			+ "                    </span>\r\n"
	            			+ "                \r\n"
	            			+ "                                \r\n"
	            			+ "                            </td>\r\n"
	            			+ "                            <td class=\"Post_cright\" style=\"border-bottom: 1px solid rgb(230, 230, 230) !important;width: 69% !important;border-left: 1px solid rgb(230, 230, 230) !important;\">\r\n"
	            			+ "                                <span class=\"pluss\" style=\"font-size: 20px; cursor: pointer;\">&nbsp;＋ ◦◦◦</span>\r\n"
	            			+ "                            </td>\r\n"
	            			+ "                        </tr>\r\n"
	            			+ "                    </tbody>\r\n"
	            			+ "                </table>\r\n"
	            			+ "                        <div class=\"postman_urllbl\" style=\"\r\n"
	            			+ "                                width: 246.5% !important;\r\n"
	            			+ "                                background-color: #fff !important;\r\n"
	            			+ "                                border-left: 1.5px solid rgb(230, 230, 230) !important;\r\n"
	            			+ "                                font-family: 'Roboto';\r\n"
	            			+ "                                font-size: 12px;\r\n"
	            			+ "                                font-weight: 600;\r\n"
	            			+ "                                border-right: 1px solid rgb(230, 230, 230) !important;\r\n"
	            			+ "                                padding-left: 9px;\r\n"
	            			+ "                                padding-bottom: 11px;\r\n"
	            			+ "                                padding-top: 10px;\r\n"
	            			+ "                                border-bottom: 1.5px solid rgb(230, 230, 230) !important;\r\n"
	            			+ "                                overflow: hidden !important;\r\n"
	            			+ "                                text-overflow: ellipsis !important;\r\n"
	            			+ "                                white-space: nowrap;\r\n"
	            			+ "                                border-top: 1px solid rgb(230, 230, 230) !important;/\r\n"
	            			+ "                            \">"+apiUrl+"</div>\r\n"
	            			+ "                        <div class=\"postman_space\" style=\"height: 9px;width: 248.5% !important;background-color: #fff !important;border-left: 1.5px solid rgb(230, 230, 230) !important;\"></div>\r\n"
	            			+ "                        <div style=\"width: 248.5% !important;display: table;background-color: #fff !important;height: 55px;\">\r\n"
	            			+ "                    <div style=\"display: table-row;background-color: #fff !important;/* height: 75px; */\">\r\n"
	            			+ "                        <div class=\"post_man_method\" style=\"\r\n"
	            			+ "                                width: 75px !important;\r\n"
	            			+ "                                background-color: rgb(237, 237, 237) !important;\r\n"
	            			+ "                                border: 1px solid #e6e6e6;\r\n"
	            			+ "                                color: #B3205C !important;\r\n"
	            			+ "                                font-family: Roboto;\r\n"
	            			+ "                                margin: auto;\r\n"
	            			+ "                                padding-right: 27px;\r\n"
	            			+ "                                font-weight: 600;\r\n"
	            			+ "                                white-space: nowrap;\r\n"
	            			+ "                                padding-bottom: 11px;\r\n"
	            			+ "                                padding-top: 0px;\r\n"
	            			+ "                                margin-left: 1%;\r\n"
	            			+ "                                padding-left: 4px;\r\n"
	            			+ "                                cursor: text;\r\n"
	            			+ "                                box-sizing: border-box;\r\n"
	            			+ "                                border-left-width: 1px;\r\n"
	            			+ "                                border-bottom-width: 1px;\r\n"
	            			+ "                                border-right-width: 0;\r\n"
	            			+ "                                border-top-width: 1px;\r\n"
	            			+ "                                flex: 0 0 30px;\r\n"
	            			+ "                                height: 39px;\r\n"
	            			+ "                                font-size: 12px;\r\n"
	            			+ "                                border-radius: 5px;\r\n"
	            			+ "                            \">\r\n"
	            			+                             methodName+"\r\n"
	            			+ "                                   <div style=\"padding-right: 8px !important;margin-top: 0px;margin-left: -10px;background-color: transparent;width: 0px;\" class=\"btn dropdown-button\" tabindex=\"0\" data-testid=\"base-button\" role=\"button\" aria-label=\"base-button\"><i color=\"aether-icon-default-color\" class=\"IconWrapper__IconContainer-gnjn48-0 eTmUbn dropdown-caret\" title=\"\" data-testid=\"aether-icon\"><svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\"><path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M8.00004 9.29294L4.35359 5.64649L3.64648 6.3536L8.00004 10.7072L12.3536 6.3536L11.6465 5.64649L8.00004 9.29294Z\" fill=\"#6B6B6B\"></path></svg></i></div>\r\n"
	            			+ "                        </div>\r\n"
	            			+ "            <div class=\"post_man_url\" style=\"\r\n"
	            			+ "                                flex: 1;\r\n"
	            			+ "                                min-width: 0;\r\n"
	            			+ "                                width: 70% !important;\r\n"
	            			+ "                                background-color: #ededed !important;\r\n"
	            			+ "                                color: rgb(33, 33, 33) !important;\r\n"
	            			+ "                                font-family: Roboto;\r\n"
	            			+ "                                font-size: 11px;\r\n"
	            			+ "                                white-space: nowrap;\r\n"
	            			+ "                                cursor: text !important;\r\n"
	            			+ "                                padding-left: 9px;\r\n"
	            			+ "                                padding-right: 3px;\r\n"
	            			+ "                                overflow: hidden !important;\r\n"
	            			+ "                                text-overflow: ellipsis !important;\r\n"
	            			+ "                                border: 1px solid #e6e6e6;\r\n"
	            			+ "                                height: 39px;\r\n"
	            			+ "                                margin-top: -40px;\r\n"
	            			+ "                                margin-left: 90px !important;\r\n"
	            			+ "                                padding-top: 10px !important;\r\n"
	            			+ "                                border-radius: 5px;\r\n"
	            			+ "                                \">                "+apiUrl+"\r\n"
	            			+ "                        </div>\r\n"
	            			+ "                        <div style=\"display: table-cell; background-color: #fff !important;\">                <button type=\"button\" class=\"down_meth_Send\" style=\"\r\n"
	            			+ "                                    color: #fff !important;\r\n"
	            			+ "                                    background-color: rgb(9, 123, 237) !important;\r\n"
	            			+ "                                    width: 88px !important;\r\n"
	            			+ "                                    font-size: 12px;\r\n"
	            			+ "                                    font-weight: 600;\r\n"
	            			+ "                                    font-family: Roboto !important;\r\n"
	            			+ "                                    border: none;\r\n"
	            			+ "                                    border-radius: 5px;\r\n"
	            			+ "                                    margin-left: -115%;\r\n"
	            			+ "                                    height: 40px;\r\n"
	            			+ "                                    cursor: pointer;\r\n"
	            			+ "                                    word-spacing: 10px;\r\n"
	            			+ "                                    text-align: center;\r\n"
	            			+ "                                    box-sizing: border-box;\r\n"
	            			+ "                                    border-left: 1px solid #e6e6e6;\r\n"
	            			+ "                                \">\r\n"
	            			+ "                                Send\r\n"
	            			+ "                                <span style=\"border-left: 1px solid #e6e6e6;\">\r\n"
	            			+ "                                    <svg class=\"arrow-icon\" xmlns=\"http://www.w3.org/2000/svg\" width=\"12\" height=\"12\" viewBox=\"0 0 12 12\" fill=\"#fff;\" style=\"margin-bottom: -2px;fill: #fff !important;margin-left: 8px;border-right: 1px #fff;\">\r\n"
	            			+ "                                        <g>\r\n"
	            			+ "                                            <path d=\"M10.375,3.219,6,6.719l-4.375-3.5A1,1,0,1,0,.375,4.781l5,4a1,1,0,0,0,1.25,0l5-4a1,1,0,0,0-1.25-1.562Z\"></path>\r\n"
	            			+ "                                        </g>\r\n"
	            			+ "                                    </svg>\r\n"
	            			+ "                                </span>\r\n"
	            			+ "                            </button>\r\n"
	            			+ "                        </div>\r\n"
	            			+ "                    </div>\r\n"
	            			+ "                </div>\r\n"
	            			+ "                        <div class=\"collapse navbar-collapse\" id=\"navbarSupportedContentBottom\" style=\"background-color: #fff !important;margin-top: -13px;/* margin-left: -37px; */width: 248.5%;border-bottom: 1px solid #e6e6e6;\">\r\n"
	            			+ "                            <ul id=\"menu\" style=\"display: flex;white-space: nowrap;background-color: #fff !important;cursor: pointer;padding-left: 0px;\">\r\n"
	            			+ "                                <li style=\"display: inline; float: left; padding: 10px; text-decoration: none; background-color: #fff;  font-family: Roboto; color: #6b6b6b;\">Params</li>\r\n"
	            			+ "                                <li style=\"display: inline; float: left; padding: 10px; text-decoration: none;  font-family: Roboto; background-color: #fff; color: #6b6b6b;\">Authorization</li>\r\n"
	            			+ "                                <li style=\"display: inline-block; float: left; padding: 10px; text-decoration: none;  font-family: Roboto; background-color: #fff; color: #6b6b6b;\">\r\n"
	            			+ "                                    Headers <span class=\"post_dot\" style=\"color: #007f31; font-family: Roboto;\">&nbsp;"+expectedResponseHeaderCount+"</span>\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline-block; float: left; padding: 10px; text-decoration: none;  font-family: Roboto; border-bottom: 2px solid #E26856; background-color: #fff; color: #212121; padding-top: 5px !important;\">\r\n"
	            			+ "                                    Body <span class=\"post_dot\" style=\"color: #0cbb52; font-size: 18px; margin-top: 26px !important;\">&nbsp;●</span>\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline-block; float: left; padding: 10px; text-decoration: none;  font-family: Roboto; background-color: #fff; color: #6b6b6b;\">Pre-request Scripts</li>\r\n"
	            			+ "                                <li style=\"display: inline-block; float: left; padding: 10px; text-decoration: none;  font-family: Roboto; background-color: #fff; color: #6b6b6b;\">Tests</li>\r\n"
	            			+ "                                <li style=\"display: inline-block; float: left; padding: 10px; text-decoration: none;  font-family: Roboto; background-color: #fff; color: #6b6b6b;\">Settings</li>\r\n"
	            			+ "                                <li style=\"display: inline-block; float: right !important; padding: 10px; text-decoration: none;  font-family: Roboto; background-color: #fff; color: #0265d2; padding-left: 33.6px !important; font-weight: 500;\">\r\n"
	            			+ "                                    Cookies\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                            </ul>\r\n"
	            			+ "                        </div>\r\n"
	            			+ "                        <div class=\"collapse data-collapse\" id=\"rdonavbarSupportedContentBottom\" style=\"background-color: #fff !important;display: inline-block;width: 248.5% !important;border-bottom: 1px solid #e6e6e6;margin-top: 0px;padding-top: 8px;\">\r\n"
	            			+ "                            <ul id=\"data\" style=\"white-space: nowrap;background-color: #fff !important;cursor: pointer;line-height: 1;display: flex;padding-left: 0px;\">\r\n"
	            			+ "                                <li style=\"display: inline; float: left; padding: 5px; text-decoration: none; background-color: #fff;  font-family: Roboto; color: #212121;\">\r\n"
	            			+ "                                    <span class=\"data_dot\" style=\"color: #bfbfbf; font-size: 30px; vertical-align: sub;\">●&nbsp;</span>none\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline; float: left; padding: 5px; text-decoration: none; background-color: #fff;  font-family: Roboto; color: #212121;\">\r\n"
	            			+ "                                    <span class=\"data_dot\" style=\"color: #bfbfbf; font-size: 30px; vertical-align: sub;\">●&nbsp;</span>form-data\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline; float: left; padding: 5px; text-decoration: none; background-color: #fff;  font-family: Roboto; color: #212121; vertical-align: middle;\">\r\n"
	            			+ "                                    <span class=\"data_dot\" style=\"color: #bfbfbf; font-size: 30px; vertical-align: sub;\">●&nbsp;</span>x-www-form-unlencoded\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline;float: left;padding: 5px;text-decoration: none;background-color: #fff;font-family: Roboto;color: #212121;vertical-align: middle !important;margin-top: 7px !important;\">\r\n"
	            			+ "                            <div class=\"radio-item\"><input type=\"radio\" checked=\"checked\" id=\"ritema\" name=\"ritem\" value=\"ropt1\"> <label for=\"ritema\">raw</label></div><div class=\"post_dot\" style=\"color: rgb(255, 108, 55);font-size: 22px !important;margin-top: -25px;padding: 0 !important;margin-left: 0px;margin-right: 0px;\">&nbsp;●</div>\r\n"
	            			+ "                        </li>\r\n"
	            			+ "                                <li style=\"display: inline; float: left; padding: 5px; text-decoration: none; background-color: #fff;  font-family: Roboto; color: #212121;\">\r\n"
	            			+ "                                    <span class=\"data_dot\" style=\"color: #bfbfbf; font-size: 30px; vertical-align: sub;\">●&nbsp;</span>binary\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline; float: left; padding: 5px; text-decoration: none; background-color: #fff;  font-family: Roboto; color: #212121;\">\r\n"
	            			+ "                                    <span class=\"data_dot\" style=\"color: #bfbfbf; font-size: 30px; vertical-align: sub;\">●&nbsp;</span>GraphQL\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline; padding: 11px; text-decoration: none; background-color: #fff;  font-family: Roboto; color: rgb(2, 101, 210); float: none; font-weight: 600;margin-top: 4px;\">\r\n"
	            			+ "                                    JSON&nbsp;&nbsp;\r\n"
	            			+ "                                    <span class=\"Jsonbody\" style=\"vertical-align: sub;\">\r\n"
	            			+ "                                        <svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\r\n"
	            			+ "                                            <path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M8.00004 9.29294L4.35359 5.64649L3.64648 6.3536L8.00004 10.7072L12.3536 6.3536L11.6465 5.64649L8.00004 9.29294Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                        </svg>\r\n"
	            			+ "                                    </span>\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                            </ul>\r\n"
	            			+ "                        </div>\r\n"
	            			+ "                        <div class=\"collapse jsonbody-collapse\" id=\"rdonavbarSupportedContentjsonbody\" style=\"background-color: #fff !important;height: 152px;width: 248.5% !important;border-bottom: 1px solid #e6e6e6;\">\r\n"
	            			+ "                            <textarea id=\"w3review\" name=\"w3review\" rows=\"4\" cols=\"50\" style=\"resize: none;background-color: #fff;border: 1px solid #e6e6e6;border-radius: 5px;word-break: break-word;margin: 11px;width: 97%;height: 88%;white-space: break-spaces !important;padding-left: 5px;display: inline-block;overflow: scroll;padding-top: 9px;\">"+body+"</textarea>\r\n"
	            			+ "                        </div>\r\n"
	            			+ "                    \r\n"
	            			+ "                        <div class=\"collapse data-collapse\" style=\"background-color: #fff !important;display: inline-block;width: 248.5%!important;border-bottom: 1px solid #e6e6e6;margin-top: 0px;height: 58px !important;padding-left: 0px;\">\r\n"
	            			+ "                            <ul id=\"data\" style=\"white-space: nowrap;background-color: #fff !important;cursor: pointer;padding-left: 6px;\">\r\n"
	            			+ "                                <li style=\"\r\n"
	            			+ "                                        display: inline;\r\n"
	            			+ "                                        float: left;\r\n"
	            			+ "                                        padding: 5px;\r\n"
	            			+ "                                        text-decoration: none;\r\n"
	            			+ "                                        background-color: #fff;\r\n"
	            			+ "                                        \r\n"
	            			+ "                                        border-bottom: 2px solid #E26856;\r\n"
	            			+ "                                        font-family: Roboto;\r\n"
	            			+ "                                        color: #212121;\r\n"
	            			+ "                                        padding-bottom: 13px !important;\r\n"
	            			+ "                                        margin-bottom: 5px;\r\n"
	            			+ "                                    \">\r\n"
	            			+ "                                    Body\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline; float: left; padding: 5px; text-decoration: none; background-color: #fff;  font-family: Roboto; color: #6b6b6b;\">Cookies</li>\r\n"
	            			+ "                                <li style=\"display: inline; float: left; padding: 5px; text-decoration: none; background-color: #fff;  font-family: Roboto; color: #6b6b6b;\">\r\n"
	            			+ "                                    Headers<span class=\"response-viewer-tabs-content-count\" style=\"margin-left: 4px; color: #0cbb52;\"> "+receivedHeaderCount+"</span>\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline; float: left; padding: 5px; text-decoration: none; background-color: #fff;  font-family: Roboto; color: #6b6b6b;\">Test Results</li>\r\n"
	            			+ "                                <div style=\"display: flex;float: left;padding: 5px;text-decoration: none;background-color: #fff;margin-left: 95px;\">\r\n"
	            			+ "                                    <span class=\"pm-icon pm-icon-sm pm-icon-normal network-icon secure\" style=\"margin-top: 2px; margin-left: -86px \">\r\n"
	            			+ "                                        <svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\">\r\n"
	            			+ "                                            <defs>\r\n"
	            			+ "                                                <path id=\"network-secure\" d=\"M7 14C7.25621 14 7.61626 13.8497 8 13.4043V14.9291C7.6734 14.9758 7.33952 15 7 15C3.13401 15 0 11.866 0 8C0 4.13401 3.13401 1 7 1C10.1952 1 12.8904 3.14083 13.7295 6.06637C13.493 6.02278 13.2491 6 13 6H12.6586C12.5319 5.64144 12.372 5.29852 12.1827 4.97492C11.6348 5.31335 11.009 5.59672 10.326 5.81425C10.3539 5.98431 10.3787 6.15725 10.4001 6.33279C10.0573 6.48255 9.73957 6.67897 9.45489 6.91402C9.43054 6.62449 9.39671 6.34289 9.3543 6.07052C8.61147 6.22804 7.81942 6.31246 6.99994 6.31246C6.18051 6.31246 5.3885 6.22805 4.64569 6.07054C4.55187 6.67322 4.5 7.32109 4.5 8C4.5 8.67895 4.55187 9.32686 4.64571 9.92958C5.38851 9.77207 6.18051 9.68766 6.99994 9.68766C7.34156 9.68766 7.67841 9.70233 8.00891 9.73085C8.003 9.8198 8 9.90955 8 10V10.734C7.67457 10.7035 7.34064 10.6877 6.99994 10.6877C6.24147 10.6877 5.51656 10.7664 4.84427 10.9097C4.99643 11.5114 5.19259 12.0486 5.41955 12.5025C5.99978 13.663 6.61633 14 7 14ZM4.84424 5.09045C4.9964 4.4887 5.19258 3.95142 5.41955 3.49747C5.99978 2.33702 6.61633 2 7 2C7.38367 2 8.00022 2.33702 8.58045 3.49747C8.80742 3.95141 9.00359 4.48869 9.15576 5.09042C8.48343 5.2337 7.75847 5.31246 6.99994 5.31246C6.24146 5.31246 5.51654 5.23371 4.84424 5.09045ZM11.6064 4.15516C11.1699 4.41889 10.6703 4.64698 10.1212 4.82928C9.87367 3.85659 9.51582 3.01503 9.0806 2.37055C10.0724 2.73725 10.9408 3.35856 11.6064 4.15516ZM3.87879 4.82932C3.32965 4.64702 2.83005 4.41893 2.39356 4.1552C3.05918 3.35859 3.92757 2.73726 4.9194 2.37055C4.48418 3.01504 4.12632 3.85661 3.87879 4.82932ZM1.81725 4.97497C2.36521 5.3134 2.99104 5.59676 3.674 5.81428C3.56108 6.502 3.5 7.23673 3.5 8C3.5 8.76331 3.56109 9.49809 3.67402 10.1858C2.99107 10.4033 2.36525 10.6867 1.81731 11.0251C1.29776 10.1369 1 9.10325 1 8C1 6.89679 1.29774 5.86312 1.81725 4.97497ZM4.9194 13.6294C3.9276 13.2628 3.05923 12.6415 2.39363 11.8449C2.83011 11.5812 3.32969 11.3531 3.87882 11.1708C4.12634 12.1435 4.48419 12.985 4.9194 13.6294Z M12.75 12.4331C12.8995 12.3467 13 12.1851 13 12C13 11.7239 12.7761 11.5 12.5 11.5C12.2239 11.5 12 11.7239 12 12C12 12.1851 12.1005 12.3467 12.25 12.4331V13.5H12.75V12.4331Z M10.5 10H10C9.44772 10 9 10.4477 9 11V14C9 14.5523 9.44771 15 10 15H15C15.5523 15 16 14.5523 16 14V11C16 10.4477 15.5523 10 15 10H14.5L14.5 8.98224C14.5003 8.77423 14.5009 8.28481 14.245 7.84378C13.9502 7.33553 13.3895 7 12.5 7C11.6105 7 11.0499 7.33553 10.755 7.84378C10.4992 8.28481 10.4998 8.77423 10.5 8.98224L10.5 10ZM11.62 8.34556C11.5085 8.53789 11.5 8.78247 11.5 9V10H13.5V9C13.5 8.78247 13.4916 8.53789 13.38 8.34556C13.2999 8.20736 13.1105 8 12.5 8C11.8895 8 11.7002 8.20736 11.62 8.34556ZM15 11H10V14H15V11Z\"></path>\r\n"
	            			+ "                                            </defs>\r\n"
	            			+ "                                            <use fill=\"#6B6B6B\" fill-rule=\"evenodd\" href=\"#network-secure\"></use>\r\n"
	            			+ "                                        </svg>\r\n"
	            			+ "                                    </span>\r\n"
	            			+ "                                </div>\r\n"
	            			+ "                                <li style=\"display: flex;float: left;padding: 5px;text-decoration: none;font-size: 11px;font-family: Roboto;color: rgb(0, 127, 49);margin-top: 2px;margin-left: -72px;\">"+statusCode+"</li>\r\n"
	            			+ "                                <li style=\"display: inline;float: left;padding: 5px;text-decoration: none;font-size: 11px;font-family: Roboto;color: rgb(0, 127, 49);margin-top: 2px;margin-left: -50px;\">"+repStatusLine+"</li>\r\n"
	            			+ "                                <li style=\"display: inline; float: left; padding: 5px; text-decoration: none; font-size: 11px; font-family: Roboto; color: rgb(0, 127, 49);margin-left: 7px;margin-top: 3px;font-size: smaller;\">"+responseTime+" ms</li>\r\n"
	            			+ "                                <li style=\"\r\n"
	            			+ "                                        display: inline;\r\n"
	            			+ "                                        float: left;\r\n"
	            			+ "                                        padding: 5px;\r\n"
	            			+ "                                        text-decoration: none;\r\n"
	            			+ "                                        background-color: #fff;\r\n"
	            			+ "                                        \r\n"
	            			+ "                                        font-family: Roboto;\r\n"
	            			+ "                                        color: rgb(0, 127, 49);\r\n"
	            			+ "                                        padding-left: 5px;\r\n"
	            			+ "                                        text-align: justify;\r\n"
	            			+ "                                        justify-content: right;    margin-left: -1px;    margin-top: 3px;\r\n"
	            			+ "                                    \">\r\n"
	            			+ "                    1290 B\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"\r\n"
	            			+ "                                        display: inline;\r\n"
	            			+ "                                        float: left;\r\n"
	            			+ "                                        padding: 5px;\r\n"
	            			+ "                                        text-decoration: none;\r\n"
	            			+ "                                        background-color: rgba(0, 0, 0, 0);\r\n"
	            			+ "                                        \r\n"
	            			+ "                                        font-family: Roboto;\r\n"
	            			+ "                                        border-bottom-left-radius: 4px;\r\n"
	            			+ "                                        border-bottom-right-radius: 4px;\r\n"
	            			+ "                                        border-top-left-radius: 4px;\r\n"
	            			+ "                                        border-top-right-radius: 4px;\r\n"
	            			+ "                                        box-sizing: border-box;\r\n"
	            			+ "                                        color:#0265D2;\r\n"
	            			+ "                                        border-left: 1px solid #ededed;\r\n"
	            			+ "                                        cursor: pointer;\r\n"
	            			+ "                                        margin-top: -37px; margin-left: 548px;  font-weight: 600\">\r\n"
	            			+ "                                    Save Response\r\n"
	            			+ "                                    <i class=\"IconWrapper__IconContainer-r96cto-0 gJkKrF dropdown-caret\" title=\"\" style=\"margin: 4px; vertical-align: middle;\">\r\n"
	            			+ "                                        <svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\r\n"
	            			+ "                                            <path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M8.00004 9.29294L4.35359 5.64649L3.64648 6.3536L8.00004 10.7072L12.3536 6.3536L11.6465 5.64649L8.00004 9.29294Z\" fill=\"#0265D2\"></path>\r\n"
	            			+ "                                        </svg>\r\n"
	            			+ "                                    </i>\r\n"
	            			+ "                             </li>\r\n"
	            			+ "                            </ul>\r\n"
	            			+ "                        </div>\r\n"
	            			+ "                        <div class=\"tabdata-collapse\" style=\"background-color: #fff !important;width: 248.5% !important;margin-top: -13px;padding-left: 0px;\">\r\n"
	            			+ "                            <ul id=\"data\" style=\"white-space: nowrap;background-color: #fff !important;cursor: pointer;line-height: 1;display: flex;padding-left: 0px;padding-top: 9px;\">\r\n"
	            			+ "                                <div style=\"background-color: #e6e6e6;float: left;overflow: hidden;padding: 14px 13px;border-top-left-radius: 4px;border-bottom-left-radius: 4px;margin-left: 3px;/* padding-top: 9px; */\">\r\n"
	            			+ "                                    <li style=\"display: inline; border-top-left-radius: 4px; border-bottom-left-radius: 4px; color:#212121;small;font-family: Roboto;\">Pretty</li>\r\n"
	            			+ "                                </div>\r\n"
	            			+ "                                <div style=\"background-color: #f2f2f2; float: left; overflow: hidden; padding: 14px 12px;\">\r\n"
	            			+ "                                    <li style=\"display: inline;color:#6b6b6b;small;font-family: Roboto;\">Raw</li>\r\n"
	            			+ "                                </div>\r\n"
	            			+ "                                <div style=\"background-color: #f2f2f2; float: left; overflow: hidden; padding: 14px 12px;\">\r\n"
	            			+ "                                    <li style=\"display: inline;color:#6b6b6b;small;font-family: Roboto;\">Preview</li>\r\n"
	            			+ "                                </div>\r\n"
	            			+ "                                <div style=\"background-color: #f2f2f2; float: left; overflow: hidden; padding: 14px 12px; border-top-right-radius: 4px; border-bottom-right-radius: 4px;\">\r\n"
	            			+ "                                    <li style=\"display: inline;overflow: hidden;color:#6b6b6b;small;font-family: Roboto;\">Visualize</li>\r\n"
	            			+ "                                </div>\r\n"
	            			+ "                                <li style=\"display: inline;float: left;padding: 5px;overflow: hidden;border-radius:4px;background-color: #fff;color:#6b6b6b;small;font-family: Roboto;\"></li>\r\n"
	            			+ "                                <div style=\"background-color: #f2f2f2;float: left;overflow: hidden;padding: 6px 13px;border-radius: 4px;margin-left: 3px;padding-top: 13px;height: 42px;\">\r\n"
	            			+ "                                    <li style=\"display: inline;color: #6b6b6b;font-family: Roboto;margin-left: 3px;\">\r\n"
	            			+ "                                        JSON\r\n"
	            			+ "                                        <span class=\"jdown\" style=\"margin: 4px; vertical-align: sub;\">\r\n"
	            			+ "                                            <svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\r\n"
	            			+ "                                                <path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M8.00004 9.29294L4.35359 5.64649L3.64648 6.3536L8.00004 10.7072L12.3536 6.3536L11.6465 5.64649L8.00004 9.29294Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            </svg>\r\n"
	            			+ "                                        </span>\r\n"
	            			+ "                                    </li>\r\n"
	            			+ "                                </div>\r\n"
	            			+ "                                <div style=\"background-color: #f2f2f2; float: left; overflow: hidden; padding: 6px 13px; border-radius: 4px; margin-left: 7px;\">\r\n"
	            			+ "                                    <li style=\"display: inline; color: #6b6b6b; font-family: Roboto;\">\r\n"
	            			+ "                                        <span class=\"stylejdown\" style=\"margin: 4px; vertical-align: middle;\">\r\n"
	            			+ "                                            <svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\r\n"
	            			+ "                                                <path d=\"M15 3H1V2H15V3Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                                <path d=\"M12 8H1V7H12C13.6569 7 15 8.34315 15 10C15 11.6569 13.6569 13 12 13H9.70712L11.3536 14.6464L10.6465 15.3536L7.79291 12.5L10.6465 9.64645L11.3536 10.3536L9.70712 12H12C13.1046 12 14 11.1046 14 10C14 8.89543 13.1046 8 12 8Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                                <path d=\"M1 13H6V12H1V13Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            </svg>\r\n"
	            			+ "                                        </span>\r\n"
	            			+ "                                    </li>\r\n"
	            			+ "                                </div>\r\n"
	            			+ "                                <li style=\"display: inline; float: right; padding: 5px; overflow: hidden; border-radius: 4px; background-color: #fff; color: #fff; margin-left: 157px !important;\">\r\n"
	            			+ "                                    <span class=\"serchdata\" style=\"margin: 4px; vertical-align: middle;\">\r\n"
	            			+ "                                        <svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\r\n"
	            			+ "                                            <path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M6.409.534a6.513 6.513 0 0 0-5.84 5.573c-.065.425-.065 1.361 0 1.786.443 2.915 2.623 5.095 5.538 5.538.425.065 1.361.065 1.786 0a6.736 6.736 0 0 0 1.963-.606c.383-.187.949-.545 1.2-.759a.783.783 0 0 1 .162-.119c.012 0 .787.765 1.722 1.7l1.7 1.699.353-.353.353-.353-1.699-1.7c-.935-.935-1.7-1.71-1.7-1.722 0-.013.053-.085.119-.162.214-.251.572-.817.759-1.2.3-.615.499-1.261.606-1.963.065-.425.065-1.361 0-1.786C12.99 3.205 10.798 1.004 7.92.574a9.094 9.094 0 0 0-1.511-.04m1.457 1.025c1.783.304 3.257 1.385 4.053 2.974.264.525.418 1.001.525 1.615.066.373.066 1.331 0 1.704-.209 1.199-.728 2.217-1.552 3.04-.823.824-1.841 1.343-3.04 1.552-.373.066-1.331.066-1.704 0-1.201-.209-2.216-.727-3.04-1.552-.825-.824-1.343-1.839-1.552-3.04-.066-.373-.066-1.331 0-1.704.209-1.201.727-2.216 1.552-3.04.841-.842 1.995-1.418 3.119-1.558.132-.017.27-.035.306-.042.152-.026 1.094.01 1.333.051\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                        </svg>\r\n"
	            			+ "                                    </span>\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                    \r\n"
	            			+ "                                <li style=\"display: inline; float: right; padding: 5px; overflow: hidden; border-radius: 4px; background-color: #fff; color: #fff; padding-right: 12px;\">\r\n"
	            			+ "                                    <span class=\"clipboardcopy\" style=\"margin: 4px; vertical-align: middle;\">\r\n"
	            			+ "                                        <svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\r\n"
	            			+ "                                            <path d=\"M2 9.5C2 9.77614 2.22386 10 2.5 10H3V11H2.5C1.67157 11 1 10.3284 1 9.5V2.5C1 1.67157 1.67157 1 2.5 1H9.5C10.3284 1 11 1.67157 11 2.5V3H10V2.5C10 2.22386 9.77614 2 9.5 2H2.5C2.22386 2 2 2.22386 2 2.5V9.5Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M6.5 5C5.67157 5 5 5.67157 5 6.5V13.5C5 14.3284 5.67157 15 6.5 15H13.5C14.3284 15 15 14.3284 15 13.5V6.5C15 5.67157 14.3284 5 13.5 5H6.5ZM6 6.5C6 6.22386 6.22386 6 6.5 6H13.5C13.7761 6 14 6.22386 14 6.5V13.5C14 13.7761 13.7761 14 13.5 14H6.5C6.22386 14 6 13.7761 6 13.5V6.5Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                        </svg>\r\n"
	            			+ "                                    </span>\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                            </ul>\r\n"
	            			+ "                        </div>\r\n"
	            			+ "                        <div class=\"collapse jsonbody-collapse\" id=\"rdonavbarSupportedContentjsonresponse\" style=\"background-color: #fff !important; width: 248.5% !important; margin-top: -13px; height: auto !important;\">\r\n"
	            			+ "                          <textarea id=\"result\" name=\"jresulr\" rows=\"4\" cols=\"50\" style=\"resize: none;background-color: #fff;word-break: break-word;border: none;margin-bottom: 10px;white-space: break-spaces !important;padding-left: 38px;display: inherit;overflow-x: scroll;overflow-y: scroll;height: 180px;margin-top: 7px;padding-top: 25px;padding-left: 9px !important;\">{\r\n"
	            			+                       prettyResponseBody+"</textarea>\r\n"
	            			+ "                        </div>\r\n"
	            			+ "                    \r\n"
	            			+ "                        <div class=\"sub_div\" style=\"width: 251.5% !important; position: sticky; background-color: #fff; height: 26px; padding-top: 10px !important;    margin-top: -10px;\">\r\n"
	            			+ "                            <ul id=\"footerdata\" style=\"white-space: nowrap; background-color: #fff !important; cursor: pointer; border-top: 1px solid #ededed; padding: 5px 0; left: 0; right: 0; bottom: -5px;display: flow-root;\">\r\n"
	            			+ "                                <li style=\"display: inline; float: right; overflow: hidden; background-color: #fff; color: #fff; padding-right: 12px;\">\r\n"
	            			+ "                                    <span class=\"serchdata\" style=\"vertical-align: middle;\">\r\n"
	            			+ "                                        <svg width=\"12\" height=\"12\" viewBox=\"0 0 12 12\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\r\n"
	            			+ "                                            <path d=\"M5.28789 7.125V6.90967C5.28789 6.5874 5.34596 6.31934 5.46211 6.10547C5.57825 5.8916 5.7815 5.67041 6.07186 5.44189C6.41739 5.1665 6.63952 4.95264 6.73824 4.80029C6.83987 4.64795 6.89068 4.46631 6.89068 4.25537C6.89068 4.00928 6.80938 3.82031 6.64678 3.68848C6.48418 3.55664 6.25044 3.49072 5.94556 3.49072C5.66972 3.49072 5.4142 3.53027 5.17901 3.60938C4.94382 3.68848 4.71443 3.78369 4.49085 3.89502L4.125 3.12158C4.71443 2.79053 5.34596 2.625 6.0196 2.625C6.58871 2.625 7.04021 2.76562 7.37413 3.04688C7.70804 3.32812 7.875 3.71631 7.875 4.21143C7.875 4.43115 7.84306 4.62744 7.77918 4.80029C7.7153 4.97021 7.61803 5.13281 7.48737 5.28809C7.35961 5.44336 7.13749 5.64551 6.82099 5.89453C6.55096 6.1084 6.36948 6.28564 6.27657 6.42627C6.18656 6.56689 6.14155 6.75586 6.14155 6.99316V7.125H5.28789Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path d=\"M6.42936 8.625C6.42936 9.03921 6.09657 9.375 5.68604 9.375C5.27552 9.375 4.94272 9.03921 4.94272 8.625C4.94272 8.21079 5.27552 7.875 5.68604 7.875C6.09657 7.875 6.42936 8.21079 6.42936 8.625Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M12 6C12 9.31371 9.31371 12 6 12C2.68629 12 0 9.31371 0 6C0 2.68629 2.68629 0 6 0C9.31371 0 12 2.68629 12 6ZM11 6C11 8.76142 8.76142 11 6 11C3.23858 11 1 8.76142 1 6C1 3.23858 3.23858 1 6 1C8.76142 1 11 3.23858 11 6Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                        </svg>\r\n"
	            			+ "                                    </span>\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline; float: right; overflow: hidden; background-color: #fff; color: #fff; padding-right: 12px;\">\r\n"
	            			+ "                                    <span class=\"serchdata\" style=\"vertical-align: middle;\">\r\n"
	            			+ "                                        <svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\">\r\n"
	            			+ "                                            <defs>\r\n"
	            			+ "                                                <path id=\"two-pane\" d=\"M7.5 1H1v14h6.521a.974.974 0 0 1-.021-.205V1zm1 0v13.795c0 .072-.007.14-.021.205H15V1H8.5zM15 0a1 1 0 0 1 1 1v14a1 1 0 0 1-1 1H1a1 1 0 0 1-1-1V1a1 1 0 0 1 1-1h14zM2.25 10V6h4v4h-4zm9.5 0a2 2 0 1 1 0-4 2 2 0 0 1 0 4z\"></path>\r\n"
	            			+ "                                            </defs>\r\n"
	            			+ "                                            <use fill=\"gray\" fill-rule=\"evenodd\" xlink:href=\"#two-pane\"></use>\r\n"
	            			+ "                                        </svg>\r\n"
	            			+ "                                    </span>\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline; float: right; border-radius: 4px; background-color: #fff; color: #6b6b6b; padding-right: 12px;\">\r\n"
	            			+ "                                    <span class=\"serchdata\" style=\"vertical-align: middle;\">\r\n"
	            			+ "                                        <svg width=\"12\" height=\"12\" viewBox=\"0 0 12 12\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\r\n"
	            			+ "                                            <path d=\"M4 1V0H8V1H11V2H1V1H4Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path d=\"M3 10.5V3H2V10.5C2 11.3284 2.67157 12 3.5 12H8.5C9.32843 12 10 11.3284 10 10.5V3H9V10.5C9 10.7761 8.77614 11 8.5 11H3.5C3.22386 11 3 10.7761 3 10.5Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path d=\"M4.25 10V3H5.25V10H4.25Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path d=\"M6.75 3V10H7.75V3H6.75Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                        </svg>\r\n"
	            			+ "                                    </span>\r\n"
	            			+ "                                    &nbsp;Trash\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline; float: right; overflow: hidden; background-color: #fff; color: #6b6b6b; padding-right: 12px;\">\r\n"
	            			+ "                                    <span class=\"serchdata\" style=\"vertical-align: middle;\">\r\n"
	            			+ "                                        <svg width=\"12\" height=\"12\" viewBox=\"0 0 12 12\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\r\n"
	            			+ "                                            <path d=\"M4.64999 4.35323V7.64676C4.64999 7.80222 4.81959 7.89824 4.95289 7.81825L7.6975 6.17149C7.82696 6.09381 7.82696 5.90618 7.6975 5.8285L4.95289 4.18173C4.81959 4.10175 4.64999 4.19777 4.64999 4.35323Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M2.5 1C1.67157 1 1 1.67157 1 2.5V9.5C1 10.3284 1.67157 11 2.5 11H9.5C10.3284 11 11 10.3284 11 9.5V2.5C11 1.67157 10.3284 1 9.5 1H2.5ZM2 2.5C2 2.22386 2.22386 2 2.5 2H9.5C9.77614 2 10 2.22386 10 2.5V9.5C10 9.77614 9.77614 10 9.5 10H2.5C2.22386 10 2 9.77614 2 9.5V2.5Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                        </svg>\r\n"
	            			+ "                                        &nbsp;Runner\r\n"
	            			+ "                                    </span>\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline; float: right; overflow: hidden; background-color: #fff; color: #6b6b6b; padding-right: 12px;\">\r\n"
	            			+ "                                    <span class=\"serchdata\" style=\"vertical-align: middle;\">\r\n"
	            			+ "                                        <svg width=\"12\" height=\"12\" viewBox=\"0 0 12 12\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\r\n"
	            			+ "                                            <path d=\"M8.64648 3.64645L5.5 6.79291L3.85364 5.14645L3.14648 5.85356L5.5 8.20711L9.35364 4.35356L8.64648 3.64645Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M12 6C12 9.31371 9.31372 12 6 12C2.68628 12 0 9.31371 0 6C0 2.68629 2.68628 0 6 0C9.31372 0 12 2.68629 12 6ZM11 6C11 8.76143 8.76147 11 6 11C3.23853 11 1 8.76143 1 6C1 3.23857 3.23853 1 6 1C8.76147 1 11 3.23857 11 6Z\" fill=\"#007f31\"></path>\r\n"
	            			+ "                                        </svg>\r\n"
	            			+ "                                        &nbsp;Auto-select agent\r\n"
	            			+ "                                    </span>\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline; float: right; overflow: hidden; background-color: #fff; color: #6b6b6b; padding-right: 12px;\">\r\n"
	            			+ "                                    <span class=\"serchdata\" style=\"vertical-align: middle;\">\r\n"
	            			+ "                                        <svg width=\"12\" height=\"12\" viewBox=\"0 0 12 12\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\r\n"
	            			+ "                                            <path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M5.66308 1.58913C5.88209 1.51126 6.12139 1.51211 6.33984 1.59155L11.4407 3.44642C11.9402 3.62806 11.9726 4.31862 11.5 4.54897V6.5H10.5V5.00377L10 5.23105V8.42425C10 8.5697 9.95733 8.72992 9.84933 8.86817C9.57018 9.2255 8.40031 10.5 6 10.5C3.5997 10.5 2.42983 9.22553 2.15067 8.8682C2.04267 8.72995 2 8.56973 2 8.42428V5.26939L0.400766 4.55861C-0.0944949 4.3385 -0.0672027 3.62656 0.443444 3.445L5.66308 1.58913ZM6.41149 6.86219L9 5.68559V8.32647C8.7305 8.63519 7.81585 9.49997 6 9.49997C4.18415 9.49997 3.2695 8.63521 3 8.3265V5.71383L5.59155 6.86563C5.85279 6.98174 6.15124 6.98048 6.41149 6.86219ZM1.72227 4.05163L5.99769 5.95182L10.1785 4.05147L5.99809 2.53134L1.72227 4.05163Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                        </svg>\r\n"
	            			+ "                                        &nbsp;Bootcamp\r\n"
	            			+ "                                    </span>\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline; float: right; overflow: hidden; background-color: #fff; color: #6b6b6b; padding-right: 12px;\">\r\n"
	            			+ "                                    <span class=\"serchdata\" style=\"vertical-align: middle;\">\r\n"
	            			+ "                                        <svg width=\"12\" height=\"12\" viewBox=\"0 0 12 12\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\r\n"
	            			+ "                                            <path d=\"M4.04186 2.73689C4.34794 2.73689 4.76912 2.73689 4.76912 3.04531C4.76912 3.35372 4.65454 3.73962 4.34794 3.73962C4.04186 3.73962 3.4297 3.4312 3.4297 3.1228C3.4297 2.81386 3.73578 2.73689 4.04186 2.73689Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path d=\"M6.62093 4.04807C6.46814 3.77814 6.23846 3.58519 5.97058 3.73965C5.7027 3.89413 5.74089 4.58792 5.93187 4.85784C6.08516 5.12776 6.42944 5.0123 6.69732 4.85784C6.9652 4.70338 6.77423 4.31799 6.62093 4.04807Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path d=\"M7.02678 8.74387C7.02678 9.05227 7.63894 9.36069 7.94502 9.36069C8.2511 9.36069 8.36621 8.97532 8.36621 8.6669C8.36621 8.35849 7.94501 8.35849 7.63894 8.35849C7.33287 8.35849 7.02678 8.43546 7.02678 8.74387Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path d=\"M6.56793 6.86281C6.68251 6.59289 7.21828 6.13001 7.48617 6.246C7.75405 6.36145 7.71585 6.67039 7.60075 6.94031C7.48617 7.21023 7.29467 7.59562 7.02678 7.48016C6.72123 7.36417 6.45335 7.13274 6.56793 6.86281Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path d=\"M5.1903 9.8159C5.4969 9.8159 5.61149 9.43 5.61149 9.12159C5.61149 8.81318 5.19083 8.81317 4.88422 8.8137C4.57815 8.8137 4.27206 8.89067 4.27206 9.19908C4.27206 9.50748 4.88422 9.8159 5.1903 9.8159Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path d=\"M5.1903 6.82826C5.1903 6.51985 4.76912 6.51985 4.46304 6.51985C4.15697 6.51985 3.85088 6.59682 3.85088 6.90576C3.85088 7.21416 4.46304 7.52258 4.76912 7.52258C5.07572 7.52258 5.1903 7.13668 5.1903 6.82826Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path d=\"M2.52417 4.89691C2.79205 4.74244 3.48112 4.74244 3.63391 5.01236C3.78721 5.28229 3.55752 5.51372 3.28964 5.62918C3.02175 5.78364 2.63928 5.9376 2.48598 5.66767C2.33321 5.39827 2.25629 5.05137 2.52417 4.89691Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M5.97257 0C2.69107 0 0.0026432 2.7049 0 6.01462L4.0413e-05 6.02172C0.0399375 9.29405 2.72939 12 6.01125 12C9.23034 12 11.8783 9.39721 11.9807 6.17416L11.9972 5.65643L11.4817 5.61337C11.2541 5.59435 11.0596 5.55075 10.9006 5.50016C10.7476 5.45143 10.629 5.39675 10.5469 5.35288L10.4588 5.30526L10.3797 5.28915C9.61289 5.13299 9.23701 4.69222 9.07073 4.25653C9.03709 4.16838 9.01243 4.08169 8.99453 3.9992L8.92805 3.69274L8.63009 3.59614C8.49579 3.55261 8.38188 3.49123 8.28588 3.41114C8.07671 3.23666 7.9948 3.00581 7.97439 2.79455C7.96228 2.6691 7.97112 2.54517 7.98939 2.43365L8.02447 2.21957L7.9031 2.03987C7.84009 1.94658 7.7976 1.83899 7.77679 1.72215C7.73627 1.49474 7.77966 1.2551 7.84122 1.04917C7.85431 1.00538 7.86871 0.961596 7.88406 0.918171L8.08034 0.362759L7.51218 0.209654C7.0191 0.0767808 6.49584 0 5.97257 0ZM6.01125 10.9093C3.33582 10.9093 1.12382 8.69452 1.08919 6.01195C1.09327 3.29966 3.2996 1.09066 5.97257 1.09066C6.21459 1.09066 6.45973 1.11128 6.7027 1.15003C6.66767 1.3748 6.65529 1.63729 6.70453 1.91369C6.7379 2.10098 6.79841 2.28641 6.88894 2.46197C6.87746 2.59555 6.87518 2.74334 6.89027 2.89955C6.92979 3.30875 7.09969 3.84115 7.58872 4.24911C7.72023 4.35882 7.86131 4.44789 8.00838 4.5191C8.02207 4.56087 8.03701 4.60317 8.05331 4.64588C8.3369 5.38892 8.97589 6.08944 10.0836 6.34091C10.2115 6.40605 10.3743 6.47707 10.5707 6.53957C10.6588 6.56762 10.7533 6.59381 10.8538 6.61693C10.5412 9.03378 8.47412 10.9093 6.01125 10.9093Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                        </svg>\r\n"
	            			+ "                                        &nbsp;Cookies\r\n"
	            			+ "                                    </span>\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                            </ul>\r\n"
	            			+ "                        </div></div>";
	          
	             
	        } catch (Exception e) {
	        	Log.error("Error making API request: ${error.message}");
	            logSummary += "Error making API request: ${error.message}";
	        }

		    return logSummary;
		}
	    public static String logApischemaResponse(String apiUrl, String script, int statusCode, String repStatusLine, int expectedResponseHeaderCount, long responseTime, String responseData, Map<String, String> receivedHeaders) {
		    StringBuilder log = new StringBuilder();
		    log.append("API URL: ").append(apiUrl).append("\n");

		    log.append("Status Code: ").append(statusCode).append("\n");
		    log.append("Response Status Line: ").append(repStatusLine).append("\n");
		    log.append("Expected Response Header Count: ").append(expectedResponseHeaderCount).append("\n");
		    log.append("Response Time: ").append(responseTime).append(" ms\n");
		    log.append("Response Data: ").append(responseData).append("\n");
		    String   methodName= "GET";
		    String logSummary = "";
		    try {
	            // Simulate dynamic headers based on expectedResponseHeaderCount
		    	Map<String, String> headers = new HashMap<>();

		        for (int i = 0; i < expectedResponseHeaderCount; i++) {
		            headers.put("Custom-Header-" + i, "Value-" + i);
		        }

	            String prettyResponseBody = responseData;
	            int receivedHeaderCount = receivedHeaders.size();

	            	logSummary += "<div style=\"\r\n"
	            			+ "                height: auto;\r\n"
	            			+ "                padding: 10px !important;\r\n"
	            			+ "                overflow-x: auto !important;\r\n"
	            			+ "                float: left;\r\n"
	            			+ "                overflow: scroll;\r\n"
	            			+ "                width: auto !important;\r\n"
	            			+ "                font-weight: normal !important;\r\n"
	            			+ "                /* background-color: #0973B9CC !important; */\r\n"
	            			+ "                font-family: monospace;\r\n"
	            			+ "                margin-right: -237px !important;\r\n"
	            			+ "                padding-right: 90px !important;\r\n"
	            			+ "                white-space: nowrap;\r\n"
	            			+ "                position: relative;\r\n"
	            			+"max-width: 706px;\r\n"
	            			+ "                \">\r\n"
	            			+ "            <div class=\"postman_nav\" style=\"height: 30px;width: 248.5% !important;background-color: #0973b9 !important;\">\r\n"
	            			+ "                    <svg width=\"15\" height=\"15\" float=\"right\" viewBox=\"0 0 32 32\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\" style=\"/* display: block; */ /* position: absolute; */ margin: 7px; padding: 0; border: none; overflow: hidden;\">\r\n"
	            			+ "                        <path d=\"M18.0379 0.13033C14.8991 -0.272747 11.7113 0.264019 8.87752 1.67275C6.04379 3.08148 3.69144 5.29889 2.11796 8.04457C0.544486 10.7903 -0.179442 13.9409 0.0377277 17.098C0.254897 20.2551 1.40341 23.277 3.33802 25.7813C5.27263 28.2857 7.90645 30.1601 10.9064 31.1676C13.9063 32.175 17.1376 32.2702 20.1917 31.4412C23.2457 30.6121 25.9854 28.896 28.0641 26.5099C30.1428 24.1237 31.4672 21.1748 31.8699 18.0359C32.4098 13.8273 31.2559 9.57657 28.6619 6.21869C26.068 2.8608 22.2465 0.670781 18.0379 0.13033Z\" fill=\"#E26856\"></path>\r\n"
	            			+ "                        <path d=\"M11.5674 17.0111C11.5736 17.0238 11.5841 17.0339 11.5969 17.0397C11.6097 17.0456 11.6242 17.0468 11.6378 17.0431L14.1978 16.4911L13.121 15.3999L11.5866 16.9343C11.5745 16.9425 11.5659 16.9548 11.5623 16.969C11.5588 16.9832 11.5606 16.9982 11.5674 17.0111Z\" fill=\"white\"></path>\r\n"
	            			+ "                        <path d=\"M23.5548 6.0193C23.198 6.01966 22.8459 6.10004 22.5243 6.25451C22.2027 6.40898 21.9199 6.63361 21.6966 6.91187C21.4733 7.19012 21.3153 7.51491 21.2342 7.86232C21.153 8.20973 21.1508 8.57091 21.2277 8.91928C21.3047 9.26765 21.4588 9.59433 21.6786 9.87527C21.8985 10.1562 22.1786 10.3842 22.4983 10.5426C22.818 10.701 23.1691 10.7856 23.5259 10.7903C23.8826 10.795 24.2358 10.7196 24.5596 10.5697L22.9372 8.9473C22.9186 8.92872 22.9038 8.90665 22.8937 8.88235C22.8836 8.85805 22.8784 8.83201 22.8784 8.8057C22.8784 8.77939 22.8836 8.75334 22.8937 8.72904C22.9038 8.70475 22.9186 8.68268 22.9372 8.6641L25.0572 6.5457C24.6314 6.20338 24.1011 6.01756 23.5548 6.0193Z\" fill=\"white\"></path>\r\n"
	            			+ "                        <path d=\"M25.3483 6.8208L23.3611 8.8L24.9195 10.3584C25.0343 10.2778 25.1414 10.1867 25.2395 10.0864C25.6666 9.6564 25.9152 9.08072 25.9354 8.47497C25.9556 7.86923 25.7458 7.27828 25.3483 6.8208Z\" fill=\"white\"></path>\r\n"
	            			+ "                        <path d=\"M21.3722 10.4737H21.337C21.2957 10.4733 21.2544 10.4771 21.2138 10.4849H21.1994C21.1548 10.4945 21.111 10.5073 21.0682 10.5233L21.0346 10.5393C21.0024 10.5527 20.9714 10.5687 20.9418 10.5873L20.9066 10.6097C20.8679 10.6364 20.8315 10.6664 20.7978 10.6993L14.905 16.5936L15.6346 17.3232L21.8746 11.8465C21.9099 11.8155 21.942 11.7812 21.9706 11.7441L21.9978 11.7089C22.0191 11.6776 22.0384 11.645 22.0554 11.6113C22.065 11.5921 22.073 11.5729 22.081 11.5537C22.0918 11.5281 22.1009 11.5019 22.1082 11.4753C22.1082 11.4561 22.1194 11.4369 22.1242 11.4177C22.1321 11.378 22.1375 11.338 22.1402 11.2977V11.2449C22.1402 11.2161 22.1402 11.1873 22.1402 11.1585C22.1402 11.1297 22.1402 11.1201 22.1322 11.1009C22.1026 10.9499 22.0289 10.811 21.9204 10.7019C21.8119 10.5929 21.6735 10.5184 21.5226 10.4881H21.4922C21.4526 10.4804 21.4125 10.4756 21.3722 10.4737Z\" fill=\"white\"></path>\r\n"
	            			+ "                        <path d=\"M13.3962 15.1168L14.6058 16.32L20.5146 10.4112C20.7069 10.2232 20.9575 10.1064 21.225 10.08C20.1802 9.28 19.041 9.4896 13.3962 15.1168Z\" fill=\"white\"></path>\r\n"
	            			+ "                        <path d=\"M22.2075 12.0769L22.1355 12.1473L15.8955 17.6225L16.9563 18.6817C19.5867 16.1937 21.9211 13.8241 22.2075 12.0769Z\" fill=\"white\"></path>\r\n"
	            			+ "                        <path d=\"M6.64277 24.9039C6.64601 24.9153 6.65267 24.9253 6.66182 24.9328C6.67098 24.9402 6.6822 24.9447 6.69397 24.9455L9.41396 25.1327L7.88917 23.6079L6.65557 24.8399C6.64757 24.8483 6.64212 24.8587 6.63985 24.87C6.63759 24.8814 6.6386 24.8931 6.64277 24.9039Z\" fill=\"white\"></path>\r\n"
	            			+ "                        <path d=\"M8.17383 23.3247L9.78182 24.9327C9.80091 24.9531 9.82645 24.9663 9.85413 24.9701C9.88181 24.9739 9.90996 24.968 9.93382 24.9535C9.95875 24.9411 9.97861 24.9205 9.98999 24.8951C10.0014 24.8697 10.0036 24.8411 9.99622 24.8143L9.72582 23.6591C9.70831 23.5842 9.71627 23.5056 9.74844 23.4358C9.7806 23.366 9.83514 23.3089 9.90342 23.2735C12.7226 21.8607 14.9962 20.4063 16.665 18.9535L15.545 17.8335L13.145 18.3503L8.17383 23.3247Z\" fill=\"white\"></path>\r\n"
	            			+ "                        <path d=\"M15.2012 17.4944L14.5996 16.8928L13.7676 17.7232C13.7616 17.7304 13.7583 17.7395 13.7583 17.7488C13.7583 17.7582 13.7616 17.7672 13.7676 17.7744C13.7714 17.7829 13.7783 17.7898 13.7868 17.7936C13.7954 17.7974 13.805 17.798 13.814 17.7952L15.2012 17.4944Z\" fill=\"white\"></path>\r\n"
	            			+ "                        <path d=\"M25.4043 8.11051C25.3961 8.08533 25.3826 8.0622 25.3647 8.04267C25.3469 8.02314 25.325 8.00766 25.3006 7.99728C25.2763 7.9869 25.25 7.98185 25.2235 7.98247C25.197 7.9831 25.171 7.98938 25.1471 8.0009C25.1233 8.01242 25.1022 8.02891 25.0852 8.04926C25.0683 8.06962 25.0559 8.09336 25.0489 8.1189C25.0419 8.14444 25.0404 8.17118 25.0446 8.19733C25.0488 8.22348 25.0585 8.24844 25.0731 8.27051C25.1209 8.36674 25.1399 8.47474 25.1277 8.58148C25.1155 8.68823 25.0726 8.78917 25.0043 8.87211C24.9819 8.89921 24.9676 8.93213 24.9632 8.96703C24.9587 9.00193 24.9643 9.03738 24.9792 9.06924C24.9941 9.10111 25.0178 9.12807 25.0475 9.14699C25.0771 9.16591 25.1116 9.17601 25.1467 9.17611C25.1739 9.17582 25.2007 9.16967 25.2253 9.15807C25.2499 9.14647 25.2716 9.1297 25.2891 9.10891C25.4024 8.97122 25.4735 8.80379 25.4939 8.62669C25.5143 8.4496 25.4832 8.27037 25.4043 8.11051Z\" fill=\"#E26856\"></path>\r\n"
	            			+ "                    </svg>\r\n"
	            			+ "                    <p style=\"\r\n"
	            			+ "                            color: #fff !important;\r\n"
	            			+ "                            height: 30px;\r\n"
	            			+ "                            background-color: #0973b9 !important;\r\n"
	            			+ "                            font-size: 12px;\r\n"
	            			+ "                            font-family: Inter, system-ui, -apple-system, BlinkMacSystemFont, Segoe UI, Roboto, Oxygen, Ubuntu, Cantarell, Fira Sans, Droid Sans, Helvetica, Arial, sans-serif;\r\n"
	            			+ "                            align-items: center;\r\n"
	            			+ "                            line-height: 30px;\r\n"
	            			+ "                            padding-left: 30px;\r\n"
	            			+ "                            margin-top: -29px;\r\n"
	            			+ "                        \">\r\n"
	            			+                         methodName+"\r\n"
	            			+ "                    </p>\r\n"
	            			+ "                </div>\r\n"
	            			+ "                        <div class=\"postman_nav\" style=\"height: 25px;width: 248.5%!important;background-color: #fff !important;border-left: 1px solid rgb(230, 230, 230) !important;\"></div>\r\n"
	            			+ "            <table class=\"postman_nav\" style=\"width: 248.5% !important;background-color: #fff !important;margin-top: 0px !important;cursor: pointer;padding-top: 0px;line-height: 0px;white-space: nowrap;overflow: hidden;text-overflow: ellipsis !important;height: 41px;\">\r\n"
	            			+ "            <tbody><tr><td class=\"Post_cleft\" style=\"\r\n"
	            			+ "                                    width: 0px !important;\r\n"
	            			+ "                                    border-top: 1px solid rgb(255, 108, 55);\r\n"
	            			+ "                                    border-left: 1px solid rgb(230, 230, 230) !important;\r\n"
	            			+ "                                    border-bottom: none !important;\r\n"
	            			+ "                                    font-size: 12px;\r\n"
	            			+ "                                    font-family: 'Roboto';\r\n"
	            			+ "                                    cursor: pointer;\r\n"
	            			+ "                                    padding-top: 0px;\r\n"
	            			+ "                                    line-height: 0px;\r\n"
	            			+ "                                    white-space: nowrap;\r\n"
	            			+ "                                    overflow: hidden;\r\n"
	            			+ "                                    text-overflow: ellipsis !important;\r\n"
	            			+ "                                    font-style: italic;\r\n"
	            			+ "                                    height: 41px;\r\n"
	            			+ "                                    flex-direction: row;\r\n"
	            			+ "                                    min-width: 0;\r\n"
	            			+ "                                    overflow-x: scroll;\r\n"
	            			+ "                                    scrollbar-width: none;\r\n"
	            			+ "                                    align-items: center;\r\n"
	            			+ "                                    /* width: 800px !important; */\r\n"
	            			+ "                                \">\r\n"
	            			+ "                                \r\n"
	            			+ "                                \r\n"
	            			+ "                    <span class=\"post_method\" style=\"color: #B3205C;font-size: 10px;font-family: Inter, system-ui, -apple-system, BlinkMacSystemFont, Segoe UI, Roboto, Oxygen, Ubuntu, Cantarell, Fira Sans, Droid Sans, Helvetica, Arial, sans-serif;align-items: center;font-weight: bold;\">\r\n"
	            			+ methodName+"\r\n"
	            			+ "                    </span>\r\n"
	            			+ "                     <span class=\"post_url\" style=\"\r\n"
	            			+ "                        \r\n"
	            			+ "                         /* Set the width you want */\r\n"
	            			+ "                        text-overflow: ellipsis !important;\r\n"
	            			+ "                        overflow: hidden;\r\n"
	            			+ "                        white-space: nowrap;\r\n"
	            			+ "                        width: 100%;\r\n"
	            			+ "                        max-width: 200px; /* Adjust the width as needed */\r\n"
	            			+ "                        font-style: italic;\r\n"
	            			+ "                        height: 41px;\r\n"
	            			+ "                        \">\r\n"
	            			+ "                        &nbsp;&nbsp;"+apiUrl+"\r\n"
	            			+ "                    </span>\r\n"
	            			+ "                \r\n"
	            			+ "                                \r\n"
	            			+ "                            </td>\r\n"
	            			+ "                            <td class=\"Post_cright\" style=\"border-bottom: 1px solid rgb(230, 230, 230) !important;width: 69% !important;border-left: 1px solid rgb(230, 230, 230) !important;\">\r\n"
	            			+ "                                <span class=\"pluss\" style=\"font-size: 20px; cursor: pointer;\">&nbsp;＋ ◦◦◦</span>\r\n"
	            			+ "                            </td>\r\n"
	            			+ "                        </tr>\r\n"
	            			+ "                    </tbody>\r\n"
	            			+ "                </table>\r\n"
	            			+ "                        <div class=\"postman_urllbl\" style=\"\r\n"
	            			+ "                                width: 246.5% !important;\r\n"
	            			+ "                                background-color: #fff !important;\r\n"
	            			+ "                                border-left: 1.5px solid rgb(230, 230, 230) !important;\r\n"
	            			+ "                                font-family: 'Roboto';\r\n"
	            			+ "                                font-size: 12px;\r\n"
	            			+ "                                font-weight: 600;\r\n"
	            			+ "                                border-right: 1px solid rgb(230, 230, 230) !important;\r\n"
	            			+ "                                padding-left: 9px;\r\n"
	            			+ "                                padding-bottom: 11px;\r\n"
	            			+ "                                padding-top: 10px;\r\n"
	            			+ "                                border-bottom: 1.5px solid rgb(230, 230, 230) !important;\r\n"
	            			+ "                                overflow: hidden !important;\r\n"
	            			+ "                                text-overflow: ellipsis !important;\r\n"
	            			+ "                                white-space: nowrap;\r\n"
	            			+ "                                /* margin-left: 6px; */\r\n"
	            			+ "                            \">"+apiUrl+"</div>\r\n"
	            			+ "                        <div class=\"postman_space\" style=\"height: 9px;width: 248.5% !important;background-color: #fff !important;border-left: 1.5px solid rgb(230, 230, 230) !important;\"></div>\r\n"
	            			+ "                        <div style=\"width: 248.5% !important;display: table;background-color: #fff !important;height: 55px;\">\r\n"
	            			+ "                    <div style=\"display: table-row;background-color: #fff !important;/* height: 75px; */\">\r\n"
	            			+ "                        <div class=\"post_man_method\" style=\"\r\n"
	            			+ "                                width: 75px !important;\r\n"
	            			+ "                                background-color: rgb(237, 237, 237) !important;\r\n"
	            			+ "                                border: 1px solid #e6e6e6;\r\n"
	            			+ "                                color: #B3205C !important;\r\n"
	            			+ "                                font-family: Roboto;\r\n"
	            			+ "                                margin: auto;\r\n"
	            			+ "                                padding-right: 10px;\r\n"
	            			+ "                                font-weight: 600;\r\n"
	            			+ "                                white-space: nowrap;\r\n"
	            			+ "                                padding-bottom: 11px;\r\n"
	            			+ "                                padding-top: 10px;\r\n"
	            			+ "                                margin-left: 1%;\r\n"
	            			+ "                                padding-left: 4px;\r\n"
	            			+ "                                cursor: text;\r\n"
	            			+ "                                box-sizing: border-box;\r\n"
	            			+ "                                border-left-width: 1px;\r\n"
	            			+ "                                border-bottom-width: 1px;\r\n"
	            			+ "                                border-right-width: 0;\r\n"
	            			+ "                                border-top-width: 1px;\r\n"
	            			+ "                                flex: 0 0 30px;\r\n"
	            			+ "                                height: 39px;\r\n"
	            			+ "                                font-size: 12px;\r\n"
	            			+ "                                border-radius: 5px;\r\n"
	            			+ "                            \">\r\n"
	            			+                             methodName+"&nbsp;&nbsp;\r\n"
	            			+ "                                   <div style=\"padding-right: 8px !important;margin-top: 0px;margin-left: -10px;background-color: transparent;width: 0px;\" class=\"btn dropdown-button\" tabindex=\"0\" data-testid=\"base-button\" role=\"button\" aria-label=\"base-button\"><i color=\"aether-icon-default-color\" class=\"IconWrapper__IconContainer-gnjn48-0 eTmUbn dropdown-caret\" title=\"\" data-testid=\"aether-icon\"><svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\"><path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M8.00004 9.29294L4.35359 5.64649L3.64648 6.3536L8.00004 10.7072L12.3536 6.3536L11.6465 5.64649L8.00004 9.29294Z\" fill=\"#6B6B6B\"></path></svg></i></div>\r\n"
	            			+ "                        </div>\r\n"
	            			+ "            <div class=\"post_man_url\" style=\"\r\n"
	            			+ "                                flex: 1;\r\n"
	            			+ "                                min-width: 0;\r\n"
	            			+ "                                width: 70% !important;\r\n"
	            			+ "                                background-color: #ededed !important;\r\n"
	            			+ "                                color: rgb(33, 33, 33) !important;\r\n"
	            			+ "                                font-family: Roboto;\r\n"
	            			+ "                                font-size: 11px;\r\n"
	            			+ "                                white-space: nowrap;\r\n"
	            			+ "                                cursor: text !important;\r\n"
	            			+ "                                padding-left: 10px;\r\n"
	            			+ "                                padding-right: 3px;\r\n"
	            			+ "                                overflow: hidden !important;\r\n"
	            			+ "                                text-overflow: ellipsis !important;\r\n"
	            			+ "                                border: 1px solid #e6e6e6;\r\n"
	            			+ "                                height: 27px;\r\n"
	            			+ "                                margin-top: -40px;\r\n"
	            			+ "                                margin-left: 84px !important;\r\n"
	            			+ "                                padding-top: 10px !important;\r\n"
	            			+ "                                border-radius: 5px;\r\n"
	            			+ "                                \">                "+apiUrl+"\r\n"
	            			+ "                        </div>\r\n"
	            			+ "                        <div style=\"display: table-cell; background-color: #fff !important;\">                <button type=\"button\" class=\"down_meth_Send\" style=\"\r\n"
	            			+ "                                    color: #fff !important;\r\n"
	            			+ "                                    background-color: rgb(9, 123, 237) !important;\r\n"
	            			+ "                                    width: 88px !important;\r\n"
	            			+ "                                    font-size: 12px;\r\n"
	            			+ "                                    font-weight: 600;\r\n"
	            			+ "                                    font-family: Roboto !important;\r\n"
	            			+ "                                    border: none;\r\n"
	            			+ "                                    border-radius: 5px;\r\n"
	            			+ "                                    margin-left: -44%;\r\n"
	            			+ "                                    height: 40px;\r\n"
	            			+ "                                    cursor: pointer;\r\n"
	            			+ "                                    word-spacing: 10px;\r\n"
	            			+ "                                    text-align: center;\r\n"
	            			+ "                                    box-sizing: border-box;\r\n"
	            			+ "                                    border-left: 1px solid #e6e6e6;\r\n"
	            			+ "                                \">\r\n"
	            			+ "                                Send\r\n"
	            			+ "                                <span style=\"border-left: 1px solid #e6e6e6;\">\r\n"
	            			+ "                                    <svg class=\"arrow-icon\" xmlns=\"http://www.w3.org/2000/svg\" width=\"12\" height=\"12\" viewBox=\"0 0 12 12\" fill=\"#fff;\" style=\"margin-bottom: -2px;fill: #fff !important;margin-left: 8px;border-right: 1px #fff;\">\r\n"
	            			+ "                                        <g>\r\n"
	            			+ "                                            <path d=\"M10.375,3.219,6,6.719l-4.375-3.5A1,1,0,1,0,.375,4.781l5,4a1,1,0,0,0,1.25,0l5-4a1,1,0,0,0-1.25-1.562Z\"></path>\r\n"
	            			+ "                                        </g>\r\n"
	            			+ "                                    </svg>\r\n"
	            			+ "                                </span>\r\n"
	            			+ "                            </button>\r\n"
	            			+ "                        </div>\r\n"
	            			+ "                    </div>\r\n"
	            			+ "                </div>\r\n"
	            			+ "                        <div class=\"collapse navbar-collapse\" id=\"navbarSupportedContentBottom\" style=\"background-color: #fff !important;margin-top: -13px;/* margin-left: -37px; */width: 248.5%;border-bottom: 1px solid #e6e6e6;\">\r\n"
	            			+ "                            <ul id=\"menu\" style=\"display: flex;white-space: nowrap;background-color: #fff !important;cursor: pointer;padding-left: 0px;\">\r\n"
	            			+ "                                <li style=\"display: inline; float: left; padding: 10px; text-decoration: none; background-color: #fff;  font-family: Roboto; color: #6b6b6b;\">Params</li>\r\n"
	            			+ "                                <li style=\"display: inline; float: left; padding: 10px; text-decoration: none;  font-family: Roboto; background-color: #fff; color: #6b6b6b;\">Authorization</li>\r\n"
	            			+ "                                <li style=\"display: inline-block; float: left; padding: 10px; text-decoration: none;  font-family: Roboto; background-color: #fff; color: #6b6b6b;\">\r\n"
	            			+ "                                    Headers <span class=\"post_dot\" style=\"color: #007f31; font-family: Roboto;\">&nbsp;"+expectedResponseHeaderCount+"</span>\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline-block; float: left; padding: 10px; text-decoration: none;  font-family: Roboto; border-bottom: 2px solid #E26856; background-color: #fff; color: #212121; padding-top: 5px !important;\">\r\n"
	            			+ "                                    Body <span class=\"post_dot\" style=\"color: #0cbb52; font-size: 18px; margin-top: 26px !important;\">&nbsp;●</span>\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline-block; float: left; padding: 10px; text-decoration: none;  font-family: Roboto; background-color: #fff; color: #6b6b6b;\">Pre-request Scripts</li>\r\n"
	            			+ "                                <li style=\"display: inline-block; float: left; padding: 10px; text-decoration: none;  font-family: Roboto; background-color: #fff; color: #6b6b6b;\">Tests</li>\r\n"
	            			+ "                                <li style=\"display: inline-block; float: left; padding: 10px; text-decoration: none;  font-family: Roboto; background-color: #fff; color: #6b6b6b;\">Settings</li>\r\n"
	            			+ "                                <li style=\"display: inline-block; float: right !important; padding: 10px; text-decoration: none;  font-family: Roboto; background-color: #fff; color: #0265d2; padding-left: 33.6px !important; font-weight: 500;\">\r\n"
	            			+ "                                    Cookies\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                            </ul>\r\n"
	            			+ "                        </div>\r\n"
	            			+ "                        <div class=\"collapse data-collapse\" id=\"rdonavbarSupportedContentBottom\" style=\"background-color: #fff !important;display: inline-block;width: 248.5% !important;border-bottom: 1px solid #e6e6e6;margin-top: 0px;padding-top: 8px;\">\r\n"
	            			+ "                            <ul id=\"data\" style=\"white-space: nowrap;background-color: #fff !important;cursor: pointer;line-height: 1;display: flex;padding-left: 0px;\">\r\n"
	            			+ "                                <li style=\"display: inline; float: left; padding: 5px; text-decoration: none; background-color: #fff;  font-family: Roboto; color: #212121;\">\r\n"
	            			+ "                                    <span class=\"data_dot\" style=\"color: #bfbfbf; font-size: 30px; vertical-align: sub;\">●&nbsp;</span>none\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline; float: left; padding: 5px; text-decoration: none; background-color: #fff;  font-family: Roboto; color: #212121;\">\r\n"
	            			+ "                                    <span class=\"data_dot\" style=\"color: #bfbfbf; font-size: 30px; vertical-align: sub;\">●&nbsp;</span>form-data\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline; float: left; padding: 5px; text-decoration: none; background-color: #fff;  font-family: Roboto; color: #212121; vertical-align: middle;\">\r\n"
	            			+ "                                    <span class=\"data_dot\" style=\"color: #bfbfbf; font-size: 30px; vertical-align: sub;\">●&nbsp;</span>x-www-form-unlencoded\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline;float: left;padding: 5px;text-decoration: none;background-color: #fff;font-family: Roboto;color: #212121;vertical-align: middle !important;margin-top: 7px !important;\">\r\n"
	            			+ "                            <div class=\"radio-item\"><input type=\"radio\" checked=\"checked\" id=\"ritema\" name=\"ritem\" value=\"ropt1\"> <label for=\"ritema\">raw</label></div><div class=\"post_dot\" style=\"color: #E26856;font-size: 22px !important;margin-top: -25px;padding: 0 !important;margin-left: 0px;margin-right: 0px;\">&nbsp;●</div>\r\n"
	            			+ "                        </li>\r\n"
	            			+ "                                <li style=\"display: inline; float: left; padding: 5px; text-decoration: none; background-color: #fff;  font-family: Roboto; color: #212121;\">\r\n"
	            			+ "                                    <span class=\"data_dot\" style=\"color: #bfbfbf; font-size: 30px; vertical-align: sub;\">●&nbsp;</span>binary\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline; float: left; padding: 5px; text-decoration: none; background-color: #fff;  font-family: Roboto; color: #212121;\">\r\n"
	            			+ "                                    <span class=\"data_dot\" style=\"color: #bfbfbf; font-size: 30px; vertical-align: sub;\">●&nbsp;</span>GraphQL\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline; padding: 11px; text-decoration: none; background-color: #fff;  font-family: Roboto; color: rgb(2, 101, 210); float: none; font-weight: 600;margin-top: 4px;\">\r\n"
	            			+ "                                    JSON&nbsp;&nbsp;\r\n"
	            			+ "                                    <span class=\"Jsonbody\" style=\"vertical-align: sub;\">\r\n"
	            			+ "                                        <svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\r\n"
	            			+ "                                            <path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M8.00004 9.29294L4.35359 5.64649L3.64648 6.3536L8.00004 10.7072L12.3536 6.3536L11.6465 5.64649L8.00004 9.29294Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                        </svg>\r\n"
	            			+ "                                    </span>\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                            </ul>\r\n"
	            			+ "                        </div>\r\n"
	            			+ "                        <div class=\"collapse jsonbody-collapse\" id=\"rdonavbarSupportedContentjsonbody\" style=\"background-color: #fff !important;height: 152px;width: 248.5% !important;border-bottom: 1px solid #e6e6e6;\">\r\n"
	            			+ "                            <textarea id=\"w3review\" name=\"w3review\" rows=\"4\" cols=\"50\" style=\"resize: none;background-color: #fff;border: 1px solid #e6e6e6;border-radius: 5px;word-break: break-word;margin: 11px;width: 97%;height: 88%;white-space: break-spaces !important;padding-left: 5px;display: inline-block;overflow: scroll;padding-top: 9px;\">"+script+"</textarea>\r\n"
	            			+ "                        </div>\r\n"
	            			+ "                    \r\n"
	            			+ "                        <div class=\"collapse data-collapse\" style=\"background-color: #fff !important;display: inline-block;width: 248.5%!important;border-bottom: 1px solid #e6e6e6;margin-top: 0px;height: 58px !important;padding-left: 0px;\">\r\n"
	            			+ "                            <ul id=\"data\" style=\"white-space: nowrap;background-color: #fff !important;cursor: pointer;padding-left: 6px;\">\r\n"
	            			+ "                                <li style=\"\r\n"
	            			+ "                                        display: inline;\r\n"
	            			+ "                                        float: left;\r\n"
	            			+ "                                        padding: 5px;\r\n"
	            			+ "                                        text-decoration: none;\r\n"
	            			+ "                                        background-color: #fff;\r\n"
	            			+ "                                        \r\n"
	            			+ "                                        border-bottom: 2px solid #E26856;\r\n"
	            			+ "                                        font-family: Roboto;\r\n"
	            			+ "                                        color: #212121;\r\n"
	            			+ "                                        padding-bottom: 13px !important;\r\n"
	            			+ "                                        margin-bottom: 5px;\r\n"
	            			+ "                                    \">\r\n"
	            			+ "                                    Body\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline; float: left; padding: 5px; text-decoration: none; background-color: #fff;  font-family: Roboto; color: #6b6b6b;\">Cookies</li>\r\n"
	            			+ "                                <li style=\"display: inline; float: left; padding: 5px; text-decoration: none; background-color: #fff;  font-family: Roboto; color: #6b6b6b;\">\r\n"
	            			+ "                                    Headers<span class=\"response-viewer-tabs-content-count\" style=\"margin-left: 4px; color: #0cbb52;\"> "+receivedHeaderCount+"</span>\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline; float: left; padding: 5px; text-decoration: none; background-color: #fff;  font-family: Roboto; color: #6b6b6b;\">Test Results</li>\r\n"
	            			+ "                                <div style=\"display: flex;float: left;padding: 5px;text-decoration: none;background-color: #fff;margin-left: 95px;\">\r\n"
	            			+ "                                    <span class=\"pm-icon pm-icon-sm pm-icon-normal network-icon secure\" style=\"margin-top: 2px; margin-left: -86px \">\r\n"
	            			+ "                                        <svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\">\r\n"
	            			+ "                                            <defs>\r\n"
	            			+ "                                                <path id=\"network-secure\" d=\"M7 14C7.25621 14 7.61626 13.8497 8 13.4043V14.9291C7.6734 14.9758 7.33952 15 7 15C3.13401 15 0 11.866 0 8C0 4.13401 3.13401 1 7 1C10.1952 1 12.8904 3.14083 13.7295 6.06637C13.493 6.02278 13.2491 6 13 6H12.6586C12.5319 5.64144 12.372 5.29852 12.1827 4.97492C11.6348 5.31335 11.009 5.59672 10.326 5.81425C10.3539 5.98431 10.3787 6.15725 10.4001 6.33279C10.0573 6.48255 9.73957 6.67897 9.45489 6.91402C9.43054 6.62449 9.39671 6.34289 9.3543 6.07052C8.61147 6.22804 7.81942 6.31246 6.99994 6.31246C6.18051 6.31246 5.3885 6.22805 4.64569 6.07054C4.55187 6.67322 4.5 7.32109 4.5 8C4.5 8.67895 4.55187 9.32686 4.64571 9.92958C5.38851 9.77207 6.18051 9.68766 6.99994 9.68766C7.34156 9.68766 7.67841 9.70233 8.00891 9.73085C8.003 9.8198 8 9.90955 8 10V10.734C7.67457 10.7035 7.34064 10.6877 6.99994 10.6877C6.24147 10.6877 5.51656 10.7664 4.84427 10.9097C4.99643 11.5114 5.19259 12.0486 5.41955 12.5025C5.99978 13.663 6.61633 14 7 14ZM4.84424 5.09045C4.9964 4.4887 5.19258 3.95142 5.41955 3.49747C5.99978 2.33702 6.61633 2 7 2C7.38367 2 8.00022 2.33702 8.58045 3.49747C8.80742 3.95141 9.00359 4.48869 9.15576 5.09042C8.48343 5.2337 7.75847 5.31246 6.99994 5.31246C6.24146 5.31246 5.51654 5.23371 4.84424 5.09045ZM11.6064 4.15516C11.1699 4.41889 10.6703 4.64698 10.1212 4.82928C9.87367 3.85659 9.51582 3.01503 9.0806 2.37055C10.0724 2.73725 10.9408 3.35856 11.6064 4.15516ZM3.87879 4.82932C3.32965 4.64702 2.83005 4.41893 2.39356 4.1552C3.05918 3.35859 3.92757 2.73726 4.9194 2.37055C4.48418 3.01504 4.12632 3.85661 3.87879 4.82932ZM1.81725 4.97497C2.36521 5.3134 2.99104 5.59676 3.674 5.81428C3.56108 6.502 3.5 7.23673 3.5 8C3.5 8.76331 3.56109 9.49809 3.67402 10.1858C2.99107 10.4033 2.36525 10.6867 1.81731 11.0251C1.29776 10.1369 1 9.10325 1 8C1 6.89679 1.29774 5.86312 1.81725 4.97497ZM4.9194 13.6294C3.9276 13.2628 3.05923 12.6415 2.39363 11.8449C2.83011 11.5812 3.32969 11.3531 3.87882 11.1708C4.12634 12.1435 4.48419 12.985 4.9194 13.6294Z M12.75 12.4331C12.8995 12.3467 13 12.1851 13 12C13 11.7239 12.7761 11.5 12.5 11.5C12.2239 11.5 12 11.7239 12 12C12 12.1851 12.1005 12.3467 12.25 12.4331V13.5H12.75V12.4331Z M10.5 10H10C9.44772 10 9 10.4477 9 11V14C9 14.5523 9.44771 15 10 15H15C15.5523 15 16 14.5523 16 14V11C16 10.4477 15.5523 10 15 10H14.5L14.5 8.98224C14.5003 8.77423 14.5009 8.28481 14.245 7.84378C13.9502 7.33553 13.3895 7 12.5 7C11.6105 7 11.0499 7.33553 10.755 7.84378C10.4992 8.28481 10.4998 8.77423 10.5 8.98224L10.5 10ZM11.62 8.34556C11.5085 8.53789 11.5 8.78247 11.5 9V10H13.5V9C13.5 8.78247 13.4916 8.53789 13.38 8.34556C13.2999 8.20736 13.1105 8 12.5 8C11.8895 8 11.7002 8.20736 11.62 8.34556ZM15 11H10V14H15V11Z\"></path>\r\n"
	            			+ "                                            </defs>\r\n"
	            			+ "                                            <use fill=\"#6B6B6B\" fill-rule=\"evenodd\" href=\"#network-secure\"></use>\r\n"
	            			+ "                                        </svg>\r\n"
	            			+ "                                    </span>\r\n"
	            			+ "                                </div>\r\n"
	            			+ "                                <li style=\"display: flex;float: left;padding: 5px;text-decoration: none;font-size: 11px;font-family: Roboto;color: rgb(0, 127, 49);margin-top: 2px;margin-left: -72px;\">"+statusCode+"</li>\r\n"
	            			+ "                                <li style=\"display: inline;float: left;padding: 5px;text-decoration: none;font-size: 11px;font-family: Roboto;color: rgb(0, 127, 49);margin-top: 2px;margin-left: -50px;\">"+repStatusLine+"</li>\r\n"
	            			+ "                                <li style=\"display: inline; float: left; padding: 5px; text-decoration: none; font-size: 11px; font-family: Roboto; color: rgb(0, 127, 49);margin-left: 7px;margin-top: 3px;\">"+responseTime+" ms</li>\r\n"
	            			+ "                                <li style=\"\r\n"
	            			+ "                                        display: inline;\r\n"
	            			+ "                                        float: left;\r\n"
	            			+ "                                        padding: 5px;\r\n"
	            			+ "                                        text-decoration: none;\r\n"
	            			+ "                                        background-color: #fff;\r\n"
	            			+ "                                        \r\n"
	            			+ "                                        font-family: Roboto;\r\n"
	            			+ "                                        color: rgb(0, 127, 49);\r\n"
	            			+ "                                        padding-left: 5px;\r\n"
	            			+ "                                        text-align: justify;\r\n"
	            			+ "                                        justify-content: right;    margin-left: -1px;    margin-top: 3px;\r\n"
	            			+ "                                    \">\r\n"
	            			+ "                    1290 B\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"\r\n"
	            			+ "                                        display: inline;\r\n"
	            			+ "                                        float: left;\r\n"
	            			+ "                                        padding: 5px;\r\n"
	            			+ "                                        text-decoration: none;\r\n"
	            			+ "                                        background-color: rgba(0, 0, 0, 0);\r\n"
	            			+ "                                        \r\n"
	            			+ "                                        font-family: Roboto;\r\n"
	            			+ "                                        border-bottom-left-radius: 4px;\r\n"
	            			+ "                                        border-bottom-right-radius: 4px;\r\n"
	            			+ "                                        border-top-left-radius: 4px;\r\n"
	            			+ "                                        border-top-right-radius: 4px;\r\n"
	            			+ "                                        box-sizing: border-box;\r\n"
	            			+ "                                        color:#0265D2;\r\n"
	            			+ "                                        border-left: 1px solid #ededed;\r\n"
	            			+ "                                        cursor: pointer;\r\n"
	            			+ "                                        margin-top: -37px; margin-left: 548px;  font-weight: 600\">\r\n"
	            			+ "                                    Save Response\r\n"
	            			+ "                                    <i class=\"IconWrapper__IconContainer-r96cto-0 gJkKrF dropdown-caret\" title=\"\" style=\"margin: 4px; vertical-align: middle;\">\r\n"
	            			+ "                                        <svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\r\n"
	            			+ "                                            <path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M8.00004 9.29294L4.35359 5.64649L3.64648 6.3536L8.00004 10.7072L12.3536 6.3536L11.6465 5.64649L8.00004 9.29294Z\" fill=\"#0265D2\"></path>\r\n"
	            			+ "                                        </svg>\r\n"
	            			+ "                                    </i>\r\n"
	            			+ "                             </li>\r\n"
	            			+ "                            </ul>\r\n"
	            			+ "                        </div>\r\n"
	            			+ "                        <div class=\"tabdata-collapse\" style=\"background-color: #fff !important;width: 248.5% !important;margin-top: -13px;padding-left: 0px;\">\r\n"
	            			+ "                            <ul id=\"data\" style=\"white-space: nowrap;background-color: #fff !important;cursor: pointer;line-height: 1;display: flex;padding-left: 0px;padding-top: 9px;\">\r\n"
	            			+ "                                <div style=\"background-color: #e6e6e6;float: left;overflow: hidden;padding: 14px 13px;border-top-left-radius: 4px;border-bottom-left-radius: 4px;margin-left: 3px;/* padding-top: 9px; */\">\r\n"
	            			+ "                                    <li style=\"display: inline; border-top-left-radius: 4px; border-bottom-left-radius: 4px; color:#212121;small;font-family: Roboto;\">Pretty</li>\r\n"
	            			+ "                                </div>\r\n"
	            			+ "                                <div style=\"background-color: #f2f2f2; float: left; overflow: hidden; padding: 14px 12px;\">\r\n"
	            			+ "                                    <li style=\"display: inline;color:#6b6b6b;small;font-family: Roboto;\">Raw</li>\r\n"
	            			+ "                                </div>\r\n"
	            			+ "                                <div style=\"background-color: #f2f2f2; float: left; overflow: hidden; padding: 14px 12px;\">\r\n"
	            			+ "                                    <li style=\"display: inline;color:#6b6b6b;small;font-family: Roboto;\">Preview</li>\r\n"
	            			+ "                                </div>\r\n"
	            			+ "                                <div style=\"background-color: #f2f2f2; float: left; overflow: hidden; padding: 14px 12px; border-top-right-radius: 4px; border-bottom-right-radius: 4px;\">\r\n"
	            			+ "                                    <li style=\"display: inline;overflow: hidden;color:#6b6b6b;small;font-family: Roboto;\">Visualize</li>\r\n"
	            			+ "                                </div>\r\n"
	            			+ "                                <li style=\"display: inline;float: left;padding: 5px;overflow: hidden;border-radius:4px;background-color: #fff;color:#6b6b6b;small;font-family: Roboto;\"></li>\r\n"
	            			+ "                                <div style=\"background-color: #f2f2f2;float: left;overflow: hidden;padding: 6px 13px;border-radius: 4px;margin-left: 3px;padding-top: 13px;height: 28px;\">\r\n"
	            			+ "                                    <li style=\"display: inline;color: #6b6b6b;font-family: Roboto;margin-left: 3px;\">\r\n"
	            			+ "                                        JSON\r\n"
	            			+ "                                        <span class=\"jdown\" style=\"margin: 4px; vertical-align: sub;\">\r\n"
	            			+ "                                            <svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\r\n"
	            			+ "                                                <path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M8.00004 9.29294L4.35359 5.64649L3.64648 6.3536L8.00004 10.7072L12.3536 6.3536L11.6465 5.64649L8.00004 9.29294Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            </svg>\r\n"
	            			+ "                                        </span>\r\n"
	            			+ "                                    </li>\r\n"
	            			+ "                                </div>\r\n"
	            			+ "                                <div style=\"background-color: #f2f2f2; float: left; overflow: hidden; padding: 6px 13px; border-radius: 4px; margin-left: 7px;\">\r\n"
	            			+ "                                    <li style=\"display: inline; color: #6b6b6b; font-family: Roboto;\">\r\n"
	            			+ "                                        <span class=\"stylejdown\" style=\"margin: 4px; vertical-align: middle;\">\r\n"
	            			+ "                                            <svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\r\n"
	            			+ "                                                <path d=\"M15 3H1V2H15V3Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                                <path d=\"M12 8H1V7H12C13.6569 7 15 8.34315 15 10C15 11.6569 13.6569 13 12 13H9.70712L11.3536 14.6464L10.6465 15.3536L7.79291 12.5L10.6465 9.64645L11.3536 10.3536L9.70712 12H12C13.1046 12 14 11.1046 14 10C14 8.89543 13.1046 8 12 8Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                                <path d=\"M1 13H6V12H1V13Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            </svg>\r\n"
	            			+ "                                        </span>\r\n"
	            			+ "                                    </li>\r\n"
	            			+ "                                </div>\r\n"
	            			+ "                                <li style=\"display: inline; float: right; padding: 5px; overflow: hidden; border-radius: 4px; background-color: #fff; color: #fff; margin-left: 157px !important;\">\r\n"
	            			+ "                                    <span class=\"serchdata\" style=\"margin: 4px; vertical-align: middle;\">\r\n"
	            			+ "                                        <svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\r\n"
	            			+ "                                            <path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M6.409.534a6.513 6.513 0 0 0-5.84 5.573c-.065.425-.065 1.361 0 1.786.443 2.915 2.623 5.095 5.538 5.538.425.065 1.361.065 1.786 0a6.736 6.736 0 0 0 1.963-.606c.383-.187.949-.545 1.2-.759a.783.783 0 0 1 .162-.119c.012 0 .787.765 1.722 1.7l1.7 1.699.353-.353.353-.353-1.699-1.7c-.935-.935-1.7-1.71-1.7-1.722 0-.013.053-.085.119-.162.214-.251.572-.817.759-1.2.3-.615.499-1.261.606-1.963.065-.425.065-1.361 0-1.786C12.99 3.205 10.798 1.004 7.92.574a9.094 9.094 0 0 0-1.511-.04m1.457 1.025c1.783.304 3.257 1.385 4.053 2.974.264.525.418 1.001.525 1.615.066.373.066 1.331 0 1.704-.209 1.199-.728 2.217-1.552 3.04-.823.824-1.841 1.343-3.04 1.552-.373.066-1.331.066-1.704 0-1.201-.209-2.216-.727-3.04-1.552-.825-.824-1.343-1.839-1.552-3.04-.066-.373-.066-1.331 0-1.704.209-1.201.727-2.216 1.552-3.04.841-.842 1.995-1.418 3.119-1.558.132-.017.27-.035.306-.042.152-.026 1.094.01 1.333.051\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                        </svg>\r\n"
	            			+ "                                    </span>\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                    \r\n"
	            			+ "                                <li style=\"display: inline; float: right; padding: 5px; overflow: hidden; border-radius: 4px; background-color: #fff; color: #fff; padding-right: 12px;\">\r\n"
	            			+ "                                    <span class=\"clipboardcopy\" style=\"margin: 4px; vertical-align: middle;\">\r\n"
	            			+ "                                        <svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\r\n"
	            			+ "                                            <path d=\"M2 9.5C2 9.77614 2.22386 10 2.5 10H3V11H2.5C1.67157 11 1 10.3284 1 9.5V2.5C1 1.67157 1.67157 1 2.5 1H9.5C10.3284 1 11 1.67157 11 2.5V3H10V2.5C10 2.22386 9.77614 2 9.5 2H2.5C2.22386 2 2 2.22386 2 2.5V9.5Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M6.5 5C5.67157 5 5 5.67157 5 6.5V13.5C5 14.3284 5.67157 15 6.5 15H13.5C14.3284 15 15 14.3284 15 13.5V6.5C15 5.67157 14.3284 5 13.5 5H6.5ZM6 6.5C6 6.22386 6.22386 6 6.5 6H13.5C13.7761 6 14 6.22386 14 6.5V13.5C14 13.7761 13.7761 14 13.5 14H6.5C6.22386 14 6 13.7761 6 13.5V6.5Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                        </svg>\r\n"
	            			+ "                                    </span>\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                            </ul>\r\n"
	            			+ "                        </div>\r\n"
	            			+ "                        <div class=\"collapse jsonbody-collapse\" id=\"rdonavbarSupportedContentjsonresponse\" style=\"background-color: #fff !important; width: 248.5% !important; margin-top: -13px; height: auto !important;\">\r\n"
	            			+ "                          <textarea id=\"result\" name=\"jresulr\" rows=\"4\" cols=\"50\" style=\"resize: none;background-color: #fff;word-break: break-word;border: none;margin-bottom: 10px;white-space: break-spaces !important;padding-left: 38px;display: inherit;overflow-x: scroll;overflow-y: scroll;height: 180px;margin-top: 7px;padding-top: 25px;padding-left: 9px !important;\">{\r\n"
	            			+                       prettyResponseBody+"</textarea>\r\n"
	            			+ "                        </div>\r\n"
	            			+ "                    \r\n"
	            			+ "                        <div class=\"sub_div\" style=\"width: 251.5% !important; position: sticky; background-color: #fff; height: 26px; padding-top: 10px !important;    margin-top: -10px;\">\r\n"
	            			+ "                            <ul id=\"footerdata\" style=\"white-space: nowrap; background-color: #fff !important; cursor: pointer; border-top: 1px solid #ededed; padding: 5px 0; left: 0; right: 0; bottom: -5px;display: flow-root;\">\r\n"
	            			+ "                                <li style=\"display: inline; float: right; overflow: hidden; background-color: #fff; color: #fff; padding-right: 12px;\">\r\n"
	            			+ "                                    <span class=\"serchdata\" style=\"vertical-align: middle;\">\r\n"
	            			+ "                                        <svg width=\"12\" height=\"12\" viewBox=\"0 0 12 12\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\r\n"
	            			+ "                                            <path d=\"M5.28789 7.125V6.90967C5.28789 6.5874 5.34596 6.31934 5.46211 6.10547C5.57825 5.8916 5.7815 5.67041 6.07186 5.44189C6.41739 5.1665 6.63952 4.95264 6.73824 4.80029C6.83987 4.64795 6.89068 4.46631 6.89068 4.25537C6.89068 4.00928 6.80938 3.82031 6.64678 3.68848C6.48418 3.55664 6.25044 3.49072 5.94556 3.49072C5.66972 3.49072 5.4142 3.53027 5.17901 3.60938C4.94382 3.68848 4.71443 3.78369 4.49085 3.89502L4.125 3.12158C4.71443 2.79053 5.34596 2.625 6.0196 2.625C6.58871 2.625 7.04021 2.76562 7.37413 3.04688C7.70804 3.32812 7.875 3.71631 7.875 4.21143C7.875 4.43115 7.84306 4.62744 7.77918 4.80029C7.7153 4.97021 7.61803 5.13281 7.48737 5.28809C7.35961 5.44336 7.13749 5.64551 6.82099 5.89453C6.55096 6.1084 6.36948 6.28564 6.27657 6.42627C6.18656 6.56689 6.14155 6.75586 6.14155 6.99316V7.125H5.28789Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path d=\"M6.42936 8.625C6.42936 9.03921 6.09657 9.375 5.68604 9.375C5.27552 9.375 4.94272 9.03921 4.94272 8.625C4.94272 8.21079 5.27552 7.875 5.68604 7.875C6.09657 7.875 6.42936 8.21079 6.42936 8.625Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M12 6C12 9.31371 9.31371 12 6 12C2.68629 12 0 9.31371 0 6C0 2.68629 2.68629 0 6 0C9.31371 0 12 2.68629 12 6ZM11 6C11 8.76142 8.76142 11 6 11C3.23858 11 1 8.76142 1 6C1 3.23858 3.23858 1 6 1C8.76142 1 11 3.23858 11 6Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                        </svg>\r\n"
	            			+ "                                    </span>\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline; float: right; overflow: hidden; background-color: #fff; color: #fff; padding-right: 12px;\">\r\n"
	            			+ "                                    <span class=\"serchdata\" style=\"vertical-align: middle;\">\r\n"
	            			+ "                                        <svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\">\r\n"
	            			+ "                                            <defs>\r\n"
	            			+ "                                                <path id=\"two-pane\" d=\"M7.5 1H1v14h6.521a.974.974 0 0 1-.021-.205V1zm1 0v13.795c0 .072-.007.14-.021.205H15V1H8.5zM15 0a1 1 0 0 1 1 1v14a1 1 0 0 1-1 1H1a1 1 0 0 1-1-1V1a1 1 0 0 1 1-1h14zM2.25 10V6h4v4h-4zm9.5 0a2 2 0 1 1 0-4 2 2 0 0 1 0 4z\"></path>\r\n"
	            			+ "                                            </defs>\r\n"
	            			+ "                                            <use fill=\"gray\" fill-rule=\"evenodd\" xlink:href=\"#two-pane\"></use>\r\n"
	            			+ "                                        </svg>\r\n"
	            			+ "                                    </span>\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline; float: right; border-radius: 4px; background-color: #fff; color: #6b6b6b; padding-right: 12px;\">\r\n"
	            			+ "                                    <span class=\"serchdata\" style=\"vertical-align: middle;\">\r\n"
	            			+ "                                        <svg width=\"12\" height=\"12\" viewBox=\"0 0 12 12\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\r\n"
	            			+ "                                            <path d=\"M4 1V0H8V1H11V2H1V1H4Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path d=\"M3 10.5V3H2V10.5C2 11.3284 2.67157 12 3.5 12H8.5C9.32843 12 10 11.3284 10 10.5V3H9V10.5C9 10.7761 8.77614 11 8.5 11H3.5C3.22386 11 3 10.7761 3 10.5Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path d=\"M4.25 10V3H5.25V10H4.25Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path d=\"M6.75 3V10H7.75V3H6.75Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                        </svg>\r\n"
	            			+ "                                    </span>\r\n"
	            			+ "                                    &nbsp;Trash\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline; float: right; overflow: hidden; background-color: #fff; color: #6b6b6b; padding-right: 12px;\">\r\n"
	            			+ "                                    <span class=\"serchdata\" style=\"vertical-align: middle;\">\r\n"
	            			+ "                                        <svg width=\"12\" height=\"12\" viewBox=\"0 0 12 12\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\r\n"
	            			+ "                                            <path d=\"M4.64999 4.35323V7.64676C4.64999 7.80222 4.81959 7.89824 4.95289 7.81825L7.6975 6.17149C7.82696 6.09381 7.82696 5.90618 7.6975 5.8285L4.95289 4.18173C4.81959 4.10175 4.64999 4.19777 4.64999 4.35323Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M2.5 1C1.67157 1 1 1.67157 1 2.5V9.5C1 10.3284 1.67157 11 2.5 11H9.5C10.3284 11 11 10.3284 11 9.5V2.5C11 1.67157 10.3284 1 9.5 1H2.5ZM2 2.5C2 2.22386 2.22386 2 2.5 2H9.5C9.77614 2 10 2.22386 10 2.5V9.5C10 9.77614 9.77614 10 9.5 10H2.5C2.22386 10 2 9.77614 2 9.5V2.5Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                        </svg>\r\n"
	            			+ "                                        &nbsp;Runner\r\n"
	            			+ "                                    </span>\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline; float: right; overflow: hidden; background-color: #fff; color: #6b6b6b; padding-right: 12px;\">\r\n"
	            			+ "                                    <span class=\"serchdata\" style=\"vertical-align: middle;\">\r\n"
	            			+ "                                        <svg width=\"12\" height=\"12\" viewBox=\"0 0 12 12\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\r\n"
	            			+ "                                            <path d=\"M8.64648 3.64645L5.5 6.79291L3.85364 5.14645L3.14648 5.85356L5.5 8.20711L9.35364 4.35356L8.64648 3.64645Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M12 6C12 9.31371 9.31372 12 6 12C2.68628 12 0 9.31371 0 6C0 2.68629 2.68628 0 6 0C9.31372 0 12 2.68629 12 6ZM11 6C11 8.76143 8.76147 11 6 11C3.23853 11 1 8.76143 1 6C1 3.23857 3.23853 1 6 1C8.76147 1 11 3.23857 11 6Z\" fill=\"#007f31\"></path>\r\n"
	            			+ "                                        </svg>\r\n"
	            			+ "                                        &nbsp;Auto-select agent\r\n"
	            			+ "                                    </span>\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline; float: right; overflow: hidden; background-color: #fff; color: #6b6b6b; padding-right: 12px;\">\r\n"
	            			+ "                                    <span class=\"serchdata\" style=\"vertical-align: middle;\">\r\n"
	            			+ "                                        <svg width=\"12\" height=\"12\" viewBox=\"0 0 12 12\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\r\n"
	            			+ "                                            <path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M5.66308 1.58913C5.88209 1.51126 6.12139 1.51211 6.33984 1.59155L11.4407 3.44642C11.9402 3.62806 11.9726 4.31862 11.5 4.54897V6.5H10.5V5.00377L10 5.23105V8.42425C10 8.5697 9.95733 8.72992 9.84933 8.86817C9.57018 9.2255 8.40031 10.5 6 10.5C3.5997 10.5 2.42983 9.22553 2.15067 8.8682C2.04267 8.72995 2 8.56973 2 8.42428V5.26939L0.400766 4.55861C-0.0944949 4.3385 -0.0672027 3.62656 0.443444 3.445L5.66308 1.58913ZM6.41149 6.86219L9 5.68559V8.32647C8.7305 8.63519 7.81585 9.49997 6 9.49997C4.18415 9.49997 3.2695 8.63521 3 8.3265V5.71383L5.59155 6.86563C5.85279 6.98174 6.15124 6.98048 6.41149 6.86219ZM1.72227 4.05163L5.99769 5.95182L10.1785 4.05147L5.99809 2.53134L1.72227 4.05163Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                        </svg>\r\n"
	            			+ "                                        &nbsp;Bootcamp\r\n"
	            			+ "                                    </span>\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                                <li style=\"display: inline; float: right; overflow: hidden; background-color: #fff; color: #6b6b6b; padding-right: 12px;\">\r\n"
	            			+ "                                    <span class=\"serchdata\" style=\"vertical-align: middle;\">\r\n"
	            			+ "                                        <svg width=\"12\" height=\"12\" viewBox=\"0 0 12 12\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\r\n"
	            			+ "                                            <path d=\"M4.04186 2.73689C4.34794 2.73689 4.76912 2.73689 4.76912 3.04531C4.76912 3.35372 4.65454 3.73962 4.34794 3.73962C4.04186 3.73962 3.4297 3.4312 3.4297 3.1228C3.4297 2.81386 3.73578 2.73689 4.04186 2.73689Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path d=\"M6.62093 4.04807C6.46814 3.77814 6.23846 3.58519 5.97058 3.73965C5.7027 3.89413 5.74089 4.58792 5.93187 4.85784C6.08516 5.12776 6.42944 5.0123 6.69732 4.85784C6.9652 4.70338 6.77423 4.31799 6.62093 4.04807Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path d=\"M7.02678 8.74387C7.02678 9.05227 7.63894 9.36069 7.94502 9.36069C8.2511 9.36069 8.36621 8.97532 8.36621 8.6669C8.36621 8.35849 7.94501 8.35849 7.63894 8.35849C7.33287 8.35849 7.02678 8.43546 7.02678 8.74387Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path d=\"M6.56793 6.86281C6.68251 6.59289 7.21828 6.13001 7.48617 6.246C7.75405 6.36145 7.71585 6.67039 7.60075 6.94031C7.48617 7.21023 7.29467 7.59562 7.02678 7.48016C6.72123 7.36417 6.45335 7.13274 6.56793 6.86281Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path d=\"M5.1903 9.8159C5.4969 9.8159 5.61149 9.43 5.61149 9.12159C5.61149 8.81318 5.19083 8.81317 4.88422 8.8137C4.57815 8.8137 4.27206 8.89067 4.27206 9.19908C4.27206 9.50748 4.88422 9.8159 5.1903 9.8159Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path d=\"M5.1903 6.82826C5.1903 6.51985 4.76912 6.51985 4.46304 6.51985C4.15697 6.51985 3.85088 6.59682 3.85088 6.90576C3.85088 7.21416 4.46304 7.52258 4.76912 7.52258C5.07572 7.52258 5.1903 7.13668 5.1903 6.82826Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path d=\"M2.52417 4.89691C2.79205 4.74244 3.48112 4.74244 3.63391 5.01236C3.78721 5.28229 3.55752 5.51372 3.28964 5.62918C3.02175 5.78364 2.63928 5.9376 2.48598 5.66767C2.33321 5.39827 2.25629 5.05137 2.52417 4.89691Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                            <path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M5.97257 0C2.69107 0 0.0026432 2.7049 0 6.01462L4.0413e-05 6.02172C0.0399375 9.29405 2.72939 12 6.01125 12C9.23034 12 11.8783 9.39721 11.9807 6.17416L11.9972 5.65643L11.4817 5.61337C11.2541 5.59435 11.0596 5.55075 10.9006 5.50016C10.7476 5.45143 10.629 5.39675 10.5469 5.35288L10.4588 5.30526L10.3797 5.28915C9.61289 5.13299 9.23701 4.69222 9.07073 4.25653C9.03709 4.16838 9.01243 4.08169 8.99453 3.9992L8.92805 3.69274L8.63009 3.59614C8.49579 3.55261 8.38188 3.49123 8.28588 3.41114C8.07671 3.23666 7.9948 3.00581 7.97439 2.79455C7.96228 2.6691 7.97112 2.54517 7.98939 2.43365L8.02447 2.21957L7.9031 2.03987C7.84009 1.94658 7.7976 1.83899 7.77679 1.72215C7.73627 1.49474 7.77966 1.2551 7.84122 1.04917C7.85431 1.00538 7.86871 0.961596 7.88406 0.918171L8.08034 0.362759L7.51218 0.209654C7.0191 0.0767808 6.49584 0 5.97257 0ZM6.01125 10.9093C3.33582 10.9093 1.12382 8.69452 1.08919 6.01195C1.09327 3.29966 3.2996 1.09066 5.97257 1.09066C6.21459 1.09066 6.45973 1.11128 6.7027 1.15003C6.66767 1.3748 6.65529 1.63729 6.70453 1.91369C6.7379 2.10098 6.79841 2.28641 6.88894 2.46197C6.87746 2.59555 6.87518 2.74334 6.89027 2.89955C6.92979 3.30875 7.09969 3.84115 7.58872 4.24911C7.72023 4.35882 7.86131 4.44789 8.00838 4.5191C8.02207 4.56087 8.03701 4.60317 8.05331 4.64588C8.3369 5.38892 8.97589 6.08944 10.0836 6.34091C10.2115 6.40605 10.3743 6.47707 10.5707 6.53957C10.6588 6.56762 10.7533 6.59381 10.8538 6.61693C10.5412 9.03378 8.47412 10.9093 6.01125 10.9093Z\" fill=\"#6B6B6B\"></path>\r\n"
	            			+ "                                        </svg>\r\n"
	            			+ "                                        &nbsp;Cookies\r\n"
	            			+ "                                    </span>\r\n"
	            			+ "                                </li>\r\n"
	            			+ "                            </ul>\r\n"
	            			+ "                        </div></div>";
	          
	             
	        } catch (Exception e) {
	        	Log.error("Error making API request: ${error.message}");
	            logSummary += "Error making API request: ${error.message}";
	        }

		    return logSummary;
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