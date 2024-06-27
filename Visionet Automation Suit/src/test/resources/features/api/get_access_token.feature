#Author: dakshina.moorthy@visionet.com
Feature: Get Access Token

  Scenario: Get access token using Bearer Token
    Given the API endpoint for auth is "https://api.example.com/auth"
    And authentication type is "Bearer Token"
    And the auth headers are:
      | Authorization | Bearer your_token_here |
    And the JSON body for auth is:
      """
      {
        "username": "user",
        "password": "pass"
      }
      """
    When I request an access token
    Then I should get an access token in the response

  Scenario: Get access token using Basic Auth
    Given the API endpoint for auth is "https://api.example.com/auth"
    And authentication type is "Basic Auth"
    And the auth headers are:
      | username | user |
      | password | pass |
    When I request an access token
    Then I should get an access token in the response
