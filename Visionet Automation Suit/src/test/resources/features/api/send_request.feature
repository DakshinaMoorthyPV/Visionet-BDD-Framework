#Author: dakshina.moorthy@visionet.com
Feature: Send Request

  Scenario: Send GET request and receive JSON response
    Given the API endpoint is "https://pokeapi.co/api/v2/pokemon?limit=100&offset=200"
    And the HTTP method is "GET"
    And the request headers are:
      | Content-Type | application/json |
    When I send the request
    Then I should get a response with status code 200
    And the response should contain key "count"

  Scenario: Send POST request with JSON body and receive JSON response
    Given the API endpoint is "https://postman-echo.com/post"
    And the HTTP method is "POST"
    And the request headers are:
      | Content-Type    | application/json  |
      | Accept-Encoding | gzip, deflate, br |
      | Connection      | keep-alive        |
    And the JSON body is:
      """
      {
      "message": "oh hai!"
      }
      """
    When I send the request
    Then I should get a response with status code 200
    And the response should contain key "data.message"
