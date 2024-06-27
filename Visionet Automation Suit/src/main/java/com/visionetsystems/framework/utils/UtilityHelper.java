package com.visionetsystems.framework.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.testng.ITestResult;

import com.relevantcodes.extentreports.LogStatus;

import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;

/**
 * Utility class providing helper methods for UI interactions and font
 * management.
 */
public class UtilityHelper {

	public void checkAndInstallFonts() {
		String fontsDirPath = System.getProperty("user.dir") + File.separator + "src/main/resources/fonts";
		File fontsDir = new File(fontsDirPath);
		if (!fontsDir.isDirectory()) {
			System.out.println("Invalid directory: " + fontsDirPath);
			return;
		}
		installFontsRecursively(fontsDir);
	}

	private void installFontsRecursively(File directory) {
		File[] files = directory.listFiles();
		if (files == null) {
			System.err.println("Error listing files in directory: " + directory.getPath());
			return;
		}
		for (File file : files) {
			if (file.isDirectory()) {
				installFontsRecursively(file);
			} else if (file.isFile() && (file.getName().endsWith(".ttf") || file.getName().endsWith(".otf"))) {
				registerFont(file);
			}
		}
	}

	public static void takeFullScreenShots(String expected, String actual, int status) throws Exception {
		takeFullScreenShots(expected, actual, status, Optional.empty());
	}

	public static void takeFullScreenShots(String expected, String actual, int status, Optional<WebElement> element)
			throws Exception {
		String sanitizedExpected = sanitizeExpectedString(expected, 100);
		String screenshotName = generateScreenshotName(sanitizedExpected, status);
		String screenshotDirectoryPath = TestReportUtility.SCREENSHOTS_FOLDER;
		String screenshotPath = screenshotDirectoryPath + File.separator + screenshotName;

		File screenshotDir = new File(screenshotDirectoryPath);
		if (!screenshotDir.exists() && !screenshotDir.mkdirs()) {
			throw new IOException("Failed to create directory for screenshots at: " + screenshotDirectoryPath);
		}

		TakesScreenshot tsDriver = (TakesScreenshot) UIConstantsUtil.WEB_DRIVER;
		File screenshotFile = tsDriver.getScreenshotAs(OutputType.FILE);

		if ("Native".equalsIgnoreCase(UIConstantsUtil.TEST_TYPE) && element.isPresent()) {
			enhanceScreenshot(screenshotFile, element.get(), screenshotPath);
			TestReportUtility.logTestStep(UIConstantsUtil.SCENARIO_TEST, expected, actual,
					convertStatusToLogStatus(status).name(), screenshotPath);
		} else {
			FileUtils.copyFile(screenshotFile, new File(screenshotPath));
			if (isUrl(expected)) {
				String[] extractedExpected = extractUrlAndText(expected);
				String[] extractedActual = extractUrlAndText(actual);
				String formattedExpected = extractedExpected[0] + formatUrlForLog(extractedExpected[1]);
				String formattedActual = extractedActual[0] + formatUrlForLog(extractedActual[1]);
				TestReportUtility.logTestStep(UIConstantsUtil.SCENARIO_TEST, formattedExpected, formattedActual,
						convertStatusToLogStatus(status).name(), screenshotPath);
			} else {
				TestReportUtility.logTestStep(UIConstantsUtil.SCENARIO_TEST, expected, actual,
						convertStatusToLogStatus(status).name(), screenshotPath);
			}
		}

	}

	public static void enhanceScreenshot(File screenshotFile, WebElement element, String screenshotPath)
			throws IOException, InterruptedException {
		BufferedImage img = ImageIO.read(screenshotFile);
		Graphics2D g = img.createGraphics();

		// Improve rendering quality for high-definition border drawing
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		Rectangle rect = element.getRect();

		// Define the colors for the Indian flag tricolor
		Color saffron = new Color(255, 153, 51);
		Color white = new Color(255, 255, 255);
		Color green = new Color(19, 136, 8);

		// Define the border thickness
		int borderWidth = 6;

		// Drawing the tricolor border
		// Outermost border - Saffron
		g.setColor(saffron);
		g.setStroke(new BasicStroke(borderWidth));
		g.drawRect(rect.x, rect.y, rect.width, rect.height);

		// Middle border - White
		g.setColor(white);
		g.setStroke(new BasicStroke(borderWidth));
		g.drawRect(rect.x + borderWidth, rect.y + borderWidth, rect.width - 2 * borderWidth,
				rect.height - 2 * borderWidth);

		// Innermost border - Green
		g.setColor(green);
		g.setStroke(new BasicStroke(borderWidth));
		g.drawRect(rect.x + 2 * borderWidth, rect.y + 2 * borderWidth, rect.width - 4 * borderWidth,
				rect.height - 4 * borderWidth);

		g.dispose();
		ImageIO.write(img, "png", new File(screenshotPath));
	}

	public static LogStatus convertStatusToLogStatus(int status) {
		return switch (status) {
		case ITestResult.SUCCESS -> LogStatus.PASS;
		case ITestResult.FAILURE -> LogStatus.FAIL;
		case ITestResult.SKIP -> LogStatus.SKIP;
		default -> LogStatus.UNKNOWN;
		};
	}

	private static boolean isUrl(String str) {
		String regex = "(http|https)://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(\\S*)?";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.find();
	}

