# language: en
@regression @premises @dropdown
Feature: Multi-Premise & Apartment Dropdown Resolution
  As a frontend UI developer
  I want to fetch all individual apartments and building premises for a UK postcode
  So that users can select their exact flat/building address from a dropdown

  Scenario: Fetch premise list for apartment building (HA9 7ES - Bluebell Apartments)
    Given the address service is online
    When I send a GET request to "/api/v1/address/uk/premises" with query param postcode "HA9 7ES"
    Then the response HTTP status should be 200
    And the JSON field "success" should be true
    And the JSON integer field "data.totalPremises" should be equal to 8
    And the JSON array "data.addresses" should contain formatted address "Flat 1, Bluebell Apartments, 12 Wembley Park Drive, Wembley, HA9 7ES"
    And the JSON array "data.addresses" should contain formatted address "Flat 5, Bluebell Apartments, 12 Wembley Park Drive, Wembley, HA9 7ES"

  Scenario: Fetch premise list for government street (SW1A 2AA - Downing Street)
    Given the address service is online
    When I send a GET request to "/api/v1/address/uk/premises/{postcode}" with path variable "SW1A 2AA"
    Then the response HTTP status should be 200
    And the JSON field "success" should be true
    And the JSON array "data.addresses" should contain formatted address "10 Downing Street, Westminster, London, SW1A 2AA"
