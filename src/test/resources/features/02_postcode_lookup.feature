# language: en
@regression @lookup @geocoding
Feature: UK Postcode Geocoding & Regional Lookup
  As a consuming client microservice
  I want to resolve geolocation coordinates, country, region, and district for a UK postcode
  So that I can enrich user profiles and map deliveries accurately

  Scenario Outline: Resolve geocoding coordinates for valid UK postcodes
    Given the address service is online
    When I send a GET request to "/api/v1/address/uk/lookup" with query param postcode "<postcode>"
    Then the response HTTP status should be 200
    And the JSON field "success" should be true
    And the JSON field "data.postcode" should be "<expectedPostcode>"
    And the JSON field "data.country" should be "England"
    And the JSON field "data.adminDistrict" should be "<district>"
    And the JSON float field "data.latitude" should be greater than 50.0
    And the JSON float field "data.longitude" should be less than 1.0

    Examples:
      | postcode | expectedPostcode | district    |
      | SW1A 2AA | SW1A 2AA         | Westminster |
      | HA9 7ES  | HA9 7ES          | Brent       |

  Scenario: Attempt lookup with missing postcode query parameter
    Given the address service is online
    When I send a GET request to "/api/v1/address/uk/lookup" without parameters
    Then the response HTTP status should be 400
    And the JSON field "success" should be false
    And the JSON field "message" should contain "Postcode is required"
