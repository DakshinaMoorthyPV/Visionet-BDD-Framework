package com.visionetsystems.framework.pages.api;

public class HttpResponse {
	 private String content;
	    private int statusCode;
	    private long responseTime;

	    public String getContent() {
	        return content;
	    }

	    public void setContent(String content) {
	        this.content = content;
	    }

	    public int getStatusCode() {
	        return statusCode;
	    }

	    public void setStatusCode(int statusCode) {
	        this.statusCode = statusCode;
	    }

	    public long getResponseTime() {
	        return responseTime;
	    }

	    public void setResponseTime(long responseTime) {
	        this.responseTime = responseTime;
	    }
}
