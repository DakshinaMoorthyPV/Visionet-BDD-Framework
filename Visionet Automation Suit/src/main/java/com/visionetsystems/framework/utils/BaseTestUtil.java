package com.visionetsystems.framework.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.NetworkMode;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.restassured.RestAssured;

public class BaseTestUtil {

	public synchronized static void initializeExtentReports() {
		String testName = TestReportUtility.testName();
		if (testName != null && !testName.isEmpty()) {
			UIConstantsUtil.ReportFile = File.separator + testName.replaceAll("Log", "ExtentReport") + ".html";
			if (UIConstantsUtil.EXTENT_REPORTS == null) {
				UIConstantsUtil.EXTENT_REPORTS = new ExtentReports(
						TestReportUtility.EXTENT_REPORT_FOLDER + File.separator + UIConstantsUtil.ReportFile, false,
						NetworkMode.OFFLINE);
				UIConstantsUtil.EXTENT_REPORTS.loadConfig(new File("src/main/resources/extent-config.xml"));
			}
		}
	}

	public synchronized static void initializeEnvironment() throws Exception {
		String executionLocation = UIConstantsUtil.APP_CONFIG_MAP.get("ExecutionLocation");
		UIConstantsUtil.TEST_TYPE = UIConstantsUtil.APP_CONFIG_MAP.get("TestType");
		String platform = UIConstantsUtil.APP_CONFIG_MAP.get("Platform");
		String webBrowser = UIConstantsUtil.APP_CONFIG_MAP.get("Web_Browser");
		String mobilePlatformType = UIConstantsUtil.APP_CONFIG_MAP.get("platformType");

		switch (executionLocation) {
		case "Local":
			switch (UIConstantsUtil.TEST_TYPE) {
			case "Web":
				setupLocalWebEnvironment(platform, webBrowser);
				break;
			case "Native":
				setupLocalNativeEnvironment(platform, mobilePlatformType);
				break;
			case "Hybrid":
				setupLocalHybridEnvironment(platform, mobilePlatformType);
				break;
			case "API":
				setupLocalAPIEnvironment();
				break;
			}
			break;

		case "BrowserStack":
			switch (UIConstantsUtil.TEST_TYPE) {
			case "Web":
				setupBrowserStackWebEnvironment(platform);
				break;
			case "Native":
				setupBrowserStackNativeEnvironment(platform);
				break;
			case "Hybrid":
				setupBrowserStackHybridEnvironment(platform);
				break;
			case "API":
				setupBrowserStackAPIEnvironment();
				break;
			}
			break;
		}
	}

