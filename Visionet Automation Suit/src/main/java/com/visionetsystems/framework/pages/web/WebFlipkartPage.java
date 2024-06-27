package com.visionetsystems.framework.pages.web;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.ITestResult;

import com.github.javafaker.Faker;
import com.visionetsystems.framework.pagefactory.web.WebFlipkartPageFactory;
import com.visionetsystems.framework.pages.interfaces.BasePage;
import com.visionetsystems.framework.selenium.interactions.SeleniumActions;
import com.visionetsystems.framework.utils.UIActionHandler;
import com.visionetsystems.framework.utils.UIConstantsUtil;
import com.visionetsystems.framework.utils.URLConstants;
import com.visionetsystems.framework.utils.UtilityHelper;
import com.visionetsystems.framework.utils.WaitUtils;
import com.visionetsystems.framework.utils.WebResponseValidator;

public class WebFlipkartPage extends BasePage {
	protected static WebDriver driver;
	protected WebFlipkartPageFactory flipkartPageFactory;
	private static List<String> selectedProductName = new ArrayList<>();
	private static List<Integer> selectedProductPrice = new ArrayList<>();
	private static int getRandomSearchResultProductNum;

	public WebFlipkartPage() {
		super();
		driver = UIConstantsUtil.WEB_DRIVER;
		flipkartPageFactory = new WebFlipkartPageFactory();
	}

	@Override
	public void navigateToHomePage() throws Exception {
		new WaitUtils(driver).waitForPageLoaded();
		new WaitUtils(driver).hardWait(5000);
		// WebElement
		// getADemo=driver.findElement(RelativeLocator.with(By.cssSelector("")).above(flipkartPageFactory.inpSearchBox));
		if (driver.getCurrentUrl().equals(URLConstants.HomePage)) {
			UtilityHelper.takeFullScreenShots("I expect to navigate to the URL: '" + URLConstants.HomePage + "' ",
					"I actually navigated to the URL: '" + driver.getCurrentUrl() + "'", ITestResult.SUCCESS);
			new WebResponseValidator(driver).verifyWebResponseStatus();
			return;
		}
		new SeleniumActions(driver).navigateToURL(URLConstants.HomePage);
	}

	@Override
	public void searchForProduct(String productName) throws Exception {
		new SeleniumActions(driver).sendKeys(flipkartPageFactory.inpSearchBox, productName, "Search");
		new WaitUtils(driver).hardWait(2000);

	}

	@Override
	public void verifySearchResultsVisibility() throws Exception {
		new WaitUtils(driver).hardWait(3000);
		if (flipkartPageFactory.lstAutoPopulateResult.size() == 0) {
			UtilityHelper.takeFullScreenShots("I should see the auto search list with product",
					"I shouldn't see the auto search list with product", ITestResult.FAILURE);
			throw new Exception("Auto Search complete is not working");
		}
		int getRandomProductNum = new Random().nextInt(flipkartPageFactory.lstAutoPopulateResult.size());
		JavascriptExecutor js = (JavascriptExecutor) driver;
		String script = "return arguments[0].childNodes[0].nodeValue.trim();";
		String text = (String) js.executeScript(script, flipkartPageFactory.lstAutoPopulateResult
				.get(getRandomProductNum).findElement(By.cssSelector("div:nth-child(2")));
		String getRandomSelectedProductName = text;
		new SeleniumActions(driver).click(flipkartPageFactory.lstAutoPopulateResult.get(getRandomProductNum),
				getRandomSelectedProductName);
		new WaitUtils(driver).hardWait(2000);

		new WaitUtils(driver).waitForPageLoaded();
	}

	@Override
	public void selectRandomProduct(String productType) throws Exception {
		if (flipkartPageFactory.lstLandingPageResult.size() == 0) {
			UtilityHelper.takeFullScreenShots(
					"I expect to see selected category items listed on the search results page",
					"I found no items in the selected category on the search results page", ITestResult.FAILURE);
			throw new Exception("No items in the selected category on the search results page");
		}
		getRandomSearchResultProductNum = new Random().nextInt(flipkartPageFactory.lstLandingPageResult.size());
		WebElement ele = flipkartPageFactory.lstLandingPageResult.get(getRandomSearchResultProductNum);
		new UIActionHandler(driver).highlightElement(ele);
		UtilityHelper.takeFullScreenShots("I should select a random item from the search results page",
				"I have selected item number " + (getRandomSearchResultProductNum + 1) + " from the search results row",
				ITestResult.SUCCESS);
		new UIActionHandler(driver).removeHighlightOfElement(ele);
	}

	@Override
	public void verifyProductListingPage(String productType) throws Exception {
		WebElement ele = flipkartPageFactory.lstLandingPageResult.get(getRandomSearchResultProductNum);
		selectedProductName.add(new SeleniumActions(driver).getElementText(
				ele.findElement(By.cssSelector(" div[class$='row'] [class*='col-7-12']>div:nth-child(1)")),
				(getRandomSearchResultProductNum + 1) + " product name"));
		selectedProductPrice.add(convertToInteger(new SeleniumActions(driver).getElementText(
				ele.findElement(By.cssSelector(" [class*='col-5-12']>div:nth-child(1)>div>div:nth-child(1)[class]")),
				(getRandomSearchResultProductNum + 1) + " product price")));
	}

