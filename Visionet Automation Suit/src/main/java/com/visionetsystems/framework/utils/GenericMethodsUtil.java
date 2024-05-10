package com.visionetsystems.framework.utils;

import com.visionetsystems.framework.pages.interfaces.BasePage;

public abstract class GenericMethodsUtil {
	public static BasePage initializePageObjects(Class<? extends BasePage> webPageClass,
			Class<? extends BasePage> mobilePageClass) {
		String testType = UIConstantsUtil.APP_CONFIG_MAP.get("TestType");
		try {
			System.out.println(
					"Attempting to instantiate: " + (testType.equalsIgnoreCase("Web") ? webPageClass.getSimpleName()
							: mobilePageClass.getSimpleName()));
			return "Web".equalsIgnoreCase(testType) ? instantiateWebClass(webPageClass)
					: "Native".equalsIgnoreCase(testType) ? instantiateMobileClass(mobilePageClass)
							: throwUnsupportedPlatformException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to instantiate page object", e);
		}
	}

	private static BasePage instantiateWebClass(Class<? extends BasePage> clazz) throws Exception {
		return clazz.getDeclaredConstructor().newInstance();
	}

	private static BasePage instantiateMobileClass(Class<? extends BasePage> clazz) throws Exception {
		return clazz.getDeclaredConstructor().newInstance();
	}

	private static BasePage throwUnsupportedPlatformException() {
		throw new IllegalArgumentException("Unsupported platform type");
	}
}