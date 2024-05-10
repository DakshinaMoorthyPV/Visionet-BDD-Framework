package com.visionetsystems.framework.stepdefinitions.Sprint1;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.visionetsystems.framework.pages.appium.MobileFlipkartPage;
import com.visionetsystems.framework.pages.interfaces.BasePage;
import com.visionetsystems.framework.pages.web.WebFlipkartPage;
import com.visionetsystems.framework.utils.GenericMethodsUtil;

public class NewTest {
	BasePage page;

	@BeforeClass
	public void setUp() {
		page = GenericMethodsUtil.initializePageObjects(WebFlipkartPage.class, MobileFlipkartPage.class);

	}

	// Test to verify navigation to homepage
	@Test
	public void navigateToHomePage() throws Exception {
		page.navigateToHomePage();
	}

	// Test to search for a product and verify the search results
	@Test(dependsOnMethods = { "navigateToHomePage" })
	public void searchAndVerifyProduct() throws Exception {
		String productName = "iPhone"; // Example product name, you can parameterize as needed
		page.searchForProduct(productName);
		page.verifySearchResultsVisibility();
	}

	// Test to select a product from the search results
	@Test(dependsOnMethods = { "searchAndVerifyProduct" })
	public void selectProductFromResults() throws Exception {
		page.selectRandomProduct(null);
	}
}
