package com.visionetsystems.framework.selenium.interactions;

import java.io.File;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.PointerInput.MouseButton;
import org.openqa.selenium.interactions.PointerInput.Origin;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;

import com.google.common.collect.ImmutableMap;
import com.visionetsystems.framework.utils.UIActionHandler;
import com.visionetsystems.framework.utils.UIConstantsUtil;
import com.visionetsystems.framework.utils.UtilityHelper;
import com.visionetsystems.framework.utils.WaitUtils;
import com.visionetsystems.framework.utils.WebResponseValidator;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.remote.SupportsContextSwitching;

/**
 * Class for performing common Selenium actions.
 */

public class SeleniumActions {

	private WebDriver driver;

	private WebDriverWait wait;

	private Actions actions;

	private JavascriptExecutor jsExecutor;

	private static final boolean TEST_TYPE_WEB = "Web".equalsIgnoreCase(UIConstantsUtil.TEST_TYPE);

	/**
	 * Constructor for SeleniumActions.
	 *
	 * @param driver the WebDriver instance to use.
	 */

	public SeleniumActions(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		this.actions = new Actions(driver);
		this.jsExecutor = (JavascriptExecutor) driver;
	}

	/**
	 * Clears the text from a web element.
	 *
	 * @param element  the WebElement to clear.
	 * @param variable the name of the variable for logging.
	 * @throws Exception if an error occurs during the clear action.
	 */

