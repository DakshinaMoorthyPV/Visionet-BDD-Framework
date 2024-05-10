package com.visionetsystems.framework.stepdefinitions.Sprint1;

import com.visionetsystems.framework.pages.appium.MobileFlipkartPage;
import com.visionetsystems.framework.pages.interfaces.BasePage;
import com.visionetsystems.framework.pages.web.WebFlipkartPage;
import com.visionetsystems.framework.utils.GenericMethodsUtil;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class UserstoryID_ShoppingCartStepDefinitions {

	private BasePage page;

	public UserstoryID_ShoppingCartStepDefinitions() {
		// Initialize page objects dynamically based on the platform (Web or Mobile)
		page = GenericMethodsUtil.initializePageObjects(WebFlipkartPage.class, MobileFlipkartPage.class);
	}

	@Given("I am on the Flipkart homepage")
	public void i_am_on_the_flipkart_homepage() throws Exception {
		page.navigateToHomePage();
	}

	@When("I search for {string} in the search box")
	public void i_search_for_in_the_search_box(String productName) throws Exception {
		page.searchForProduct(productName);
	}

	@Then("the search results should display automatically in a dropdown list")
	public void the_search_results_should_display_automatically_in_a_dropdown_list() throws Exception {
		page.verifySearchResultsVisibility();
	}

	@When("I select a random {string} from the search results")
	public void i_select_a_random_product_from_the_search_results(String productType) throws Exception {
		page.selectRandomProduct(productType);
	}

	@Then("I am redirected to the {string} listing page")
	public void i_am_redirected_to_the_product_listing_page(String productType) throws Exception {
		page.verifyProductListingPage(productType);
	}

	@When("I click on the selected {string} description link")
	public void i_click_on_the_selected_product_description_link(String productType) throws Exception {
		page.navigateToProductDetails();
	}

	@When("I click on the selected product description link")
	public void i_click_on_the_selected_product_description_link() throws Exception {
		page.navigateToProductDetails();
	}

	@Then("I am taken to the product detail page")
	public void i_am_taken_to_the_product_detail_page() throws Exception {
		page.verifyOnProductDetailPage();
	}

	@And("the {string} name and price are verified on the product detail page")
	public void the_product_name_and_price_are_verified_on_the_product_detail_page(String productType)
			throws Exception {
		page.verifyProductDetailsOnDetailPage();
	}

	@And("the {string} image is verified")
	public void the_product_image_is_verified(String productType) throws Exception {
		page.verifyProductImageOnDetailPage();
	}

	@And("the product name and price are verified on the product detail page")
	public void the_product_name_and_price_are_verified_on_the_product_detail_page() throws Exception {
		page.verifyProductDetailsOnDetailPage();
	}

	@And("the product image is verified")
	public void the_product_image_is_verified() throws Exception {
		page.verifyProductImageOnDetailPage();
	}

	@When("I click on the add to cart button")
	public void i_click_on_the_add_to_cart_button() throws Exception {
		page.addToCart();
	}

	@Then("{string} should be added to the shopping cart")
	public void the_product_should_be_added_to_the_shopping_cart(String productType) throws Exception {
		page.verifyProductInCart();
	}

	@Then("the product should be added to the shopping cart")
	public void the_product_should_be_added_to_the_shopping_cart() throws Exception {
		page.verifyProductInCart();
	}

	@When("I am on the cart page")
	public void i_am_on_the_cart_page() throws Exception {
		page.navigateToCartPage();
	}

	@Then("all selected products should be listed in the cart with correct details")
	public void all_selected_products_should_be_listed_in_the_cart_with_correct_details() throws Exception {
		page.verifyAllProductsInCart();
	}

	@Then("{string} should be listed in the cart with correct details")
	public void all_selected_products_should_be_listed_in_the_cart_with_correct_details(String productType)
			throws Exception {
		page.verifyAllProductsInCart();
	}
}
