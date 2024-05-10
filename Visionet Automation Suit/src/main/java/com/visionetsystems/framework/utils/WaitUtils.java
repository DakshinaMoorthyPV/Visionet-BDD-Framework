package com.visionetsystems.framework.utils;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WaitUtils {

	private WebDriver driver;
	private WebDriverWait wait;

	public WaitUtils(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	// Implicit Wait
	public void setImplicitWait(long timeInSeconds) {
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeInSeconds));
	}

	// Explicit Wait to wait for a specific element to be visible
	public WebElement waitForElementVisible(WebElement element, long timeInSeconds) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeInSeconds));
		return wait.until(ExpectedConditions.visibilityOf(element));
	}

	// Explicit Wait for element to be clickable
	public WebElement waitForElementClickable(WebElement element, long timeInSeconds) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeInSeconds));
		return wait.until(ExpectedConditions.elementToBeClickable(element));
	}

	// Fluent Wait to wait for an element with polling
	public WebElement fluentWaitForElement(By locator, long timeoutInSeconds, long pollingEveryInSec) {
		FluentWait<WebDriver> fluentWait = new FluentWait<>(driver).withTimeout(Duration.ofSeconds(timeoutInSeconds))
				.pollingEvery(Duration.ofSeconds(pollingEveryInSec)).ignoring(NoSuchElementException.class);

		return fluentWait.until(driver -> driver.findElement(locator));
	}

	// Thread.sleep
	public void hardWait(long timeInMillis) throws InterruptedException {
		Thread.sleep(timeInMillis);
	}

	public void waitForPageLoaded() {
		ExpectedCondition<Boolean> expectation = new ExpectedCondition<>() {
			@Override
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
			}
		};

		try {
			wait.until(expectation);
		} catch (Throwable error) {
			System.out.println("Timeout waiting for Page Load Request to complete.");
		}
	}
}