	public void clear(WebElement element, String variable) throws Exception {
		WaitUtils waitUtils = new WaitUtils(driver);
		waitUtils.hardWait(2000);
		try {
			wait.until(ExpectedConditions.visibilityOf(element));

			if (TEST_TYPE_WEB) {
				WebResponseValidator webResponseValidator = new WebResponseValidator(driver);
				webResponseValidator.verifyWebResponseStatus();
				UIActionHandler uiActionHandler = new UIActionHandler(driver);
				uiActionHandler.scrollToElementView(element);
				uiActionHandler.highlightElement(element);
				element.click();
				new WebDriverWait(driver, Duration.ofSeconds(60)).until(ExpectedConditions.visibilityOf(element))
						.clear();
				UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements("clear", variable, null)[0],
						UtilityHelper.generateStatements("clear", variable, null)[1], ITestResult.SUCCESS);
				uiActionHandler.removeHighlightOfElement(element);
			} else {
				UIActionHandler uiActionHandler = new UIActionHandler(driver);
				uiActionHandler.scrollToMobileElement(element);
				element.click();
				element.clear();
				UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements("clear", variable, null)[0],
						UtilityHelper.generateStatements("clear", variable, null)[1], ITestResult.SUCCESS,
						Optional.of(element));
			}
		} catch (Exception e) {
			System.err.println(UtilityHelper.generateStatements("clear", variable, null)[2]);

			if (TEST_TYPE_WEB) {
				UIActionHandler uiActionHandler = new UIActionHandler(driver);
				uiActionHandler.highlightElement(element);
				UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements("clear", variable, null)[0],
						UtilityHelper.generateStatements("clear", variable, null)[2], ITestResult.FAILURE);
				return;
			}

			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements("enter", variable, null)[0],
					UtilityHelper.generateStatements("clear", variable, null)[2], ITestResult.FAILURE,
					Optional.of(element));
			return;
		}
	}

	/**
	 * Sends keys to a web element.
	 *
	 * @param element  the WebElement to type into.
	 * @param text     the text to send to the element.
	 * @param variable the name of the variable for logging.
	 * @throws Exception if an error occurs during the sendKeys action.
	 */

	public void sendKeys(WebElement element, String text, String variable) throws Exception {
		WaitUtils waitUtils = new WaitUtils(driver);
		waitUtils.hardWait(2000);
		try {
			waitUtils.waitForElementVisible(element, 30);

			if (TEST_TYPE_WEB) {
				WebResponseValidator webResponseValidator = new WebResponseValidator(driver);
				webResponseValidator.verifyWebResponseStatus();
				UIActionHandler uiActionHandler = new UIActionHandler(driver);
				uiActionHandler.scrollToElementView(element);
				uiActionHandler.highlightElement(element);
				element.click();
				element.clear();
				new WebDriverWait(driver, Duration.ofSeconds(60)).until(ExpectedConditions.visibilityOf(element))
						.sendKeys(text);
				UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements("enter", variable, text)[0],
						UtilityHelper.generateStatements("enter", variable, text)[1], ITestResult.SUCCESS);
			} else {
				UIActionHandler uiActionHandler = new UIActionHandler(driver);
				uiActionHandler.scrollToMobileElement(element);
				element.click();
				element.clear();
				element.sendKeys(text);
				UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements("enter", variable, text)[0],
						UtilityHelper.generateStatements("enter", variable, text)[1], ITestResult.SUCCESS,
						Optional.of(element));
			}
		} catch (Exception e) {
			System.err.println(UtilityHelper.generateStatements("enter", variable, text)[2]);

			if (TEST_TYPE_WEB) {
				UIActionHandler uiActionHandler = new UIActionHandler(driver);
				uiActionHandler.highlightElement(element);
				UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements("enter", variable, text)[0],
						UtilityHelper.generateStatements("enter", variable, text)[2], ITestResult.FAILURE);
			}

			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements("enter", variable, text)[0],
					UtilityHelper.generateStatements("enter", variable, text)[2], ITestResult.FAILURE,
					Optional.of(element));
		}
	}

	/**
	 * Simulates the pressing of the ENTER key on a web element.
	 *
	 * @param element  the WebElement to receive the ENTER key.
	 * @param variable the name of the variable for logging.
	 * @throws Exception if an error occurs during the action.
	 */

	public void enterKey(WebElement element, String variable) throws Exception {
		try {
			actions.moveToElement(element).sendKeys(Keys.ENTER).perform();

			if (TEST_TYPE_WEB) {
				UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements("enter key", variable, null)[0],
						UtilityHelper.generateStatements("enter key", variable, null)[1], ITestResult.SUCCESS);
			}

			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements("enter key", variable, null)[0],
					UtilityHelper.generateStatements("enter key", variable, null)[1], ITestResult.SUCCESS,
					Optional.of(element));
		} catch (Exception e) {
			System.err.println(UtilityHelper.generateStatements("enter key", variable, null)[2]);

			if (TEST_TYPE_WEB) {
				UIActionHandler uiActionHandler = new UIActionHandler(driver);
				uiActionHandler.highlightElement(element);
				UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements("enter key", variable, null)[0],
						UtilityHelper.generateStatements("enter key", variable, null)[2], ITestResult.FAILURE);
			}

			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements("enter", variable, null)[0],
					UtilityHelper.generateStatements("enter key", variable, null)[2], ITestResult.FAILURE,
					Optional.of(element));
		}
	}

	/**
	 * Clicks a web element.
	 *
	 * @param element  the WebElement to be clicked.
	 * @param variable the name of the variable for logging.
	 * @throws Exception if an error occurs during the click action.
	 */

	public void click(WebElement element, String variable) throws Exception {
		WaitUtils waitUtils = new WaitUtils(driver);
		waitUtils.hardWait(3000);
		waitUtils.waitForElementVisible(element, 30);
		waitUtils.waitForElementClickable(element, 30);
		try {
			if (TEST_TYPE_WEB) {
				UIActionHandler uiActionHandler = new UIActionHandler(driver);
				uiActionHandler.scrollToElementView(element);
				uiActionHandler.highlightElement(element);
				UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements("click", variable, null)[0],
						UtilityHelper.generateStatements("click", variable, null)[1], ITestResult.SUCCESS);
				uiActionHandler.removeHighlightOfElement(element);
				element.click();
			} else {
				UIActionHandler uiActionHandler = new UIActionHandler(driver);
				uiActionHandler.scrollToMobileElement(element);
				UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements("click", variable, null)[0],
						UtilityHelper.generateStatements("click", variable, null)[1], ITestResult.SUCCESS,
						Optional.of(element));
				element.click();
			}
		} catch (Exception e) {
			try {
				new WebDriverWait(driver, Duration.ofSeconds(60))
						.until(ExpectedConditions.elementToBeClickable(element)).click();
			} catch (Exception e2) {
				try {
					jsExecutor.executeScript("arguments[0].click()", element);
				} catch (Exception e3) {
					System.err.println(UtilityHelper.generateStatements("click", variable, null)[2]);

					if (TEST_TYPE_WEB) {
						UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements("click", variable, null)[0],
								UtilityHelper.generateStatements("click", variable, null)[2], ITestResult.FAILURE);
					} else {
						UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements("click", variable, null)[0],
								UtilityHelper.generateStatements("click", variable, null)[2], ITestResult.FAILURE,
								Optional.of(element));
					}
				}
			}
		}
	}

	/**
	 * Checks a checkbox if it is not already checked.
	 *
	 * @param checkbox the WebElement representing the checkbox.
	 * @param variable the name of the variable for logging.
	 * @throws Exception if an error occurs during the check action.
	 */

	public void checkCheckbox(WebElement checkbox, String variable) throws Exception {
		WaitUtils waitUtils = new WaitUtils(driver);
		waitUtils.hardWait(3000);
		waitUtils.waitForElementVisible(checkbox, 30);
		waitUtils.waitForElementClickable(checkbox, 30);
		try {
			if (TEST_TYPE_WEB) {
				UIActionHandler uiActionHandler = new UIActionHandler(driver);
				uiActionHandler.scrollToElementView(checkbox);

				if (!checkbox.isSelected()) {
					checkbox.click();
				}

				uiActionHandler.highlightElement(checkbox);
				UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements("check checkbox", variable, null)[0],
						UtilityHelper.generateStatements("check checkbox", variable, null)[1], ITestResult.SUCCESS);
				uiActionHandler.removeHighlightOfElement(checkbox);
			} else {
				UIActionHandler uiActionHandler = new UIActionHandler(driver);
				uiActionHandler.scrollToMobileElement(checkbox);

				if (!checkbox.isSelected()) {
					checkbox.click();
				}

				UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements("check checkbox", variable, null)[0],
						UtilityHelper.generateStatements("check checkbox", variable, null)[1], ITestResult.SUCCESS,
						Optional.of(checkbox));
			}
		} catch (Exception e) {
			System.err.println(UtilityHelper.generateStatements("check checkbox", variable, null)[2]);

			if (TEST_TYPE_WEB) {
				UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements("check checkbox", variable, null)[0],
						UtilityHelper.generateStatements("check checkbox", variable, null)[2], ITestResult.FAILURE);
			} else {
				UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements("check checkbox", variable, null)[0],
						UtilityHelper.generateStatements("check checkbox", variable, null)[2], ITestResult.FAILURE,
						Optional.of(checkbox));
			}
		}
	}

	/**
	 * Unchecks a checkbox if it is currently checked.
	 *
	 * @param checkbox the WebElement representing the checkbox.
	 * @param variable the name of the variable for logging.
	 * @throws Exception if an error occurs during the uncheck action.
	 */

	public void uncheckCheckbox(WebElement checkbox, String variable) throws Exception {
		WaitUtils waitUtils = new WaitUtils(driver);
		waitUtils.hardWait(3000);
		waitUtils.waitForElementVisible(checkbox, 30);
		waitUtils.waitForElementClickable(checkbox, 30);
		try {
			if (TEST_TYPE_WEB) {
				UIActionHandler uiActionHandler = new UIActionHandler(driver);
				uiActionHandler.scrollToElementView(checkbox);

				if (checkbox.isSelected()) {
					checkbox.click();
				}

				uiActionHandler.highlightElement(checkbox);
				UtilityHelper.takeFullScreenShots(
						UtilityHelper.generateStatements("uncheck checkbox", variable, null)[0],
						UtilityHelper.generateStatements("uncheck checkbox", variable, null)[1], ITestResult.SUCCESS);
				uiActionHandler.removeHighlightOfElement(checkbox);
			} else {
				UIActionHandler uiActionHandler = new UIActionHandler(driver);
				uiActionHandler.scrollToMobileElement(checkbox);

				if (checkbox.isSelected()) {
					checkbox.click();
				}

				UtilityHelper.takeFullScreenShots(
						UtilityHelper.generateStatements("uncheck checkbox", variable, null)[0],
						UtilityHelper.generateStatements("uncheck checkbox", variable, null)[1], ITestResult.SUCCESS,
						Optional.of(checkbox));
			}
		} catch (Exception e) {
			System.err.println(UtilityHelper.generateStatements("uncheck checkbox", variable, null)[2]);

			if (TEST_TYPE_WEB) {
				UtilityHelper.takeFullScreenShots(
						UtilityHelper.generateStatements("uncheck checkbox", variable, null)[0],
						UtilityHelper.generateStatements("uncheck checkbox", variable, null)[2], ITestResult.FAILURE);
			} else {
				UtilityHelper.takeFullScreenShots(
						UtilityHelper.generateStatements("uncheck checkbox", variable, null)[0],
						UtilityHelper.generateStatements("uncheck checkbox", variable, null)[2], ITestResult.FAILURE,
						Optional.of(checkbox));
			}
		}
	}

	/**
	 * Selects a radio button if it is not already selected.
	 *
	 * @param radioButton the WebElement representing the radio button.
	 * @param variable    the name of the variable for logging.
	 * @throws Exception if an error occurs during the select action.
	 */

	public void selectRadioButton(WebElement radioButton, String variable) throws Exception {
		WaitUtils waitUtils = new WaitUtils(driver);
		waitUtils.hardWait(3000);
		waitUtils.waitForElementVisible(radioButton, 30);
		waitUtils.waitForElementClickable(radioButton, 30);
		try {
			if (TEST_TYPE_WEB) {
				UIActionHandler uiActionHandler = new UIActionHandler(driver);
				uiActionHandler.scrollToElementView(radioButton);

				if (!radioButton.isSelected()) {
					radioButton.click();
				}

				uiActionHandler.highlightElement(radioButton);
				UtilityHelper.takeFullScreenShots(
						UtilityHelper.generateStatements("select radio button", variable, null)[0],
						UtilityHelper.generateStatements("select radio button", variable, null)[1],
						ITestResult.SUCCESS);
				uiActionHandler.removeHighlightOfElement(radioButton);
			} else {
				UIActionHandler uiActionHandler = new UIActionHandler(driver);
				uiActionHandler.scrollToMobileElement(radioButton);

				if (!radioButton.isSelected()) {
					radioButton.click();
				}

				UtilityHelper.takeFullScreenShots(
						UtilityHelper.generateStatements("select radio button", variable, null)[0],
						UtilityHelper.generateStatements("select radio button", variable, null)[1], ITestResult.SUCCESS,
						Optional.of(radioButton));
			}
		} catch (Exception e) {
			System.err.println(UtilityHelper.generateStatements("select radio button", variable, null)[2]);

			if (TEST_TYPE_WEB) {
				UtilityHelper.takeFullScreenShots(
						UtilityHelper.generateStatements("select radio button", variable, null)[0],
						UtilityHelper.generateStatements("select radio button", variable, null)[2],
						ITestResult.FAILURE);
			} else {
				UtilityHelper.takeFullScreenShots(
						UtilityHelper.generateStatements("select radio button", variable, null)[0],
						UtilityHelper.generateStatements("select radio button", variable, null)[2], ITestResult.FAILURE,
						Optional.of(radioButton));
			}
		}
	}

	/**
	 * Selects an option in a dropdown by visible text.
	 *
	 * @param dropdown the WebElement representing the dropdown.
	 * @param text     the visible text of the option to select.
	 * @param variable the name of the variable for logging.
	 * @throws Exception if an error occurs during the selection.
	 */

	public void selectByVisibleText(WebElement dropdown, String text, String variable) throws Exception {
		WaitUtils waitUtils = new WaitUtils(driver);
		waitUtils.hardWait(3000);
		waitUtils.waitForElementVisible(dropdown, 30);
		UIActionHandler uiActionHandler = new UIActionHandler(driver);
		try {
			Select select = new Select(dropdown);
			select.selectByVisibleText(text);
			uiActionHandler.highlightElement(dropdown);
			UtilityHelper.takeFullScreenShots(
					UtilityHelper.generateStatements("select by visible text", variable, text)[0],
					UtilityHelper.generateStatements("select by visible text", variable, text)[1], ITestResult.SUCCESS);
			uiActionHandler.removeHighlightOfElement(dropdown);
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(
					UtilityHelper.generateStatements("select by visible text", variable, text)[0],
					UtilityHelper.generateStatements("select by visible text", variable, text)[2], ITestResult.FAILURE);
			uiActionHandler.removeHighlightOfElement(dropdown);
		}
	}

	/**
	 * Selects an option in a dropdown by index.
	 *
	 * @param dropdown the WebElement representing the dropdown.
	 * @param index    the index of the option to select.
	 * @param variable the name of the variable for logging.
	 * @throws Exception if an error occurs during the selection.
	 */

	public void selectByIndex(WebElement dropdown, int index, String variable) throws Exception {
		WaitUtils waitUtils = new WaitUtils(driver);
		waitUtils.hardWait(3000);
		waitUtils.waitForElementVisible(dropdown, 30);
		UIActionHandler uiActionHandler = new UIActionHandler(driver);
		try {
			Select select = new Select(dropdown);
			select.selectByIndex(index);
			uiActionHandler.highlightElement(dropdown);
			UtilityHelper.takeFullScreenShots(
					UtilityHelper.generateStatements("select by index", variable, Integer.toString(index))[0],
					UtilityHelper.generateStatements("select by index", variable, Integer.toString(index))[1],
					ITestResult.SUCCESS);
			uiActionHandler.removeHighlightOfElement(dropdown);
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(
					UtilityHelper.generateStatements("select by index", variable, Integer.toString(index))[0],
					UtilityHelper.generateStatements("select by index", variable, Integer.toString(index))[2],
					ITestResult.FAILURE);
			uiActionHandler.removeHighlightOfElement(dropdown);
		}
	}

	/**
	 * Selects an option in a dropdown by the value attribute.
	 *
	 * @param dropdown the WebElement representing the dropdown.
	 * @param value    the value attribute of the option to select.
	 * @param variable the name of the variable for logging.
	 * @throws Exception if an error occurs during the selection.
	 */

	public void selectByValue(WebElement dropdown, String value, String variable) throws Exception {
		WaitUtils waitUtils = new WaitUtils(driver);
		waitUtils.hardWait(3000);
		waitUtils.waitForElementVisible(dropdown, 30);
		UIActionHandler uiActionHandler = new UIActionHandler(driver);
		try {
			Select select = new Select(dropdown);
			select.selectByValue(value);
			uiActionHandler.highlightElement(dropdown);
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements("select by value", variable, value)[0],
					UtilityHelper.generateStatements("select by value", variable, value)[1], ITestResult.SUCCESS);
			uiActionHandler.removeHighlightOfElement(dropdown);
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements("select by value", variable, value)[0],
					UtilityHelper.generateStatements("select by value", variable, value)[2], ITestResult.FAILURE);
			uiActionHandler.removeHighlightOfElement(dropdown);
		}
	}

	/**
	 * Clicks a dropdown and selects an option by its WebElement.
	 *
	 * @param dropdown      the WebElement representing the dropdown.
	 * @param optionElement the WebElement representing the option to select.
	 * @param variable      the name of the variable for logging.
	 * @throws Exception if an error occurs during the action.
	 */

	public void clickAndSelectDropdownOption(WebElement dropdown, WebElement optionElement, String variable)
			throws Exception {
		WaitUtils waitUtils = new WaitUtils(driver);
		waitUtils.hardWait(3000);
		waitUtils.waitForElementVisible(dropdown, 30);
		String optionText = null;
		try {
			if (TEST_TYPE_WEB) {
				UIActionHandler uiActionHandler = new UIActionHandler(driver);
				uiActionHandler.scrollToElementView(dropdown);
				dropdown.click();
				waitUtils.hardWait(3000);
				optionText = optionElement.getText().trim();
				optionElement.click();
				uiActionHandler.highlightElement(dropdown);
				UtilityHelper.takeFullScreenShots(
						UtilityHelper.generateStatements("click and select", variable, optionText)[0],
						UtilityHelper.generateStatements("click and select", variable, optionText)[1],
						ITestResult.SUCCESS);
				uiActionHandler.removeHighlightOfElement(dropdown);
			} else {
				UIActionHandler uiActionHandler = new UIActionHandler(driver);
				uiActionHandler.scrollToMobileElement(dropdown);
				dropdown.click();
				waitUtils.hardWait(3000);
				optionText = optionElement.getText().trim();
				optionElement.click();
				UtilityHelper.takeFullScreenShots(
						UtilityHelper.generateStatements("click and select", variable, optionText)[0],
						UtilityHelper.generateStatements("click and select", variable, optionText)[1],
						ITestResult.SUCCESS, Optional.of(dropdown));
			}
		} catch (Exception e) {
			System.err.println(UtilityHelper.generateStatements("click and select", variable, optionText)[2]);

			if (TEST_TYPE_WEB) {
				UtilityHelper.takeFullScreenShots(
						UtilityHelper.generateStatements("click and select", variable, optionText)[0],
						UtilityHelper.generateStatements("click and select", variable, optionText)[2],
						ITestResult.FAILURE);
			} else {
				UtilityHelper.takeFullScreenShots(
						UtilityHelper.generateStatements("click and select", variable, optionText)[0],
						UtilityHelper.generateStatements("click and select", variable, optionText)[2],
						ITestResult.FAILURE, Optional.of(dropdown));
			}
		}
	}

	/**
	 * Gets text from the specified WebElement.
	 *
	 * @param element  The WebElement from which to extract text.
	 * @param variable Descriptive name of the element for logging.
	 * @return Text content of the specified WebElement.
	 * @throws Exception If an error occurs during element interaction.
	 */

	public String getElementText(WebElement element, String variable) throws Exception {
		WaitUtils waitUtils = new WaitUtils(driver);
		waitUtils.hardWait(3000);
		waitUtils.waitForElementVisible(element, 30);
		String eleTxt = null;
		try {
			UIActionHandler uiActionHandler = new UIActionHandler(driver);
			uiActionHandler.scrollToElementView(element);
			uiActionHandler.highlightElement(element);
			eleTxt = element.getText();
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements("get Element Text", variable, eleTxt)[0],
					UtilityHelper.generateStatements("get Element Text", variable, eleTxt)[1], ITestResult.SUCCESS);
			uiActionHandler.removeHighlightOfElement(element);
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements("get Element Text", variable, eleTxt)[0],
					UtilityHelper.generateStatements("get Element Text", variable, eleTxt)[2], ITestResult.FAILURE);
			throw e; // Rethrow exception after handling it
		}

		return eleTxt;
	}

	/**
	 * Retrieves a specific attribute from a WebElement.
	 *
	 * @param element   The WebElement from which to get the attribute.
	 * @param attribute The attribute name to retrieve.
	 * @param variable  Descriptive name of the element for logging.
	 * @return The attribute value of the specified WebElement.
	 * @throws Exception If an error occurs during element interaction.
	 */

	public String getElementAttribute(WebElement element, String attribute, String variable) throws Exception {
		WaitUtils waitUtils = new WaitUtils(driver);
		waitUtils.hardWait(3000);
		waitUtils.waitForElementVisible(element, 30);
		String eleAttribute = null;
		try {
			UIActionHandler uiActionHandler = new UIActionHandler(driver);
			uiActionHandler.scrollToElementView(element);
			uiActionHandler.highlightElement(element);
			eleAttribute = element.getAttribute(attribute);
			UtilityHelper.takeFullScreenShots(
					UtilityHelper.generateStatements("get Element Attribute", variable, attribute)[0],
					UtilityHelper.generateStatements("get Element Attribute", variable, attribute)[1],
					ITestResult.SUCCESS);
			uiActionHandler.removeHighlightOfElement(element);
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(
					UtilityHelper.generateStatements("get Element Attribute", variable, attribute)[0],
					UtilityHelper.generateStatements("get Element Attribute", variable, attribute)[2],
					ITestResult.FAILURE);
			throw e; // Rethrow exception after handling it
		}

		return eleAttribute;
	}

	/**
	 * Retrieves a CSS property value from a WebElement.
	 *
	 * @param element      The WebElement from which to get the CSS value.
	 * @param propertyName The name of the CSS property.
	 * @param variable     Descriptive name of the element for logging.
	 * @return The CSS value of the specified property.
	 * @throws Exception If an error occurs during element interaction.
	 */

	public String getElementCssValue(WebElement element, String propertyName, String variable) throws Exception {
		WaitUtils waitUtils = new WaitUtils(driver);
		waitUtils.hardWait(3000);
		waitUtils.waitForElementVisible(element, 30);
		String eleCssValue = null;
		try {
			UIActionHandler uiActionHandler = new UIActionHandler(driver);
			uiActionHandler.scrollToElementView(element);
			uiActionHandler.highlightElement(element);
			eleCssValue = element.getCssValue(propertyName);
			UtilityHelper.takeFullScreenShots(
					UtilityHelper.generateStatements("get Element CSS value", variable, propertyName)[0],
					UtilityHelper.generateStatements("get Element CSS value", variable, propertyName)[1],
					ITestResult.SUCCESS);
			uiActionHandler.removeHighlightOfElement(element);
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(
					UtilityHelper.generateStatements("get Element CSS value", variable, propertyName)[0],
					UtilityHelper.generateStatements("get Element CSS value", variable, propertyName)[2],
					ITestResult.FAILURE);
			throw e; // Rethrow exception after handling it
		}

		return eleCssValue;
	}

	/**
	 * Finds and returns a WebElement based on the provided locator.
	 *
	 * @param locator The locator used to find the WebElement.
	 * @return The found WebElement.
	 */

	public WebElement findElement(By locator) {
		return driver.findElement(locator);
	}

	/**
	 * Finds and returns a list of WebElements based on the provided locator.
	 *
	 * @param locator The locator used to find the WebElements.
	 * @return List of found WebElements.
	 */

	public List<WebElement> findElements(By locator) {
		return driver.findElements(locator);
	}

	/**
	 * Switches the context to the specified frame using its name or ID.
	 *
	 * @param frameName The name or ID of the frame to switch to.
	 * @throws Exception If an error occurs during the switch or frame is not found.
	 */

	public void switchToFrame(String frameName) throws Exception {
		WaitUtils waitUtils = new WaitUtils(driver);
		waitUtils.hardWait(3000);
		try {
			driver.switchTo().frame(frameName);
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements("switch to frame", frameName, null)[0],
					UtilityHelper.generateStatements("switch to frame", frameName, null)[1], ITestResult.SUCCESS);
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements("switch to frame", frameName, null)[0],
					UtilityHelper.generateStatements("switch to frame", frameName, null)[2], ITestResult.FAILURE);
			throw e;
		}
	}

	/**
	 * Switches the context to a window using its name or handle.
	 *
	 * @param windowName The name or handle of the window to switch to.
	 * @throws Exception If an error occurs during the switch or window is not
	 *                   found.
	 */

	public void switchToWindow(String windowName) throws Exception {
		WaitUtils waitUtils = new WaitUtils(driver);
		waitUtils.hardWait(3000);
		try {
			driver.switchTo().window(windowName);
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements("switch to window", windowName, null)[0],
					UtilityHelper.generateStatements("switch to window", windowName, null)[1], ITestResult.SUCCESS);
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements("switch to window", windowName, null)[0],
					UtilityHelper.generateStatements("switch to window", windowName, null)[2], ITestResult.FAILURE);
			throw e;
		}
	}

	/**
	 * Switches the context to a window using its name or handle.
	 *
	 * @param windowName The name or handle of the window to switch to.
	 * @throws Exception If an error occurs during the switch or window is not
	 *                   found.
	 */

	public void navigateToURL(String url) throws Exception {
		WaitUtils waitUtils = new WaitUtils(driver);
		waitUtils.waitForPageLoaded();
		try {
			driver.get(url);
			new WebResponseValidator(driver).verifyWebResponseStatus();

			if (driver.getCurrentUrl().equalsIgnoreCase(url)) {
				UtilityHelper.takeFullScreenShots("I expected to navigate to the URL: '" + url + "'",
						"I successfully navigated to the URL: '" + driver.getCurrentUrl() + "'", ITestResult.SUCCESS);
			} else {
				throw new Exception("Navigation to URL failed. Expected: " + url + ", Got: " + driver.getCurrentUrl());
			}
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots("I expected to navigate to the URL: '" + url + "'",
					"I successfully navigated to the URL: '" + driver.getCurrentUrl() + "'", ITestResult.SUCCESS);
			throw e;
		}
	}

	/**
	 * Navigates one item back in the browser's history.
	 *
	 * @throws Exception If navigation fails or the previous page is not accessible.
	 */

	public void navigateBack() throws Exception {
		String currentUrl = driver.getCurrentUrl();
		driver.navigate().back();
		new WaitUtils(driver).waitForPageLoaded();

		if (!currentUrl.equals(driver.getCurrentUrl())) {
			UtilityHelper.takeFullScreenShots("I expected to navigate back from URL: '" + currentUrl + "'",
					"I successfully navigated back to URL: '" + driver.getCurrentUrl() + "'", ITestResult.SUCCESS);
		} else {
			throw new Exception("Failed to navigate back from " + currentUrl);
		}
	}

	/**
	 * Navigates one item forward in the browser's history.
	 *
	 * @throws Exception If navigation fails or the next page is not accessible.
	 */

	public void navigateForward() throws Exception {
		String currentUrl = driver.getCurrentUrl();
		driver.navigate().forward();
		new WaitUtils(driver).waitForPageLoaded();

		if (!currentUrl.equals(driver.getCurrentUrl())) {
			UtilityHelper.takeFullScreenShots("I expected to navigate forward from URL: '" + currentUrl + "'",
					"I successfully navigated forward to URL: '" + driver.getCurrentUrl() + "'", ITestResult.SUCCESS);
		} else {
			throw new Exception("Failed to navigate forward from " + currentUrl);
		}
	}

	/**
	 * Refreshes the current page.
	 */

	public void refreshPage() {
		driver.navigate().refresh();
		new WaitUtils(driver).waitForPageLoaded();
	}

	/**
	 * Drags an element from a source location and drops it to a target location.
	 *
	 * @param source The source WebElement to drag.
	 * @param target The target WebElement where the source will be dropped.
	 * @throws Exception if there is an error during the operation.
	 */

	public void dragAndDrop(WebElement source, WebElement target) throws Exception {
		String action = "Drag and Drop";
		String variable = "From " + source.toString() + " to " + target.toString();
		UIActionHandler uiActionHandler = new UIActionHandler(driver);
		uiActionHandler.scrollToElementView(source);
		try {
			uiActionHandler.highlightElement(source);
			new Actions(driver).dragAndDrop(source, target).perform();
			uiActionHandler.highlightElement(target);
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[1], ITestResult.SUCCESS);
			uiActionHandler.removeHighlightOfElement(source);
			uiActionHandler.removeHighlightOfElement(target);
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[2], ITestResult.FAILURE);
			uiActionHandler.removeHighlightOfElement(source);
			uiActionHandler.removeHighlightOfElement(target);
			throw e;
		}
	}

	/**
	 * Moves the mouse from one element to another.
	 *
	 * @param fromElement The WebElement to move from.
	 * @param toElement   The WebElement to move to.
	 * @throws Exception if there is an error during the operation.
	 */

	public void moveToElement(WebElement fromElement, WebElement toElement) throws Exception {
		String action = "Move To Element";
		String variable = fromElement.toString() + " Element " + toElement.toString() + " Element";
		UIActionHandler uiActionHandler = new UIActionHandler(driver);
		uiActionHandler.scrollToElementView(fromElement);
		try {
			uiActionHandler.highlightElement(fromElement);
			Actions actions = new Actions(driver);
			actions.moveToElement(fromElement).pause(Duration.ofSeconds(1)).moveToElement(toElement).perform();
			uiActionHandler.highlightElement(toElement);
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[1], ITestResult.SUCCESS);
			uiActionHandler.removeHighlightOfElement(fromElement);
			uiActionHandler.removeHighlightOfElement(toElement);
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[2], ITestResult.FAILURE);
			uiActionHandler.removeHighlightOfElement(fromElement);
			uiActionHandler.removeHighlightOfElement(toElement);
			throw e;
		}
	}

	/**
	 * Performs a right-click on an element and selects an option from the context
	 * menu.
	 *
	 * @param element The WebElement to right-click on.
	 * @param option  The option to select from the context menu.
	 * @throws Exception if there is an error during the operation.
	 */

	public void rightClickAndSelect(WebElement element, WebElement menuOption, String variable) throws Exception {
		String action = "Right Click and Select";
		UIActionHandler uiActionHandler = new UIActionHandler(driver);
		uiActionHandler.scrollToElementView(element);
		try {
			uiActionHandler.highlightElement(element);
			Actions actions = new Actions(driver);
			actions.contextClick(element).perform();
			uiActionHandler.highlightElement(menuOption);
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[1], ITestResult.SUCCESS);
			menuOption.click();
			uiActionHandler.removeHighlightOfElement(element);
			uiActionHandler.removeHighlightOfElement(menuOption);
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[2], ITestResult.FAILURE);
			uiActionHandler.removeHighlightOfElement(element);
			uiActionHandler.removeHighlightOfElement(menuOption);
			throw e;
		}
	}

	/**
	 * Performs a double-click on the specified WebElement.
	 *
	 * @param element The WebElement to double-click on.
	 * @throws Exception if there is an error during the operation.
	 */

	public void doubleClick(WebElement element, String variable) throws Exception {
		String action = "Double Click";
		UIActionHandler uiActionHandler = new UIActionHandler(driver);
		uiActionHandler.scrollToElementView(element);
		try {
			uiActionHandler.highlightElement(element);
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[1], ITestResult.SUCCESS,
					Optional.of(element));
			uiActionHandler.scrollToElementView(element);
			new Actions(driver).doubleClick(element).perform();
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[2], ITestResult.FAILURE);
			uiActionHandler.removeHighlightOfElement(element);
			throw e;
		}
	}

	/**
	 * Toggles a checkbox or switch to the "On" state.
	 *
	 * @param element The WebElement representing the checkbox or switch.
	 * @throws Exception if there is an error during the operation.
	 */

	public void web_toggleOn(WebElement element) throws Exception {
		String action = "Toggle On";
		String variable = element.toString();
		UIActionHandler uiActionHandler = new UIActionHandler(driver);
		uiActionHandler.scrollToElementView(element);
		try {
			if (!element.isSelected()) {
				element.click();
			}

			uiActionHandler.highlightElement(element);
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[1], ITestResult.SUCCESS);
			uiActionHandler.removeHighlightOfElement(element);
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[2], ITestResult.FAILURE);
			uiActionHandler.removeHighlightOfElement(element);
			throw e;
		}
	}

	/**
	 * Toggles a checkbox or switch to the "Off" state.
	 *
	 * @param element The WebElement representing the checkbox or switch.
	 * @throws Exception if there is an error during the operation.
	 */

	public void toggleOff(WebElement element) throws Exception {
		String action = "Toggle Off";
		String variable = element.toString();
		UIActionHandler uiActionHandler = new UIActionHandler(driver);
		uiActionHandler.scrollToElementView(element);
		try {
			if (element.isSelected()) {
				element.click();
			}

			uiActionHandler.highlightElement(element);
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[1], ITestResult.SUCCESS);
			uiActionHandler.removeHighlightOfElement(element);
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[2], ITestResult.FAILURE);
			uiActionHandler.removeHighlightOfElement(element);
			throw e;
		}
	}

	/**
	 * Performs a long press on the specified WebElement.
	 *
	 * @param driver   The WebDriver instance.
	 * @param element  The WebElement to long press.
	 * @param duration The duration in milliseconds to hold the press.
	 * @throws Exception if there is an error during the operation.
	 */

	public void web_longPress(WebDriver driver, WebElement element, long duration) throws Exception {
		String action = "Long Press";
		String variable = element.toString();
		UIActionHandler uiActionHandler = new UIActionHandler(driver);
		uiActionHandler.scrollToElementView(element);
		try {
			uiActionHandler.highlightElement(element);
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[1], ITestResult.SUCCESS);
			uiActionHandler.removeHighlightOfElement(element);
			new Actions(driver).clickAndHold(element).pause(Duration.ofMillis(duration)).release().perform();
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[2], ITestResult.FAILURE);
			uiActionHandler.removeHighlightOfElement(element);
			throw e;
		}
	}

	/**
	 * Switches the context of the driver, typically between native app and web
	 * views.
	 *
	 * @param context The context to switch to (e.g., "WEBVIEW", "NATIVE_APP").
	 * @throws Exception If the desired context is not available or the switch
	 *                   fails.
	 */

	public void contextSwitching(String context) throws Exception {
		if (!(driver instanceof SupportsContextSwitching)) {
			throw new UnsupportedOperationException("Driver does not support context switching.");
		}

		SupportsContextSwitching contextDriver = (SupportsContextSwitching) driver;
		Set<String> availableContexts = contextDriver.getContextHandles();
		String currentContext = contextDriver.getContext();

		if (!currentContext.equals(context)) {
			if (availableContexts.contains(context)) {
				contextDriver.context(context);
				UtilityHelper.takeFullScreenShots("I expected to switch context to: '" + context + "'",
						"I successfully switched context to: '" + context + "'", ITestResult.SUCCESS);

			} else {
				UtilityHelper.takeFullScreenShots("I expected to switch context to: '" + context + "'",
						"Context not available: '" + context + "'", ITestResult.FAILURE);
				throw new Exception("Context not available: " + context);
			}
		}
	}

	// Mobile
	/**
	 * Pushes a file to the device.
	 *
	 * @param devicePath The path on the device where the file will be saved.
	 * @param localFile  The local file to be pushed to the device.
	 * @throws Exception if there is an error during the operation.
	 */

	public void mobile_pushFile(String devicePath, File localFile, String variable) throws Exception {
		String action = "Push File";
		try {
			AndroidDriver androidDriver = (AndroidDriver) UIConstantsUtil.WEB_DRIVER;
			androidDriver.pushFile(devicePath, localFile);
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[1], ITestResult.SUCCESS, Optional.empty());
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[2], ITestResult.FAILURE, Optional.empty());
			throw e;
		}
	}

	/**
	 * Pulls a file from the device.
	 *
	 * @param devicePath The path on the device from where the file will be pulled.
	 * @return The byte array of the file data.
	 * @throws Exception if there is an error during the operation.
	 */

	public byte[] mobile_pullFile(String devicePath, String variable) throws Exception {
		String action = "Pull File";
		byte[] fileData = null;
		try {
			AndroidDriver androidDriver = (AndroidDriver) UIConstantsUtil.WEB_DRIVER;
			fileData = androidDriver.pullFile(devicePath);
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[1], ITestResult.SUCCESS, Optional.empty());
			return fileData;
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[2], ITestResult.FAILURE, Optional.empty());
			throw e;
		}
	}

	/**
	 * Pulls a folder from the device.
	 *
	 * @param devicePath The directory path on the device from where the folder will
	 *                   be pulled.
	 * @return The byte array of the folder data.
	 * @throws Exception if there is an error during the operation.
	 */

	public byte[] mobile_pullFolder(String devicePath, String variable) throws Exception {
		String action = "Pull Folder";
		byte[] folderData = null;
		try {
			AndroidDriver androidDriver = (AndroidDriver) UIConstantsUtil.WEB_DRIVER;
			folderData = androidDriver.pullFolder(devicePath);
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[1], ITestResult.SUCCESS, Optional.empty());
			return folderData;
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[2], ITestResult.FAILURE, Optional.empty());
			throw e;
		}
	}

	/**
	 * Presses a key on the device.
	 *
	 * @param keycode The keycode to press.
	 * @throws Exception if there is an error during the operation.
	 */

	public void mobile_pressKeycode(int keycode) throws Exception {
		String action = "Press Keycode";
		String variable = String.valueOf(keycode);
		try {
			AndroidDriver androidDriver = (AndroidDriver) UIConstantsUtil.WEB_DRIVER;
			androidDriver.pressKey(new KeyEvent(AndroidKey.valueOf("KEYCODE_" + keycode))); // For Android
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[1], ITestResult.SUCCESS, Optional.empty());
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[2], ITestResult.FAILURE, Optional.empty());
			throw e;
		}
	}

	/**
	 * Long presses a key on the device.
	 *
	 * @param keycode The keycode to long press.
	 * @throws Exception if there is an error during the operation.
	 */

	public void mobile_longPressKeycode(int keycode) throws Exception {
		String action = "Long Press Keycode";
		String variable = String.valueOf(keycode);
		try {
			AndroidDriver androidDriver = (AndroidDriver) UIConstantsUtil.WEB_DRIVER;
			androidDriver.longPressKey(new KeyEvent(AndroidKey.valueOf("KEYCODE_" + keycode))); // For Android
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[1], ITestResult.SUCCESS, Optional.empty());
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[2], ITestResult.FAILURE, Optional.empty());
			throw e;
		}
	}

	/**
	 * Hides the keyboard if it is visible.
	 *
	 * @throws Exception if there is an error during the operation.
	 */

	public void mobile_hideKeyboard() throws Exception {
		String action = "Hide Keyboard";
		String variable = "Keyboard";
		try {
			AndroidDriver androidDriver = (AndroidDriver) UIConstantsUtil.WEB_DRIVER;
			androidDriver.hideKeyboard();
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[1], ITestResult.SUCCESS, Optional.empty());
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[2], ITestResult.FAILURE, Optional.empty());
			throw e;
		}
	}

	/**
	 * Checks if the keyboard is shown on the device.
	 *
	 * @return true if the keyboard is shown, false otherwise.
	 * @throws Exception if there is an error during the operation.
	 */

	public boolean mobile_isKeyboardShown() throws Exception {
		String action = "Check if Keyboard is Shown";
		String variable = "Keyboard";
		try {
			AndroidDriver androidDriver = (AndroidDriver) UIConstantsUtil.WEB_DRIVER;
			boolean isShown = androidDriver.isKeyboardShown();
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[1], ITestResult.SUCCESS, Optional.empty());
			return isShown;
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[2], ITestResult.FAILURE, Optional.empty());
			throw e;
		}
	}

	/**
	 * Performs a double click on the specified web element.
	 *
	 * @param element The web element to double click on.
	 * @throws Exception if there is an error during the operation.
	 */

	public void mobile_doubleClick(WebElement element, String variable) throws Exception {
		String action = "Double Click";
		try {
			new Actions(driver).doubleClick(element).perform();
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[1], ITestResult.SUCCESS,
					Optional.of(element));
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[2], ITestResult.FAILURE,
					Optional.of(element));
			throw e;
		}
	}

	/**
	 * Performs a button down action on the specified web element.
	 *
	 * @param element The web element to perform the button down on.
	 * @throws Exception if there is an error during the operation.
	 */

	public void mobile_buttonDown(WebElement element, String variable) throws Exception {
		String action = "Button Down";
		try {
			new Actions(driver).clickAndHold(element).perform();
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[1], ITestResult.SUCCESS,
					Optional.of(element));
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[2], ITestResult.FAILURE,
					Optional.of(element));
			throw e;
		}
	}

	/**
	 * Releases a held down button on the specified web element.
	 *
	 * @param element The web element to release the button on.
	 * @throws Exception if there is an error during the operation.
	 */

	public void mobile_buttonUp(WebElement element, String variable) throws Exception {
		String action = "Button Up";
		try {
			new Actions(driver).release(element).perform();
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[1], ITestResult.SUCCESS,
					Optional.of(element));
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[2], ITestResult.FAILURE,
					Optional.of(element));
			throw e;
		}
	}

	/**
	 * Performs a single tap on the specified mobile element using Appium's touch
	 * capabilities.
	 *
	 * @param element The mobile element to tap.
	 * @throws Exception if there is an error during the operation.
	 */

	public void mobile_singleTap(WebElement element, String variable) throws Exception {
		String action = "Single Tap";
		try {
			AndroidDriver driver = (AndroidDriver) UIConstantsUtil.WEB_DRIVER;
			PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
			org.openqa.selenium.interactions.Sequence tapSequence = new org.openqa.selenium.interactions.Sequence(
					finger, 0)
					.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(),
							element.getLocation().getX(), element.getLocation().getY()))
					.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
					.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
			driver.perform(Arrays.asList(tapSequence));
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[1], ITestResult.SUCCESS,
					Optional.of(element));
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[2], ITestResult.FAILURE,
					Optional.of(element));
			throw e;
		}
	}

	/**
	 * Performs a double tap on the specified mobile element using Appium's touch
	 * capabilities.
	 *
	 * @param element The mobile element to double tap.
	 * @throws Exception if there is an error during the operation.
	 */

	public void mobile_doubleTap(WebElement element, String variable) throws Exception {
		String action = "Double Tap";
		AndroidDriver driver = (AndroidDriver) UIConstantsUtil.WEB_DRIVER;
		try {
			PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
			int centerX = element.getLocation().getX() + element.getSize().getWidth() / 2;
			int centerY = element.getLocation().getY() + element.getSize().getHeight() / 2;
			org.openqa.selenium.interactions.Sequence doubleTapSequence = new org.openqa.selenium.interactions.Sequence(
					finger, 0)
					.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), centerX,
							centerY))
					.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
					.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()))
					.addAction(new Pause(finger, Duration.ofMillis(200)))
					.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
					.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
			driver.perform(Arrays.asList(doubleTapSequence));
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[1], ITestResult.SUCCESS,
					Optional.of(element));
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[2], ITestResult.FAILURE,
					Optional.of(element));
			throw e;
		}
	}

	/**
	 * Moves to the specified web element.
	 *
	 * @param element The web element to move to.
	 * @throws Exception if there is an error during the operation.
	 */

	public void mobile_move(WebElement element, String variable) throws Exception {
		String action = "Move";
		try {
			new Actions(driver).moveToElement(element).perform();
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[1], ITestResult.SUCCESS,
					Optional.of(element));
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[2], ITestResult.FAILURE,
					Optional.of(element));
			throw e;
		}
	}

	/**
	 * Simulates a touch down at the specified screen coordinates.
	 *
	 * @param x The x-coordinate on the screen where the touch down will occur.
	 * @param y The y-coordinate on the screen where the touch down will occur.
	 * @throws Exception if there is an error during the operation.
	 */

	public void mobile_touchDown(int x, int y) throws Exception {
		String action = "Touch Down";
		String variable = "Touch coordinates: X=" + x + " Y=" + y;
		AndroidDriver driver = (AndroidDriver) UIConstantsUtil.WEB_DRIVER;
		try {
			PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
			org.openqa.selenium.interactions.Sequence touchSequence = new org.openqa.selenium.interactions.Sequence(
					finger, 0);
			touchSequence
					.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), x, y));
			touchSequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
			driver.perform(Arrays.asList(touchSequence));
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[1], ITestResult.SUCCESS, Optional.empty());
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[2], ITestResult.FAILURE, Optional.empty());
			throw e;
		}
	}

	/**
	 * Simulates a touch up at the specified coordinates on the screen.
	 *
	 * @param x The x-coordinate for the touch up.
	 * @param y The y-coordinate for the touch up.
	 * @throws Exception if there is an error during the operation.
	 */

	public void mobile_touchUp(int x, int y) throws Exception {
		String action = "Touch Up";
		String variable = "Screen coordinates: " + x + ", " + y;
		AndroidDriver driver = (AndroidDriver) UIConstantsUtil.WEB_DRIVER;
		try {
			PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
			org.openqa.selenium.interactions.Sequence touchUp = new org.openqa.selenium.interactions.Sequence(finger,
					0);
			touchUp.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), x, y));
			touchUp.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
			driver.perform(Arrays.asList(touchUp));
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[1], ITestResult.SUCCESS, Optional.empty());
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[2], ITestResult.FAILURE, Optional.empty());
			throw e;
		}
	}

	/**
	 * Performs a long press on the specified mobile element.
	 *
	 * @param element The mobile element to long press.
	 * @throws Exception if there is an error during the operation.
	 */

	public void mobile_longPress(WebElement element) throws Exception {
		String action = "Long Press";
		String variable = element.toString();
		try {
			AndroidDriver driver = (AndroidDriver) UIConstantsUtil.WEB_DRIVER;
			PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
			int centerX = element.getLocation().getX() + element.getSize().getWidth() / 2;
			int centerY = element.getLocation().getY() + element.getSize().getHeight() / 2;
			org.openqa.selenium.interactions.Sequence longPressSequence = new org.openqa.selenium.interactions.Sequence(
					finger, 0)
					.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), centerX,
							centerY))
					.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
					.addAction(new Pause(finger, Duration.ofSeconds(1))) // Hold the press for one second
					.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
			driver.perform(Arrays.asList(longPressSequence));
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[1], ITestResult.SUCCESS,
					Optional.of(element));
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[2], ITestResult.FAILURE,
					Optional.of(element));
			throw e;
		}
	}

	/**
	 * Scrolls in the specified direction on the device's screen.
	 *
	 * @param direction The direction to scroll, e.g., "up", "down".
	 * @throws Exception if there is an error during the operation.
	 */

	public void mobile_scroll(String direction) throws Exception {
		String action = "Scroll";
		String variable = direction;
		AndroidDriver driver = (AndroidDriver) UIConstantsUtil.WEB_DRIVER;
		try {
			driver.executeScript("mobile: scroll", ImmutableMap.of("direction", direction));
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[1], ITestResult.SUCCESS, Optional.empty());
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[2], ITestResult.FAILURE, Optional.empty());
			throw e;
		}
	}

	/**
	 * Flicks on the device's screen from the start point to the end point.
	 *
	 * @param startX The starting x-coordinate.
	 * @param startY The starting y-coordinate.
	 * @param endX   The ending x-coordinate.
	 * @param endY   The ending y-coordinate.
	 * @throws Exception if there is an error during the operation.
	 */

	public void mobile_flick(int startX, int startY, int endX, int endY) throws Exception {
		String action = "Flick";
		String variable = "from (" + startX + ", " + startY + ") to (" + endX + ", " + endY + ")";
		try {
			AndroidDriver driver = (AndroidDriver) UIConstantsUtil.WEB_DRIVER;
			PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
			org.openqa.selenium.interactions.Sequence flickSequence = new org.openqa.selenium.interactions.Sequence(
					finger, 0)
					.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), startX,
							startY))
					.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
					.addAction(new Pause(finger, Duration.ofMillis(200))) // short delay to mimic human touch
					.addAction(finger.createPointerMove(Duration.ofMillis(200), PointerInput.Origin.viewport(), endX,
							endY))
					.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
			driver.perform(Arrays.asList(flickSequence));
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[1], ITestResult.SUCCESS, Optional.empty());
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[2], ITestResult.FAILURE, Optional.empty());
			throw e;
		}
	}

	/**
	 * Performs a multi-touch action involving multiple touch points.
	 *
	 * @param actions The list of individual TouchActions to perform simultaneously.
	 * @throws Exception if there is an error during the operation.
	 */

	public void mobile_multiTouchOnSingleElement(WebElement element) throws Exception {
		String action = "Multi Touch on Single Element";
		String variable = "Pinch/Zoom on element";
		AndroidDriver driver = (AndroidDriver) UIConstantsUtil.WEB_DRIVER;
		try {
			int centerX = element.getLocation().getX() + element.getSize().getWidth() / 2;
			int centerY = element.getLocation().getY() + element.getSize().getHeight() / 2;
			int startX1 = centerX - 50;
			int startY1 = centerY;
			int endX1 = centerX - 100;
			int endY1 = centerY;
			int startX2 = centerX + 50;
			int startY2 = centerY;
			int endX2 = centerX + 100;
			int endY2 = centerY;
			PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
			PointerInput finger2 = new PointerInput(PointerInput.Kind.TOUCH, "finger2");
			org.openqa.selenium.interactions.Sequence pinchIn1 = new org.openqa.selenium.interactions.Sequence(finger1,
					0).addAction(finger1.createPointerMove(Duration.ofMillis(0), Origin.viewport(), startX1, startY1))
					.addAction(finger1.createPointerDown(MouseButton.LEFT.asArg()))
					.addAction(new Pause(finger1, Duration.ofMillis(200))) // short delay to mimic human touch
					.addAction(finger1.createPointerMove(Duration.ofMillis(300), Origin.viewport(), endX1, endY1))
					.addAction(finger1.createPointerUp(MouseButton.LEFT.asArg()));
			org.openqa.selenium.interactions.Sequence pinchIn2 = new org.openqa.selenium.interactions.Sequence(finger2,
					1).addAction(finger2.createPointerMove(Duration.ofMillis(0), Origin.viewport(), startX2, startY2))
					.addAction(finger2.createPointerDown(MouseButton.LEFT.asArg()))
					.addAction(new Pause(finger2, Duration.ofMillis(200))) // short delay to mimic human touch
					.addAction(finger2.createPointerMove(Duration.ofMillis(300), Origin.viewport(), endX2, endY2))
					.addAction(finger2.createPointerUp(MouseButton.LEFT.asArg()));
			List<Sequence> multiTouch = Arrays.asList(pinchIn1, pinchIn2);
			driver.perform(multiTouch);
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[1], ITestResult.SUCCESS,
					Optional.of(element));
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[2], ITestResult.FAILURE,
					Optional.of(element));
			throw e;
		}
	}

	/**
	 * Performs a single touch action.
	 *
	 * @param action The TouchAction to perform.
	 * @throws Exception if there is an error during the operation.
	 */

	public void mobile_touchPerform(int x, int y) throws Exception {
		String actionName = "Touch Perform";
		String variable = "Single touch action at (" + x + ", " + y + ")";
		AndroidDriver driver = (AndroidDriver) UIConstantsUtil.WEB_DRIVER;
		try {
			PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
			Sequence touchSequence = new Sequence(finger, 0)
					.addAction(finger.createPointerMove(Duration.ofMillis(0), Origin.viewport(), x, y))
					.addAction(finger.createPointerDown(MouseButton.LEFT.asArg()))
					.addAction(new Pause(finger, Duration.ofMillis(200))) // short delay to mimic human touch
					.addAction(finger.createPointerUp(MouseButton.LEFT.asArg()));
			driver.perform(Arrays.asList(touchSequence));
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(actionName, variable, null)[0],
					UtilityHelper.generateStatements(actionName, variable, null)[1], ITestResult.SUCCESS,
					Optional.empty());
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(actionName, variable, null)[0],
					UtilityHelper.generateStatements(actionName, variable, null)[2], ITestResult.FAILURE,
					Optional.empty());
			throw e;
		}
	}

	/**
	 * Gets the current orientation of the device.
	 *
	 * @return The current orientation of the device.
	 * @throws Exception if there is an error during the operation.
	 */

	public ScreenOrientation mobile_getOrientation() throws Exception {
		String action = "Get Orientation";
		String variable = "Device orientation";
		AndroidDriver driver = (AndroidDriver) UIConstantsUtil.WEB_DRIVER;
		try {
			ScreenOrientation orientation = driver.getOrientation();
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[1], ITestResult.SUCCESS, Optional.empty());
			return orientation;
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[2], ITestResult.FAILURE, Optional.empty());
			throw e;
		}
	}

	/**
	 * Sets the orientation of the device.
	 *
	 * @param orientation The orientation to set (LANDSCAPE or PORTRAIT).
	 * @throws Exception if there is an error during the operation.
	 */

	public void mobile_setOrientation(ScreenOrientation orientation) throws Exception {
		String action = "Set Orientation";
		String variable = "Set to " + orientation;
		AndroidDriver driver = (AndroidDriver) UIConstantsUtil.WEB_DRIVER;
		try {
			driver.rotate(orientation);
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[1], ITestResult.SUCCESS, Optional.empty());
		} catch (Exception e) {
			UtilityHelper.takeFullScreenShots(UtilityHelper.generateStatements(action, variable, null)[0],
					UtilityHelper.generateStatements(action, variable, null)[2], ITestResult.FAILURE, Optional.empty());
			throw e;
		}
	}
}