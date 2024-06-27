package com.visionetsystems.framework.utils;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.testng.ITestResult;
import org.testng.Reporter;

import com.visionetsystems.framework.pages.interfaces.ExceptionTypes;

public class ExceptionHandler {

	private static final Map<Class<? extends Throwable>, ExceptionTypes> EXCEPTION_TYPE_MAP = new HashMap<>();

	static {
		EXCEPTION_TYPE_MAP.put(TimeoutException.class, ExceptionTypes.TIMEOUT_EXCEPTION);
		EXCEPTION_TYPE_MAP.put(NullPointerException.class, ExceptionTypes.NULL_POINTER_EXCEPTION);
		EXCEPTION_TYPE_MAP.put(NoSuchElementException.class, ExceptionTypes.ELEMENT_NOT_FOUND_EXCEPTION);
		EXCEPTION_TYPE_MAP.put(NavigationException.class, ExceptionTypes.NAVIGATION_EXCEPTION);
		EXCEPTION_TYPE_MAP.put(AssertionError.class, ExceptionTypes.ASSERTION_FAILURE);
		EXCEPTION_TYPE_MAP.put(VerificationException.class, ExceptionTypes.VERIFICATION_EXCEPTION);
		EXCEPTION_TYPE_MAP.put(FrameSwitchException.class, ExceptionTypes.FRAME_SWITCH_EXCEPTION);
		EXCEPTION_TYPE_MAP.put(CookieException.class, ExceptionTypes.COOKIE_EXCEPTION);
		EXCEPTION_TYPE_MAP.put(NoAlertPresentException.class, ExceptionTypes.ALERT_EXCEPTION);
		EXCEPTION_TYPE_MAP.put(ScrollException.class, ExceptionTypes.SCROLL_EXCEPTION);
		EXCEPTION_TYPE_MAP.put(ActionFailureException.class, ExceptionTypes.ACTION_FAILURE);
		EXCEPTION_TYPE_MAP.put(HttpException.class, ExceptionTypes.HTTP_EXCEPTION);
		EXCEPTION_TYPE_MAP.put(RuntimeException.class, ExceptionTypes.RUNTIME_EXCEPTION);
		EXCEPTION_TYPE_MAP.put(IllegalStateException.class, ExceptionTypes.ILLEGAL_STATE_EXCEPTION);
	}

	/**
	 * Handles the given exception based on its type.
	 *
	 * @param ex the exception to handle
	 * @throws Throwable if the exception should be rethrown
	 */
	private static void handleException(Throwable ex) throws Throwable {
		ExceptionTypes exceptionType = getExceptionTypeByException(ex);
		String callerMethodName = getCallerMethodName(ex);

		// Print the stack trace for all exceptions
		ex.printStackTrace();

		switch (exceptionType) {
		case TIMEOUT_EXCEPTION:
		case NULL_POINTER_EXCEPTION:
		case ELEMENT_NOT_FOUND_EXCEPTION:
		case NAVIGATION_EXCEPTION:
		case ACTION_FAILURE:
		case ASSERTION_FAILURE:
		case FRAME_SWITCH_EXCEPTION:
		case COOKIE_EXCEPTION:
		case ALERT_EXCEPTION:
		case SCROLL_EXCEPTION:
		case VERIFICATION_EXCEPTION:
		case ILLEGAL_STATE_EXCEPTION:
			logException(exceptionType.getMessage());
			captureScreenshot(callerMethodName, exceptionType, ex);
			break;
		case HTTP_EXCEPTION:
			HttpException httpEx = (HttpException) ex;
			logException("HTTP Exception: Status Code " + httpEx.getStatusCode() + " - " + exceptionType.getMessage());
			captureScreenshot(callerMethodName, exceptionType, ex);
			break;
		case RUNTIME_EXCEPTION:
			logException(exceptionType.getMessage());
			captureScreenshot(callerMethodName, exceptionType, ex);
			throw (RuntimeException) ex; // Rethrow if it's a RuntimeException
		case UNKNOWN_EXCEPTION:
		default:
			logException(ExceptionTypes.UNKNOWN_EXCEPTION.getMessage());
			captureScreenshot(callerMethodName, ExceptionTypes.UNKNOWN_EXCEPTION, ex);
			throw ex; // Rethrow the unknown exception
		}
	}

	private static ExceptionTypes getExceptionTypeByException(Throwable ex) {
		return EXCEPTION_TYPE_MAP.getOrDefault(ex.getClass(), ExceptionTypes.UNKNOWN_EXCEPTION);
	}

	private static void logException(String message) {
		Log.error(message);
	}

	private static void captureScreenshot(String callerMethodName, ExceptionTypes exceptionType, Throwable ex)
			throws Exception {
		UtilityHelper.takeFullScreenShots(
				"I expected to successfully perform the '" + callerMethodName + "' functionality",
				"But encountered an error: " + ex.getMessage(), ITestResult.FAILURE);
	}

	private static String getCallerMethodName(Throwable throwable) {
		StackTraceElement[] stackTrace = throwable.getStackTrace();
		if (stackTrace.length > 1) {
			return stackTrace[1].getMethodName(); // [0] will be the current method, [1] will be the caller
		}
		return "Unknown";
	}

	/**
	 * Handles exceptions during test execution.
	 *
	 * @param e            the exception to handle
	 * @param errorBuilder the error message builder
	 * @throws Throwable if the exception should be rethrown
	 */
	public static void handleTestException(Exception e, StringBuilder errorBuilder) {
		// Ensure logging configuration is up-to-date
		TestReportUtility.updateLog4j2Configuration();

		// Log the original exception with its stack trace to the console
		Log.error("Test failed due to an exception: ", e);

		// Construct the error message, adding details from errorBuilder if available
		StringBuilder errorMessage = new StringBuilder("Test failed due to an exception: " + e.getMessage());
		if (errorBuilder != null && errorBuilder.length() > 0) {
			errorMessage.append("\nError details: ").append(errorBuilder);
		}

		// Log the complete error message
		Log.error(errorMessage.toString());

		try {
			// Handle the exception with the utility class
			handleException(e);
		} catch (Throwable t) {
			// Log any additional exceptions thrown during handling
			Log.error("An error occurred while handling the exception: ", t);
		}

		// Set the status of the current test to failed
		ITestResult testResult = Reporter.getCurrentTestResult();
		testResult.setStatus(ITestResult.FAILURE);
		testResult.setThrowable(e);

		// Optionally log the failure to the TestNG reporter
		Reporter.log("Test failed due to an exception: " + errorMessage.toString());
		e.printStackTrace();
	}

}