	private static void setupLocalWebEnvironment(String platform, String browser) throws Exception {
		if (!platform.equals("Windows") && !platform.equals("Mac")) {
			throw new IllegalArgumentException("Unsupported platform: " + platform);
		}

		String projectRootPath = System.getProperty("user.dir");
		String web_Headless_Browser = UIConstantsUtil.APP_CONFIG_MAP.get("Web_Headless_Browser");
		boolean isHeadless = "Yes".equalsIgnoreCase(web_Headless_Browser);

		switch (browser) {
		case "Chrome":
			System.setProperty("webdriver.chrome.driver",
					projectRootPath + File.separator + "src" + File.separator + "main" + File.separator + "resources"
							+ File.separator + "drivers" + File.separator + "chromedriver.exe");
			ChromeOptions chromeOptions = chromeOptions();
			DesiredCapabilities crcapabilities = new DesiredCapabilities();
			crcapabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
			chromeOptions.merge(crcapabilities);
			chromeOptions.setBinary(projectRootPath + File.separator + "chrome-win64" + File.separator + "chrome.exe");
			System.setProperty("jdk.internal.lambda.dumpProxyClasses", "");
			UIConstantsUtil.WEB_DRIVER = new ChromeDriver(chromeOptions);
			break;
		case "Firefox":
			String firefoxDriverPath = projectRootPath + File.separator + "src" + File.separator + "main"
					+ File.separator + "resources" + File.separator + "drivers" + File.separator + "geckodriver.exe";
			System.setProperty("webdriver.gecko.driver", firefoxDriverPath);
			WebDriverManager.firefoxdriver().setup();
			FirefoxOptions firefoxOptions = new FirefoxOptions();
			if (isHeadless) {
				firefoxOptions.addArguments("--headless"); // Run in headless mode
			}
			UIConstantsUtil.WEB_DRIVER = new FirefoxDriver(firefoxOptions);
			break;
		case "Safari":
			if (!platform.equals("Mac")) {
				throw new IllegalArgumentException("Safari is only supported on Mac");
			}
			UIConstantsUtil.WEB_DRIVER = new SafariDriver();
			break;
		case "Edge":
			WebDriverManager.edgedriver().setup();
			EdgeOptions edgeOptions = new EdgeOptions();
			if (isHeadless) {
				edgeOptions.addArguments("--headless", "--disable-gpu");
			}
			UIConstantsUtil.WEB_DRIVER = new EdgeDriver(edgeOptions);
			break;
		}
		configureTrustAllCerts();
		UIConstantsUtil.WEB_DRIVER.manage().window().maximize();
		UIConstantsUtil.WEB_DRIVER.manage().deleteAllCookies();
		UIConstantsUtil.WEB_DRIVER.navigate().to(UIConstantsUtil.APP_CONFIG_MAP.get("Web_BaseURL"));
		new WaitUtils(UIConstantsUtil.WEB_DRIVER).waitForPageLoaded();
	}

	private static void setupLocalNativeEnvironment(String platform, String mobilePlatformType) {
		if ("Android".equalsIgnoreCase(platform)) {
			setupAndroidEnvironment(mobilePlatformType);
		} else if ("iOS".equalsIgnoreCase(platform)) {
			setupIOSEnvironment(mobilePlatformType);
		}
	}

