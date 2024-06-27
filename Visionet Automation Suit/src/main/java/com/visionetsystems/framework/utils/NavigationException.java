package com.visionetsystems.framework.utils;

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
