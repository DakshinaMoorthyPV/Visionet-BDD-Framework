package com.visionetsystems.framework.utils;

import java.time.Duration;
import java.util.Collections;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import io.appium.java_client.AppiumDriver;

public class UIActionHandler {

	private JavascriptExecutor js;
	private WebDriver driver;

	public UIActionHandler(WebDriver driver) {
		this.driver = UIConstantsUtil.WEB_DRIVER;
		this.js = (JavascriptExecutor) driver;
	}

	public void scrollToElementView(WebElement element) throws Exception {
		try {
			Thread.sleep(550);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		final int headerHeight = 100;
		final int additionalOffset = 20;
		Boolean isElementInViewPort = (Boolean) js.executeScript(
				"var elem = arguments[0], box = elem.getBoundingClientRect(), " + "headerHeight = arguments[1], "
						+ "isInViewPort = (box.top >= headerHeight) && (box.bottom <= window.innerHeight); "
						+ "return isInViewPort;",
				element, headerHeight);
		if (!isElementInViewPort) {
			js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
			Thread.sleep(1000);
			js.executeScript("window.scrollBy(0, -arguments[0]);", headerHeight + additionalOffset);
		}
		Point location = element.getLocation();
		int xCoordinate = location.getX();
		int yCoordinate = location.getY() - headerHeight - additionalOffset;
		System.out.println("Element x coordinate: " + xCoordinate + ", y coordinate: " + yCoordinate);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public void performSingleTap(WebElement element) {
		PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
		Sequence tapSequence = new Sequence(finger, 0);

		int centerX = element.getLocation().getX() + element.getSize().getWidth() / 2;
		int centerY = element.getLocation().getY() + element.getSize().getHeight() / 2;

		tapSequence
				.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centerX, centerY));
		tapSequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
		tapSequence.addAction(new Pause(finger, Duration.ofMillis(200))); // Correct pause usage
		tapSequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
		((AppiumDriver) driver).perform(Collections.singletonList(tapSequence));

	}

	public void swipe(int startX, int startY, int endX, int endY) {
		PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger1");

		// Define the sequence for a swipe gesture
		Sequence swipe = new Sequence(finger, 0)
				.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY))
				.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
				.addAction(new Pause(finger, Duration.ofMillis(200))) // Pause during the touch
				.addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), endX, endY))
				.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

		// Perform the sequence of actions
		((AppiumDriver) driver).perform(Collections.singletonList(swipe));
	}

	public void scrollToMobileElement(WebElement element) {
		int centerX = element.getLocation().getX() + element.getSize().getWidth() / 2;
		int startY = driver.manage().window().getSize().getHeight() * 3 / 4; // Start at 75% height from the top
		int endY = driver.manage().window().getSize().getHeight() / 4; // End at 25% height from the top
		swipe(centerX, startY, centerX, endY);
	}

	public void highlightElement(WebElement element) throws Exception {
		scrollToElementView(element);
		Thread.sleep(300);
		js.executeScript(
				"arguments[0].setAttribute('style', 'backdrop-filter: blur(2px); background: rgb(255,255,255) repeating-conic-gradient(rgb(0,0,128,0.1) 0 10deg, transparent 0 20deg);"
						+ " border-top: 3px groove rgba(255, 153, 51); border-bottom: 3px groove rgba(19, 136, 8); border-left: 3px groove rgba(255, 255, 255); border-right: 3px groove rgba(255, 255, 255); border-radius: 20.5px; color: #06038D;');",
				element);
		Thread.sleep(300);
	}

	public void removeHighlightOfElement(WebElement element) {
		String jsAttribute = element.getAttribute("style");
		if (jsAttribute != null && !jsAttribute.isEmpty()) {
			js.executeScript("arguments[0].setAttribute('style','')", element);
		}
	}
}