	private static String[] extractUrlAndText(String text) {
		Pattern pattern = Pattern.compile("(.+)(https?://\\S+)");
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			return new String[] { matcher.group(1).trim(), matcher.group(2) };
		}
		return new String[] { text.trim(), "" };
	}

	private static String formatUrlForLog(String url) {
		if (url.startsWith("http://") || url.startsWith("https://")) {
			return String.format("<a href='%s' target='_blank'>%s</a>", url, url);
		}
		return url;
	}

	private static String generateScreenshotName(String stepDescription, int status) {
		TestContext context = new TestContext();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String timestamp = sdf.format(new Date());
		LogStatus logStatus = convertStatusToLogStatus(status);
		System.out.println(context.getCurrentScenario());
		String scenario = context.getCurrentScenario().replaceAll("[^\\w\\s]", "").replaceAll("\\s+", "_");
		return String.format("%s_%s_%s_%s.png", context.getCurrentFeature(), scenario, stepDescription,
				logStatus.name(), timestamp);
	}

	private static String sanitizeExpectedString(String expected, int maxLength) {
		String sanitized = expected.replaceAll("[^\\w\\s]", "_");
		return sanitized.length() > maxLength ? sanitized.substring(0, maxLength) : sanitized;
	}

	private static void registerFont(File fontFile) {
		try {
			Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			boolean isFontRegistered = Arrays.asList(ge.getAvailableFontFamilyNames()).contains(font.getName());

			if (!isFontRegistered) {
				ge.registerFont(font);
				System.out.println("Successfully registered font: " + font.getName());
			} else {
				System.out.println("Font already available: " + font.getName());
			}
		} catch (FontFormatException | IOException e) {
			System.err.println("Failed to register font from file: " + fontFile.getAbsolutePath());
			e.printStackTrace();
		}
	}

	/**
	 * Generates structured log statements for general UI interactions.
	 *
	 * @param action   The type of action (e.g., "click", "enter").
	 * @param variable The name of the UI element involved.
	 * @param text     Optional additional text involved in the action (e.g., text
	 *                 to be entered).
	 * @return A string array containing the expected statement, actual statement,
	 *         and failure message.
	 */
	public static String[] generateStatements(String action, String variable, String text) {
		String base = action + " '" + (text != null ? text + "' in the '" : "") + variable + "' field";
		String expected = "I expect to " + base;
		String actual = "I actually " + base;
		String failure = "Failure: I did not " + base;
		return new String[] { expected, actual, failure };
	}

	/**
	 * Generates structured log statements for dropdown interactions.
	 *
	 * @param action           The type of dropdown action (e.g., "selectByIndex",
	 *                         "selectByValue", "selectByText").
	 * @param dropdownVariable The name of the dropdown UI element involved.
	 * @param optionOrValue    The value or option to be selected, depending on the
	 *                         action type.
	 * @param selectionType    A string describing the selection criteria (e.g.,
	 *                         "index", "value", "text").
	 * @return A string array containing the expected statement, actual statement,
	 *         and failure message.
	 */
	public static String[] generateStatements(String action, String dropdownVariable, String optionOrValue,
			String selectionType) {
		String actionPhrase = action.toLowerCase().replace("selectby", "select by ");
		String detail = switch (selectionType.toLowerCase()) {
		case "index", "value" -> "by " + selectionType + " '" + optionOrValue + "'";
		case "text" -> "'" + optionOrValue + "'";
		default -> optionOrValue; // Default case handles unexpected types gracefully
		};
		String base = actionPhrase + " " + detail + " from '" + dropdownVariable + "' field";
		String expected = "I expect to " + base;
		String actual = "I actually " + base;
		String failure = "Failure: I did not " + base;

		return new String[] { expected, actual, failure };
	}

	public static String extractCompanyName() {
		try {
			if ("Web".equalsIgnoreCase(UIConstantsUtil.APP_CONFIG_MAP.get("TestType"))
					|| "Hybrid".equalsIgnoreCase(UIConstantsUtil.APP_CONFIG_MAP.get("TestType"))) {
				URI uri = new URI(UIConstantsUtil.APP_CONFIG_MAP.get("Web_BaseURL"));
				String host = uri.getHost();
				if (host == null) {
					return null;
				}
				String[] parts = host.split("\\.");
				if (parts.length > 2) {
					return parts[parts.length - 2]; // Extracts the company name
				}
				return null;
			} else if ("Native".equalsIgnoreCase(UIConstantsUtil.APP_CONFIG_MAP.get("TestType"))) {
				String apkPath = "Android".equalsIgnoreCase(UIConstantsUtil.APP_CONFIG_MAP.get("Platform"))
						? UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_AndroidApk")
						: UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_iOSApp");
				try (ApkFile apkFile = new ApkFile(new File(apkPath))) {
					ApkMeta apkMeta = apkFile.getApkMeta();
					return apkMeta.getPackageName().split("\\.")[1];
				} catch (IOException e) {
					System.err.println("Error reading APK file: " + e.getMessage());
				}
			} else if ("API".equalsIgnoreCase(UIConstantsUtil.APP_CONFIG_MAP.get("TestType"))) {
				URI apiUri = URI.create(UIConstantsUtil.APP_CONFIG_MAP.get("API_BaseURL"));
				String host = apiUri.getHost();
				if (host != null) {
					return host.replaceAll("^(www\\.)?(.+?)\\..*$", "$2");
				}
			}
		} catch (URISyntaxException e) {
			System.err.println("Invalid URL syntax: " + e.getMessage());
		}
		return null;
	}
}
