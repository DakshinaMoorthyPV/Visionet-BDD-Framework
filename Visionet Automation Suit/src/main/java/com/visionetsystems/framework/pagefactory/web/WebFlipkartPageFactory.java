package com.visionetsystems.framework.pagefactory.web;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

import com.visionetsystems.framework.utils.UIConstantsUtil;

public class WebFlipkartPageFactory {
	private WebDriver driver;

	@FindBy(how = How.CSS, using = "[name*=q]")
	public WebElement inpSearchBox;

	@FindBy(how = How.CSS, using = "ul[class$='3ofZy1']>li [class='oleBil']")
	public List<WebElement> lstAutoPopulateResult;

	@FindBy(how = How.CSS, using = "#container > div > div > div > div:nth-child(2) > div[class*='col']:not([style]):not(:last-child) div[data-id]")
	public List<WebElement> lstLandingPageResult;

	@FindBy(how = How.CSS, using = "#container div:nth-child(1) > h1 > span")
	public WebElement lbltxtProductName;

	@FindBy(how = How.CSS, using = "#container > div > div > div > div.col-8-12 > div:nth-child(2)>div>div>div>div>div:nth-child(1)")
	public WebElement lbltxtProductPrice;

	@FindBy(how = How.CSS, using = "#container div[class*='col-5-12']  div> img")
	public List<WebElement> imgProductDetailPage;

	@FindBy(how = How.ID, using = "pincodeInputId")
	public WebElement inpDeliveryPincode;

	@FindBy(how = How.XPATH, using = ".//*[@id=\"container\"]//div[contains(concat(\" \", normalize-space(@class), \" \"), \" col-8-12 \")]//div/span[text()=\"Check\"]")
	public WebElement lnkCheck;

	@FindBy(how = How.CSS, using = "[class='row']>li>button")
	public WebElement btnAddtoCart;

	@FindBy(how = How.CSS, using = "#container div div[class*='col-12-12']>div>div>a")
	public List<WebElement> lstProductCount;

	@FindBy(how = How.CSS, using = "#container  div[class*='col-12-12']  div> span > div > div > div > span")
	public WebElement lbltxtTotalAmount;

	public WebFlipkartPageFactory() {
		this.driver = UIConstantsUtil.WEB_DRIVER;
		try {
			PageFactory.initElements(driver, this);
		} catch (Exception e) {
			System.err.println("Error initializing web elements: " + e.getMessage());
			// Optionally, rethrow or handle the exception as necessary
		}
	}

}