package com.visionetsystems.framework.pages.interfaces;

public enum ExceptionTypes {
	TIMEOUT_EXCEPTION("Timeout occurred during operation.", org.openqa.selenium.TimeoutException.class),
	NULL_POINTER_EXCEPTION("Null pointer encountered.", NullPointerException.class),
	ELEMENT_NOT_FOUND_EXCEPTION("Desired element was not found on the page.",
			org.openqa.selenium.NoSuchElementException.class),
	NAVIGATION_EXCEPTION("Failed to navigate to the desired page or section.", NavigationException.class),
	ACTION_FAILURE("Failed to perform the specified action on an element.", ActionFailureException.class),
	ASSERTION_FAILURE("Assertion did not meet the expected criteria.", AssertionError.class),
	FRAME_SWITCH_EXCEPTION("Failed to switch to the specified frame.", FrameSwitchException.class),
	COOKIE_EXCEPTION("Failed to manage cookies.", CookieException.class),
	ALERT_EXCEPTION("Failed to handle window alert.", org.openqa.selenium.NoAlertPresentException.class),
	SCROLL_EXCEPTION("Failed to perform scrolling operation.", ScrollException.class),
	VERIFICATION_EXCEPTION("The identity verification request has not been accepted yet.", VerificationException.class),
	UNKNOWN_EXCEPTION("An unknown exception occurred.", Exception.class),
	RUNTIME_EXCEPTION("Runtime exception occurred.", RuntimeException.class),
	HTTP_EXCEPTION("HTTP error occurred.", HttpException.class),
	ILLEGAL_STATE_EXCEPTION("No element found.", IllegalStateException.class);

	private final String message;
	private final Class<? extends Throwable> exceptionClass;

	ExceptionTypes(String message, Class<? extends Throwable> exceptionClass) {
		this.message = message;
		this.exceptionClass = exceptionClass;
	}

	public String getMessage() {
		return message;
	}

	public Class<? extends Throwable> getExceptionClass() {
		return exceptionClass;
	}
}

// For the hypothetical exceptions:
class NavigationException extends Exception {
	private static final long serialVersionUID = 1L;
}

class ActionFailureException extends Exception {
	private static final long serialVersionUID = 2L;
}

class FrameSwitchException extends Exception {
	private static final long serialVersionUID = 3L;
}

class CookieException extends Exception {
	private static final long serialVersionUID = 4L;
}

class ScrollException extends Exception {
	private static final long serialVersionUID = 5L;
}

class VerificationException extends Exception {
	public static final long serialVersionUID = 6L;

	public VerificationException(String message) {
		super(message);
	}
}

class HttpException extends Exception {
	private static final long serialVersionUID = 7L;
	private final int statusCode;

	public HttpException(String message, int statusCode) {
		super(message);
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return statusCode;
	}

}
