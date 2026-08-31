# language: en
@regression @validation
Feature: UK Postcode Validation Engine
  As a consuming client microservice (e.g. Auth Service)
  I want to validate UK postcode strings via Query Parameters and Path Variables
  So that I can reject invalid postal inputs before creating accounts or orders

  Scenario Outline: Validate active UK postcodes via query parameters
    Given the address service is online
    When I send a GET request to "/api/v1/address/uk/validate" with query param postcode "<postcode>"
    Then the response HTTP status should be 200
    And the JSON field "success" should be true
    And the JSON field "data" should be true

    Examples:
      | postcode |
      | SW1A 2AA |
      | EC1A 1BB |
      | W1A 1AA  |

  Scenario Outline: Validate active UK postcodes via path variables
    Given the address service is online
    When I send a GET request to "/api/v1/address/uk/validate/{postcode}" with path variable "<postcode>"
    Then the response HTTP status should be 200
    And the JSON field "success" should be true
    And the JSON field "data" should be true

    Examples:
      | postcode |
      | SW1A 2AA |
      | EC1A 1BB |

  Scenario: Validate an invalid UK postcode format
    Given the address service is online
    When I send a GET request to "/api/v1/address/uk/validate" with query param postcode "INVALID999"
    Then the response HTTP status should be 200
    And the JSON field "data" should be false
