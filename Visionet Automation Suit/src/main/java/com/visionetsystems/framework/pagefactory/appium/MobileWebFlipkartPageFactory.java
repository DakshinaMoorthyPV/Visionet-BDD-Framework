package com.visionetsystems.framework.pagefactory.appium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.visionetsystems.framework.utils.UIConstantsUtil;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;

public class MobileWebFlipkartPageFactory {
	private WebDriver driver;

	@FindBy(id = "search_input") // Hybrid- Web browser
	@AndroidFindBy(id = "com.flipkart.android:id/search_widget_textbox") // Android Native
	@iOSXCUITFindBy(xpath = "//XCUIElementTypeTextField[@name='search_input']") // iOS Native
	public WebElement searchInput;

	@AndroidFindBy(id = "com.flipkart.android:id/search_button")
	@iOSXCUITFindBy(xpath = "//XCUIElementTypeButton[@name='search_button']")
	public WebElement searchButton;

	public MobileWebFlipkartPageFactory() {
		this.driver = UIConstantsUtil.WEB_DRIVER;
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
	}
}