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

  @search @dynamic_selection
  Scenario: Add "Ceiling Fans" to the shopping cart
    When I search for "Ceiling Fans" in the search box
    Then the search results should display automatically in a dropdown list
    When I select a random "Ceiling Fan" from the search results
    Then I am redirected to the "Ceiling Fan" listing page
    When I click on the selected "Ceiling Fan" description link
    Then I am taken to the product detail page
    And the "Ceiling Fan" name and price are verified on the product detail page
    And the "Ceiling Fan" image is verified
    When I click on the add to cart button
    Then "Ceiling Fan" should be added to the shopping cart
    When I am on the cart page
    Then "Ceiling Fan" should be listed in the cart with correct details

  @search @dynamic_selection
  Scenario: Add "Mixer Juicer Grinders" to the shopping cart
    When I search for "Mixer Juicer Grinders" in the search box
    Then the search results should display automatically in a dropdown list
    When I select a random "Mixer Juicer Grinder" from the search results
    Then I am redirected to the "Mixer Juicer Grinder" listing page
    When I click on the selected "Mixer Juicer Grinder" description link
    Then I am taken to the product detail page
    And the "Mixer Juicer Grinder" name and price are verified on the product detail page
    And the "Mixer Juicer Grinder" image is verified
    When I click on the add to cart button
    Then "Mixer Juicer Grinder" should be added to the shopping cart
    When I am on the cart page
    Then "Mixer Juicer Grinder" should be listed in the cart with correct details
