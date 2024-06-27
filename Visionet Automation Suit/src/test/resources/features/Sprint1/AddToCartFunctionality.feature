#Author: dakshina.moorthy@visionet.com
#Keywords Summary: Add to Cart, Search Products, Product Verification, Random Selection, Flipkart, E-commerce, Web and Mobile Testing
#Feature: Manage shopping cart on Flipkart by adding multiple products

Feature: Add multiple products to shopping cart on Flipkart

  Background:
    Given I am on the Flipkart homepage

  @search @dynamic_selection
  Scenario: Add "Mobile Phones" to the shopping cart
    When I search for "Mobile Phones" in the search box
    Then the search results should display automatically in a dropdown list
    When I select a random "Mobile Phone" from the search results
    Then I am redirected to the "Mobile Phone" listing page
    When I click on the selected "Mobile Phone" description link
    Then I am taken to the product detail page
    And the "Mobile Phone" name and price are verified on the product detail page
    And the "Mobile Phone" image is verified
    When I click on the add to cart button
    Then "Mobile Phone" should be added to the shopping cart
    When I am on the cart page
    Then "Mobile Phone" should be listed in the cart with correct details

 
