package com.visionetsystems.framework.utils;

public class TestContext {
	public static String currentFeature;
	public static String currentScenario;

	public String getCurrentFeature() {
		return currentFeature;
	}

	public void setCurrentFeature(String currentFeature) {
		TestContext.currentFeature = currentFeature;
	}

	public String getCurrentScenario() {
		return currentScenario;
	}

	public void setCurrentScenario(String currentScenario) {
		TestContext.currentScenario = currentScenario;
	}
}
