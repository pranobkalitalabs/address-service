# language: en
@regression @autocomplete
Feature: UK Postcode Autocomplete Engine
  As a frontend search bar component
  I want to fetch matching UK postcodes as the user types
  So that users can complete address forms faster

  Scenario: Autocomplete suggestions for partial postcode prefix
    Given the address service is online
    When I send a GET request to "/api/v1/address/uk/autocomplete" with query "SW1A"
    Then the response HTTP status should be 200
    And the JSON field "success" should be true
    And the JSON array "data" should contain item "SW1A 0AA"
    And the JSON array "data" should contain item "SW1A 1AA"