	@Override
	public void navigateToProductDetails() throws Exception {
		WebElement ele = flipkartPageFactory.lstLandingPageResult.get(getRandomSearchResultProductNum);
		new SeleniumActions(driver).click(ele, (getRandomSearchResultProductNum + 1) + " product");
		new WaitUtils(driver).hardWait(2000);
	}

	@Override
	public void verifyOnProductDetailPage() throws Exception {
		try {
			ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
			if (tabs.size() > 1) {
				driver.switchTo().window(tabs.get(1));
				WebElement productDetail = driver.findElement(By.cssSelector(
						"#container > div > div> div> div > div:nth-child(2) > div > ul > li:nth-child(1)>button"));
				if (productDetail.isDisplayed()) {
					new UIActionHandler(driver).highlightElement(productDetail);
					System.out.println("I expected to switch to the product details tab and did so successfully.");
					UtilityHelper.takeFullScreenShots("I expected to be on the product details page.",
							"I am successfully on the product details page.", ITestResult.SUCCESS);
					new UIActionHandler(driver).removeHighlightOfElement(productDetail);
				} else {
					System.out.println("I expected to find the product details, but they are not visible.");
					UtilityHelper.takeFullScreenShots("I expected to be on the product details page.",
							"Product details are not visible on the page.", ITestResult.FAILURE);
				}
			} else {
				System.out.println("I expected to switch to the product details tab, but it is not available.");
				UtilityHelper.takeFullScreenShots("I expected to be on the product details page.",
						"Product details tab is not available.", ITestResult.FAILURE);
			}
		} catch (Exception e) {
			System.out.println("An error occurred while trying to verify the product details page: " + e.getMessage());
			UtilityHelper.takeFullScreenShots("I expected to be on the product details page.",
					"An error occurred: " + e.getMessage(), ITestResult.FAILURE);
		}
	}

	@Override
	public void verifyProductDetailsOnDetailPage() throws Exception {
		String productName = normalizeString(new SeleniumActions(driver)
				.getElementText(flipkartPageFactory.lbltxtProductName, "detail page product name "));
		String coreProductName;
		if (productName.contains("(") && productName.lastIndexOf(")") == productName.length() - 1) {
			// Split at the last occurrence of '(' and take the first part
			coreProductName = productName.substring(0, productName.lastIndexOf("(")).trim();
		} else
			coreProductName = productName.trim();
		System.out.println(coreProductName.replaceAll("\\s", ""));

		boolean isMatchFound = selectedProductName.stream()
				.anyMatch(x -> x.replaceAll("\\s", "").equalsIgnoreCase(coreProductName.replaceAll("\\s", "")));

		String expectedMessage = "I expect the product name on the detail page to match one of my previously selected products.";
		String actualMessage = isMatchFound ? "I can see the matching product name on the product detail page."
				: "I cannot find the product name on the product detail page that matches my selection.";
		if (isMatchFound) {
			UtilityHelper.takeFullScreenShots(expectedMessage, actualMessage, ITestResult.SUCCESS);
		} else {
			UtilityHelper.takeFullScreenShots(expectedMessage, actualMessage, ITestResult.FAILURE);
			Assert.fail(actualMessage);
		}
		int productPrice = convertToInteger(new SeleniumActions(driver)
				.getElementText(flipkartPageFactory.lbltxtProductPrice, "detail page product price"));
		isMatchFound = selectedProductPrice.contains(productPrice);
		expectedMessage = "I expect the product price on the detail page to match one of the selected product prices.";
		actualMessage = isMatchFound
				? "I verified that the product price on the detail page matches one of the selected product prices."
				: "I verified that the product price on the detail page does not match any of the selected product prices.";
		if (isMatchFound) {
			UtilityHelper.takeFullScreenShots(expectedMessage, actualMessage, ITestResult.SUCCESS);
			Assert.assertTrue(isMatchFound, actualMessage);
		} else {
			UtilityHelper.takeFullScreenShots(expectedMessage, actualMessage, ITestResult.FAILURE);
			Assert.fail(actualMessage);
		}
	}