	private static void setupLocalHybridEnvironment(String platform, String mobilePlatformType) {
		DesiredCapabilities caps = new DesiredCapabilities();
		String projectRootPath = System.getProperty("user.dir");
		if ("Android".equalsIgnoreCase(platform)) {

			caps.setCapability("platformName", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_AndroidPlatformName"));
			if ("emulator".equalsIgnoreCase(mobilePlatformType)) {
				String emulatorName = UIConstantsUtil.APP_CONFIG_MAP.get("Emulator_DeviceName");
				if (!isEmulatorRunning(emulatorName)) {
					launchEmulator(emulatorName);
				}
				caps.setCapability("deviceName", emulatorName);
				caps.setCapability("udid", UIConstantsUtil.APP_CONFIG_MAP.get("udid"));
			} else {
				caps.setCapability("deviceName", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_AndroidDeviceName"));
				caps.setCapability("udid", UIConstantsUtil.APP_CONFIG_MAP.get("Android_udid"));
			}
			caps.setCapability("platformVersion", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_AndroidPlatformVersion"));
			caps.setCapability("browserName", "Chrome");
			caps.setCapability("chromedriverExecutable",
					projectRootPath + File.separator + "src" + File.separator + "main" + File.separator + "resources"
							+ File.separator + "drivers" + File.separator + "chromedriver.exe");

		} else if ("iOS".equalsIgnoreCase(platform)) {
			caps.setCapability("platformName", "iOS");
			caps.setCapability("browserName", "Safari");
			caps.setCapability("automationName", "XCUITest");
			caps.setCapability("platformVersion", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_iOSPlatformVersion"));
			caps.setCapability("deviceName", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_iOSDeviceName"));
			caps.setCapability("startIWDP", true);

		}
		if (UIConstantsUtil.appiumService == null || !UIConstantsUtil.appiumService.isRunning()) {
			startAppiumServer();
		} else {
			stopRunningAppiumServer();
			startAppiumServer();
		}

		UIConstantsUtil.WEB_DRIVER = new AppiumDriver(UIConstantsUtil.appiumService.getUrl(), caps);
		UIConstantsUtil.WEB_DRIVER.manage().deleteAllCookies();
		UIConstantsUtil.WEB_DRIVER.navigate().to(UIConstantsUtil.APP_CONFIG_MAP.get("Web_BaseURL"));
		new WaitUtils(UIConstantsUtil.WEB_DRIVER).waitForPageLoaded();
	}

	private static void setupLocalAPIEnvironment() {
		RestAssured.baseURI = UIConstantsUtil.APP_CONFIG_MAP.get("API_BaseURL");
	}

	private static void setupAndroidEnvironment(String mobilePlatformType) {
		DesiredCapabilities caps = new DesiredCapabilities();
		caps.setCapability("platformName", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_AndroidPlatformName"));
		caps.setCapability("automationName", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_AndroidAutomationName"));
		caps.setCapability("automationName", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_AndroidAutomationName"));
		caps.setCapability("platformVersion", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_AndroidPlatformVersion"));
		caps.setCapability("app", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_AndroidApk"));
		caps.setCapability("appPackage", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_AndroidAppPackage"));
		caps.setCapability("appActivity", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_AndroidAppActivity"));
		caps.setCapability("noReset",
				Boolean.parseBoolean(UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_AndroidNoReset")));
		caps.setCapability("fullReset",
				Boolean.parseBoolean(UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_AndroidFullReset")));

		if ("emulator".equalsIgnoreCase(mobilePlatformType)) {
			String emulatorName = UIConstantsUtil.APP_CONFIG_MAP.get("Emulator_DeviceName");
			if (!isEmulatorRunning(emulatorName)) {
				launchEmulator(emulatorName);
			}
			caps.setCapability("deviceName", emulatorName);
			caps.setCapability("udid", UIConstantsUtil.APP_CONFIG_MAP.get("udid"));
		} else {
			caps.setCapability("deviceName", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_AndroidDeviceName"));
			caps.setCapability("udid", UIConstantsUtil.APP_CONFIG_MAP.get("Android_udid"));
		}

		if (UIConstantsUtil.appiumService == null || !UIConstantsUtil.appiumService.isRunning()) {
			startAppiumServer();
		} else {
			stopRunningAppiumServer();
			startAppiumServer();
		}

		UIConstantsUtil.WEB_DRIVER = new AppiumDriver(UIConstantsUtil.appiumService.getUrl(), caps);
	}

	private static void setupIOSEnvironment(String mobilePlatformType) {
		DesiredCapabilities caps = new DesiredCapabilities();
		caps.setCapability("platformName", "iOS");
		caps.setCapability("automationName", "XCUITest");
		caps.setCapability("platformVersion", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_iOSPlatformVersion"));
		caps.setCapability("deviceName", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_iOSDeviceName"));
		caps.setCapability("app", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_iOSApp"));
		caps.setCapability("noReset", Boolean.parseBoolean(UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_iOSNoReset")));
		caps.setCapability("useNewWDA",
				Boolean.parseBoolean(UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_iOSUseNewWDA")));
		caps.setCapability("xcodeOrgId", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_iOSXcodeOrgId"));
		caps.setCapability("xcodeSigningId", "iPhone Developer");
		caps.setCapability("bundleId", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_iOSBundleId"));

		if ("simulator".equalsIgnoreCase(mobilePlatformType)) {
			launchSimulator(UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_iOSDeviceName"));
		}

		if (UIConstantsUtil.appiumService == null || !UIConstantsUtil.appiumService.isRunning()) {
			startAppiumServer();
		} else {
			stopRunningAppiumServer();
			startAppiumServer();
		}

		UIConstantsUtil.WEB_DRIVER = new RemoteWebDriver(UIConstantsUtil.appiumService.getUrl(), caps);
	}

	private static void setupBrowserStackWebEnvironment(String platform) throws MalformedURLException {
		String USERNAME = UIConstantsUtil.APP_CONFIG_MAP.get("BrowserStack_AccessKey");
		String encodedAccessKey = UIConstantsUtil.APP_CONFIG_MAP.get("BrowserStack_AccessKey");
		String ACCESS_KEY = TestReportUtility.decodeBase64(encodedAccessKey);
		String BS_URL = "https://" + USERNAME + ":" + ACCESS_KEY + "@hub-cloud.browserstack.com/wd/hub";
		DesiredCapabilities caps = new DesiredCapabilities();
		if (platform.equals("Windows") || platform.equals("Mac")) {
			caps.setCapability("browserName", UIConstantsUtil.APP_CONFIG_MAP.get("BrowserStack_BrowserName"));
			caps.setCapability("browserVersion", UIConstantsUtil.APP_CONFIG_MAP.get("BrowserStack_BrowserVersion"));
			caps.setCapability("os", UIConstantsUtil.APP_CONFIG_MAP.get("BrowserStack_OS_Desktop"));
			caps.setCapability("os_version", UIConstantsUtil.APP_CONFIG_MAP.get("BrowserStack_OSVersion_Desktop"));
			caps.setCapability("resolution", UIConstantsUtil.APP_CONFIG_MAP.get("BrowserStack_Resolution_Desktop"));
			UIConstantsUtil.WEB_DRIVER = new RemoteWebDriver(new URL(BS_URL), caps);
		}
	}

	private static void setupBrowserStackNativeEnvironment(String platform) throws MalformedURLException {
		String USERNAME = UIConstantsUtil.APP_CONFIG_MAP.get("BrowserStack_AccessKey");
		String encodedAccessKey = UIConstantsUtil.APP_CONFIG_MAP.get("BrowserStack_AccessKey");
		String ACCESS_KEY = TestReportUtility.decodeBase64(encodedAccessKey);
		String BS_URL = "https://" + USERNAME + ":" + ACCESS_KEY + "@hub-cloud.browserstack.com/wd/hub";
		DesiredCapabilities caps = new DesiredCapabilities();
		if (platform.equals("Android")) {
			caps.setCapability("platformName", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_AndroidPlatformName"));
			caps.setCapability("deviceName", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_AndroidDeviceName"));
			caps.setCapability("platformVersion", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_AndroidPlatformVersion"));
			caps.setCapability("app", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_AndroidApk"));
			caps.setCapability("automationName", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_AndroidAutomationName"));
			caps.setCapability("appPackage", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_AndroidAppPackage"));
			caps.setCapability("appActivity", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_AndroidAppActivity"));
			caps.setCapability("noReset", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_AndroidNoReset"));
			caps.setCapability("fullReset", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_AndroidFullReset"));
			UIConstantsUtil.WEB_DRIVER = new RemoteWebDriver(new URL(BS_URL), caps);
		} else if (platform.equals("iOS")) {
			caps.setCapability("platformName", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_iOSPlatformName"));
			caps.setCapability("deviceName", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_iOSDeviceName"));
			caps.setCapability("platformVersion", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_iOSPlatformVersion"));
			caps.setCapability("app", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_iOSApp"));
			caps.setCapability("automationName", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_iOSAutomationName"));
			caps.setCapability("noReset", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_iOSNoReset"));
			caps.setCapability("useNewWDA", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_iOSUseNewWDA"));
			UIConstantsUtil.WEB_DRIVER = new RemoteWebDriver(new URL(BS_URL), caps);
		}
	}

	private static void setupBrowserStackHybridEnvironment(String platform) throws MalformedURLException {
		setupBrowserStackNativeEnvironment(platform);
	}

	private static void setupBrowserStackAPIEnvironment() {
		RestAssured.baseURI = UIConstantsUtil.APP_CONFIG_MAP.get("API_BaseURL");

	}

	private static void configureTrustAllCerts() throws NoSuchAlgorithmException, KeyManagementException {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		// Install the all-trusting host verifier
		HostnameVerifier allHostsValid = (String hostname, SSLSession session) -> true;
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	}

	private static ChromeOptions chromeOptions() {
		ChromeOptions chromeOptions = new ChromeOptions();
		String web_Headless_Browser = UIConstantsUtil.APP_CONFIG_MAP.get("Web_Headless_Browser");
		if ("Yes".equalsIgnoreCase(web_Headless_Browser)) {
			chromeOptions.addArguments("--headless"); // Enable headless mode
		}
		chromeOptions.addArguments("--incognito"); // Launch in incognito mode
		chromeOptions.setExperimentalOption("useAutomationExtension", false);
		chromeOptions.addArguments("download.prompt_for_download", "false");
		chromeOptions.addArguments("download.directory_upgrade", "true");
		chromeOptions.addArguments("--safebrowsing-disable-download-protection");
		chromeOptions.addArguments("safebrowsing-disable-extension-blacklist");
		chromeOptions.addArguments("--disable-gpu", "--disable-dev-shm-usage", "--ignore-certificate-errors",
				"--allow-running-insecure-content", "--allow-insecure-localhost",
				"--unsafely-treat-insecure-origin-as-secure", "--ignore-urlfetcher-cert-requests", "--shm-size=2g",
				"--privileged", "--verbose", "--disable-popup-blocking",
				"--disable-blink-features=AutomationControlled", "--no-sandbox", "--disable-extensions",
				"--ignore-ssl-error", "--dns-prefetch-disable", "--disable-web-security", "--no-proxy-server");
		chromeOptions.addArguments("--ignore-certificate-errors");
		chromeOptions.setExperimentalOption("excludeSwitches", new String[] { "enable-automation" });
		Map<String, Object> prefs = new HashMap<>();
		prefs.put("profile.default_content_setting_values.media_stream_mic", 2);
		prefs.put("profile.default_content_setting_values.media_stream_camera", 2);
		prefs.put("profile.default_content_setting_values.geolocation", 2);
		prefs.put("profile.default_content_setting_values.notifications", 2);
		prefs.put("credentials_enable_service", false);
		prefs.put("profile.password_manager_enabled", false);
		prefs.put("profile.default_content_settings.popups", 1); // Allow pop-ups
		prefs.put("profile.content_settings.pattern_pairs.*.multiple-automatic-downloads", 1);
		chromeOptions.setExperimentalOption("prefs", prefs);
		chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
		chromeOptions.setCapability("goog:loggingPrefs", logPrefs);
		return chromeOptions;
	}

	private static void startAppiumServer() {
		System.out.println("Attempting to start the Appium Server...");
		try {
			// Retrieve paths from configuration
			String nodePath = UIConstantsUtil.APP_CONFIG_MAP.get("Path_NodeJsexe");
			String appiumPath = UIConstantsUtil.APP_CONFIG_MAP.get("Path_AppiumJs");

			// Validate paths
			if (nodePath == null || appiumPath == null) {
				throw new IllegalStateException("Node or Appium path is not configured correctly.");
			}

			File nodeFile = new File(nodePath);
			File appiumJsFile = new File(appiumPath);
			if (!nodeFile.exists() || !appiumJsFile.exists()) {
				throw new IllegalStateException("Node.js or Appium.js file does not exist.");
			}

			// Configure and start Appium service
			AppiumDriverLocalService service = AppiumDriverLocalService.buildService(new AppiumServiceBuilder()
					.usingDriverExecutable(nodeFile).withAppiumJS(appiumJsFile).usingAnyFreePort() // Specify using any
																									// free port
					.withArgument(GeneralServerFlag.SESSION_OVERRIDE)
					.withArgument(GeneralServerFlag.LOG_LEVEL, "error"));

			service.start();
			UIConstantsUtil.appiumService = service;

			if (service.isRunning()) {
				System.out.println("Successfully connected to Appium Server at: " + service.getUrl());
			} else {
				System.out.println("Failed to start Appium Server.");
			}
		} catch (Exception e) {
			System.err.println("Error starting Appium Server: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void stopRunningAppiumServer() {
		if (UIConstantsUtil.appiumService != null && UIConstantsUtil.appiumService.isRunning()) {
			// Stop the Appium server
			UIConstantsUtil.appiumService.stop();
			System.out.println("Appium Server has been stopped successfully.");
		} else {
			System.out.println("No Appium server is currently running or it is not initialized.");
		}
	}

	private static void launchEmulator(String emulatorName) {
		try {
			// Try to get the SDK path from the ANDROID_HOME environment variable
			String sdkPath = System.getenv("ANDROID_HOME");

			// If ANDROID_HOME is not set, fallback to a configured SDK path
			if (sdkPath == null) {
				sdkPath = UIConstantsUtil.APP_CONFIG_MAP.get("SDK_Path"); // Example of fetching from a config map
				// Check if the fallback SDK path is also not set or invalid
				if (sdkPath == null || !(new File(sdkPath).exists())) {
					throw new IllegalStateException(
							"Android SDK path is not properly configured. Please set ANDROID_HOME environment variable or configure SDK_Path correctly.");
				}
			}

			// Construct the path to the emulator tool
			String emulatorPath = sdkPath + "\\emulator\\emulator.exe"; // Use correct path separator for Windows

			// Command to launch the emulator
			ProcessBuilder pb = new ProcessBuilder(emulatorPath, "-avd", emulatorName, "-netdelay", "none", "-netspeed",
					"full");
			pb.redirectErrorStream(true); // Redirect error stream to standard output
			@SuppressWarnings("unused")
			Process process = pb.start();

			// Wait for the emulator to boot up
			waitForEmulatorToStart();

			System.out.println("Emulator " + emulatorName + " started successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed to start emulator " + emulatorName + ": " + e.getMessage());
		}
	}

	/**
	 * Waits for the emulator to become ready by checking the boot status via ADB.
	 */
	private static void waitForEmulatorToStart() throws InterruptedException, IOException {
		String sdkPath = System.getenv("ANDROID_HOME");
		if (sdkPath == null) {
			sdkPath = UIConstantsUtil.APP_CONFIG_MAP.get("SDK_Path");
		}

		if (sdkPath == null || !(new File(sdkPath).exists())) {
			throw new IllegalStateException(
					"Android SDK path is not properly configured. Please set ANDROID_HOME environment variable or configure SDK_Path correctly.");
		}

		String adbPath = sdkPath + "/platform-tools/adb";
		String[] commandWait = { adbPath, "wait-for-device" };
		String[] commandBoot = { adbPath, "shell", "getprop", "sys.boot_completed" };

		new ProcessBuilder(commandWait).start().waitFor();
		boolean bootCompleted = false;
		while (!bootCompleted) {
			Process proc = new ProcessBuilder(commandBoot).start();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					if ("1".equals(line.trim())) {
						bootCompleted = true;
						break;
					}
				}
			}
			if (!bootCompleted) {
				System.out.println("Waiting for the emulator to fully boot...");
				Thread.sleep(30000);
			}
		}
		System.out.println("Emulator is ready and fully booted.");
	}

	private static boolean isEmulatorRunning(String emulatorName) {
		String adbPath = System.getenv("ANDROID_HOME") + "/platform-tools/adb";
		String[] command = { adbPath, "devices" };
		try {
			Process process = new ProcessBuilder(command).start();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.contains(emulatorName)) {
						return true;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void stopEmulator() {
		try {
			String adbPath = getAdbPath(); // Method to determine the correct adb path
			if (adbPath == null) {
				System.err.println("ADB path is not configured correctly.");
				return;
			}
			Process listEmulators = new ProcessBuilder(adbPath, "devices").start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(listEmulators.getInputStream()));
			String line;
			boolean emulatorFound = false;
			while ((line = reader.readLine()) != null) {
				if (line.contains("emulator")) {
					emulatorFound = true;
					break;
				}
			}
			if (emulatorFound) {
				String[] command = { adbPath, "emu", "kill" };
				Process process = new ProcessBuilder(command).start();
				int exitCode = process.waitFor();
				if (exitCode == 0) {
					System.out.println("Emulator has been stopped successfully.");
				} else {
					System.err.println("Failed to stop the emulator with exit code " + exitCode);
				}
			} else {
				System.out.println("No running emulators to stop.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("An error occurred while trying to stop the emulator: " + e.getMessage());
		}
	}

	/**
	 * Helper method to get the correct adb path based on the ANDROID_HOME
	 * environment variable.
	 */
	private static String getAdbPath() {
		String sdkPath = System.getenv("ANDROID_HOME");
		if (sdkPath == null) {
			sdkPath = UIConstantsUtil.APP_CONFIG_MAP.get("SDK_Path");
		}
		if (sdkPath != null && new File(sdkPath).exists()) {
			return sdkPath + "/platform-tools/adb";
		}
		return null;
	}

	private static void launchSimulator(String simulatorName) {
		try {
			// Shut down all currently running simulators
			ProcessBuilder shutdown = new ProcessBuilder("xcrun", "simctl", "shutdown", "all");
			shutdown.start().waitFor();

			// Start the specific simulator
			ProcessBuilder startSimulator = new ProcessBuilder("xcrun", "simctl", "boot", simulatorName);
			Process p = startSimulator.start();
			int exitCode = p.waitFor();
			if (exitCode == 0) {
				System.out.println("Simulator " + simulatorName + " started successfully.");
			} else {
				System.err.println("Failed to start the simulator " + simulatorName);
			}

			// Optionally, you might want to wait until the simulator is fully booted
			waitForSimulatorToBeReady(simulatorName);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error launching the iOS simulator: " + e.getMessage());
		}
	}

	private static void waitForSimulatorToBeReady(String simulatorName) throws IOException, InterruptedException {
		boolean isReady = false;
		while (!isReady) {
			ProcessBuilder pb = new ProcessBuilder("xcrun", "simctl", "spawn", simulatorName, "launchctl", "print",
					"system");
			Process proc = pb.start();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.contains("com.apple.springboard.services")) {
						isReady = true;
						break;
					}
				}
			}
			if (!isReady) {
				System.out.println("Waiting for simulator to be ready...");
				Thread.sleep(3000);
			}
		}
		System.out.println("Simulator is ready.");
	}

	public static void stopSimulator() {
		try {
			// Command to shutdown all active iOS simulators
			ProcessBuilder pb = new ProcessBuilder("xcrun", "simctl", "shutdown", "all");
			Process process = pb.start();
			// Wait for the command to complete
			int exitCode = process.waitFor();
			if (exitCode == 0) {
				System.out.println("All iOS simulators have been stopped successfully.");
			} else {
				System.err.println("Failed to stop iOS simulators with exit code " + exitCode);
			}

			// Optionally, read the output from the command
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed to stop iOS simulator: " + e.getMessage());
		}
	}

	public static void extentReportEV() throws Exception {

		UIConstantsUtil.EXTENT_REPORTS
				.assignProject(StringUtils.capitalize(System.getProperty("user.name").replaceAll("[^a-zA-Z0-9]", " ")));
		UIConstantsUtil.EXTENT_REPORTS.addSystemInfo("Platform", UIConstantsUtil.APP_CONFIG_MAP.get("Platform"))
				.addSystemInfo("Java Version", System.getProperty("java.runtime.version"))
				.addSystemInfo("Automation Test Type", UIConstantsUtil.APP_CONFIG_MAP.get("TestType") + " Application")
				.addSystemInfo("Company Name", "Visionet Systems Inc");
		if ("web".equalsIgnoreCase(UIConstantsUtil.APP_CONFIG_MAP.get("TestType"))) {
			UIConstantsUtil.EXTENT_REPORTS.addSystemInfo("Automated Site URL",
					UIConstantsUtil.APP_CONFIG_MAP.get("Web_BaseURL"));
			UIConstantsUtil.BROWSER_VERSION = ((RemoteWebDriver) UIConstantsUtil.WEB_DRIVER).getCapabilities()
					.getBrowserVersion();
			UIConstantsUtil.EXTENT_REPORTS.addSystemInfo("Tested Browser",
					StringUtils.capitalize(UIConstantsUtil.APP_CONFIG_MAP.get("Web_Browser")) + " - "
							+ UIConstantsUtil.BROWSER_VERSION);
		} else if ("API".equalsIgnoreCase(UIConstantsUtil.APP_CONFIG_MAP.get("TestType"))) {

		} else {
			UIConstantsUtil.EXTENT_REPORTS.addSystemInfo("Platform Type",
					UIConstantsUtil.APP_CONFIG_MAP.get("platformType"));
			if ("Android".equalsIgnoreCase(UIConstantsUtil.APP_CONFIG_MAP.get("Platform"))) {
				UIConstantsUtil.EXTENT_REPORTS
						.addSystemInfo("Automation Name",
								UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_AndroidAutomationName"))
						.addSystemInfo("Platform Version",
								UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_AndroidPlatformVersion"));
				if ("emulator".equalsIgnoreCase(UIConstantsUtil.APP_CONFIG_MAP.get("platformType"))) {
					String emulatorName = UIConstantsUtil.APP_CONFIG_MAP.get("Emulator_DeviceName");
					UIConstantsUtil.EXTENT_REPORTS.addSystemInfo("Device Name", emulatorName);
				} else {
					UIConstantsUtil.EXTENT_REPORTS
							.addSystemInfo("Device Name",
									UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_AndroidDeviceName"))
							.addSystemInfo("udid", UIConstantsUtil.APP_CONFIG_MAP.get("Android_udid"));
				}
				UIConstantsUtil.EXTENT_REPORTS.addSystemInfo("App Package Name",
						UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_AndroidAppPackage"));
			} else if ("iOS".equalsIgnoreCase(UIConstantsUtil.APP_CONFIG_MAP.get("Platform"))) {
				UIConstantsUtil.EXTENT_REPORTS
						.addSystemInfo("Automation Name",
								UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_iOSAutomationName"))
						.addSystemInfo("Platform Version",
								UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_iOSPlatformVersion"))
						.addSystemInfo("Device Name", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_iOSDeviceName"))
						.addSystemInfo("iOS Xcode OrgId", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_iOSXcodeOrgId"))
						.addSystemInfo("iOS Xcode SigningId",
								UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_iOSXcodeSigningId"))
						.addSystemInfo("iOS Bundle Id", UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_iOSBundleId"));

			}

		}
		Locale locale = Locale.getDefault();
		InetAddress myIP = InetAddress.getLocalHost();
		UIConstantsUtil.EXTENT_REPORTS.addSystemInfo("Location", locale.getDisplayCountry())
				.addSystemInfo("Host Address", myIP.getHostAddress());
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

	public static String readFileAsString(String filePath) throws Exception {
		return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
	}

	public static synchronized void modifyExtentReport() throws Exception {
		String cssContent = BaseTestUtil.readFileAsString(TestReportUtility.EXTENT_REPORT_FOLDER + File.separator
				+ "extentreports" + File.separator + "css" + File.separator + "css.css");
		String jsContent = BaseTestUtil.readFileAsString(TestReportUtility.EXTENT_REPORT_FOLDER + File.separator
				+ "extentreports" + File.separator + "js" + File.separator + "scripts.js");
		List<String> lines = Files.readAllLines(
				Paths.get(TestReportUtility.EXTENT_REPORT_FOLDER + UIConstantsUtil.ReportFile), StandardCharsets.UTF_8);
		List<String> modifiedLines = lines.stream().map(line -> {
			if (line.contains("<head>")) {
				String fontAwesomeLink = "<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">";
				return line + fontAwesomeLink + "<style>" + cssContent + "</style>";
			} else if (line.contains("</body>")) {
				return "<script>" + jsContent + "</script>" + line;
			}
			return line;
		}).collect(Collectors.toList());
		String testName = TestReportUtility.testName();
		String reportName = File.separator + testName.replaceAll("Log", "ExtentReport") + ".html";
		Files.write(Paths.get(TestReportUtility.EXTENT_REPORT_FOLDER + File.separator + reportName), modifiedLines,
				StandardCharsets.UTF_8);
	}
}
