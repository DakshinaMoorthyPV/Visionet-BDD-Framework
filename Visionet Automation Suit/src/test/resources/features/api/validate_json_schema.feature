#Author: dakshina.moorthy@visionet.com
Feature: Validate JSON Schema

  Scenario: Validate JSON response schema
    Given the API endpoint for schema validation is "https://api.example.com/resource"
    And authentication type is "Bearer Token"
    And the validation headers are:
      | Authorization | Bearer your_token_here |
    And the JSON body for validation is:
      """
      {
        "key": "value"
      }
      """
    And the JSON schema is:
      """
      {
        "$schema": "http://json-schema.org/draft-07/schema#",
        "type": "object",
        "properties": {
          "key": {
            "type": "string"
          }
        },
        "required": ["key"]
      }
      """
    When I validate the JSON response schema
    Then the validation should be successful