	public void verifyProductImagesOnDetailPage() throws Exception {
		List<WebElement> imageElements = flipkartPageFactory.imgProductDetailPage;
		String expectedDisplayMessage = "I expect all product images to be displayed on the detail page.";
		String expectedBrokenMessage = "I expect all product images on the detail page to load successfully.";
		Assert.assertFalse(imageElements.isEmpty(), "No product images found on the detail page.");
		for (WebElement image : imageElements) {
			boolean isImageDisplayed = image.isDisplayed();
			String actualDisplayMessage = isImageDisplayed ? "I can see the product image displayed on the detail page."
					: "I cannot see the product image on the detail page; it might be missing or hidden.";
			new UIActionHandler(driver).highlightElement(image);
			UtilityHelper.takeFullScreenShots(expectedDisplayMessage, actualDisplayMessage, ITestResult.SUCCESS);
			Assert.assertTrue(isImageDisplayed, actualDisplayMessage);
			new UIActionHandler(driver).highlightElement(image);
			String imageUrl = image.getAttribute("src");
			int httpResponseCode = getHttpResponseCode(imageUrl);
			boolean isImageBroken = httpResponseCode != 200;
			String actualBrokenMessage = !isImageBroken
					? "I verified that the product image on the detail page loads successfully."
					: "I verified that the product image on the detail page is broken; it does not load correctly.";
			UtilityHelper.takeFullScreenShots(expectedDisplayMessage, expectedBrokenMessage, ITestResult.FAILURE);
			Assert.assertFalse(isImageBroken, actualBrokenMessage);
			new UIActionHandler(driver).removeHighlightOfElement(image);
		}
	}

	@Override
	public void addToCart() throws Exception {
		new SeleniumActions(driver).sendKeys(flipkartPageFactory.inpDeliveryPincode,
				new Faker(CurrentLocaleFormatted()).address().zipCode(), "Delivery Pincode");
		new SeleniumActions(driver).click(flipkartPageFactory.lnkCheck, "check");
		new SeleniumActions(driver).click(flipkartPageFactory.btnAddtoCart, "addtocart");
		new WaitUtils(driver).waitForPageLoaded();
		new WaitUtils(driver).hardWait(5000);

	}

	@Override
	public void verifyProductInCart() throws Exception {
		int productCount = flipkartPageFactory.lstProductCount.size();
		for (WebElement element : flipkartPageFactory.lstProductCount) {
			new UIActionHandler(driver).highlightElement(element); // Assume UIActionHandler is a valid class
		}
		boolean match = selectedProductName.size() == productCount;
		String expectedMessage = "I expect the number of products in the cart (" + selectedProductName.size()
				+ ") to match the number of selected products (" + productCount + ").";
		String actualMessageSuccess = "I verified that the actual number of products in the cart matches the expected number.";
		String actualMessageFailure = "I verified that there is a mismatch in the number of products: expected "
				+ selectedProductName.size() + ", but found " + productCount + " in the cart.";
		if (match) {
			UtilityHelper.takeFullScreenShots(expectedMessage, actualMessageSuccess, ITestResult.SUCCESS);
			Assert.assertTrue(match, actualMessageSuccess);
		} else {
			UtilityHelper.takeFullScreenShots(expectedMessage, actualMessageFailure, ITestResult.FAILURE);
			Assert.fail(actualMessageFailure);
		}
		for (WebElement element : flipkartPageFactory.lstProductCount) {
			new UIActionHandler(driver).removeHighlightOfElement(element);
		}
	}

	@Override
	public void navigateToCartPage() throws Exception {
		int getTotalAmount = convertToInteger(
				new SeleniumActions(driver).getElementText(flipkartPageFactory.lbltxtTotalAmount, "Total amount"));
		int sumProductPrices = selectedProductPrice.stream().mapToInt(Integer::intValue).sum();
		String expectedMessage = "I expect the total amount displayed (" + getTotalAmount
				+ ") to match the sum of the selected product prices (" + sumProductPrices + ").";
		String actualMessageSuccess = "I verified that the actual total amount matches the sum of the selected product prices.";
		String actualMessageFailure = "I verified that there is a mismatch: the total amount displayed is "
				+ getTotalAmount + ", but the sum of the selected product prices is " + sumProductPrices + ".";
		if (getTotalAmount == sumProductPrices) {
			UtilityHelper.takeFullScreenShots(expectedMessage, actualMessageSuccess, ITestResult.SUCCESS);
			Assert.assertTrue(getTotalAmount == sumProductPrices, actualMessageSuccess);
		} else {
			UtilityHelper.takeFullScreenShots(expectedMessage, actualMessageFailure, ITestResult.FAILURE);
			Assert.fail(actualMessageFailure);
		}
	}

	@Override
	public void verifyAllProductsInCart() throws Exception {
		ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
		driver.close();
		driver.switchTo().window(tabs.get(0));
	}

	private static int convertToInteger(String amount) {
		String cleanedAmount = amount.replaceAll("[^\\d.]", "");
		double value = Double.parseDouble(cleanedAmount);
		return (int) value;
	}

	private String normalizeString(String input) {
		// Normalize the string by replacing non-breaking spaces and trimming
		return input.replace("&nbsp;", " ").replaceAll("\\s+", " ").trim();
	}

	private int getHttpResponseCode(String urlString) throws Exception {
		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();
		return connection.getResponseCode();
	}

	public Locale CurrentLocaleFormatted() {
		Locale currentLocale = Locale.getDefault();
		String language = currentLocale.getLanguage();
		String country = currentLocale.getCountry();
		return createLocaleFromLanguageAndCountry(language, country);
	}

	public Locale createLocaleFromLanguageAndCountry(String language, String country) {
		// Create a new Locale directly using language and country
		// If country is empty, it effectively defaults to just language
		return new Locale(language, country);
	}
}