package com.platform.address.bdd.stepdefs;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.hamcrest.Matchers;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AddressLookupStepDefs {

    private Response latestResponse;

    @Given("the address service is online")
    public void theAddressServiceIsOnline() {
        given()
            .when()
            .get("/actuator/health")
            .then()
            .statusCode(200);
    }

    @When("I send a GET request to {string} with query param postcode {string}")
    public void iSendAGetRequestWithQueryParamPostcode(String endpoint, String postcode) {
        latestResponse = given()
            .queryParam("postcode", postcode)
            .when()
            .get(endpoint);
    }

    @When("I send a GET request to {string} with path variable {string}")
    public void iSendAGetRequestWithPathVariable(String endpointTemplate, String postcode) {
        latestResponse = given()
            .pathParam("postcode", postcode)
            .when()
            .get(endpointTemplate);
    }

    @When("I send a GET request to {string} without parameters")
    public void iSendAGetRequestWithoutParameters(String endpoint) {
        latestResponse = given()
            .when()
            .get(endpoint);
    }

    @When("I send a GET request to {string} with query {string}")
    public void iSendAGetRequestWithQuery(String endpoint, String query) {
        latestResponse = given()
            .queryParam("query", query)
            .when()
            .get(endpoint);
    }

    @Then("the response HTTP status should be {int}")
    public void theResponseHttpStatusShouldBe(int expectedStatus) {
        latestResponse.then().statusCode(expectedStatus);
    }

    @And("the JSON field {string} should be true")
    public void theJsonFieldShouldBeTrue(String jsonPath) {
        latestResponse.then().body(jsonPath, equalTo(true));
    }

    @And("the JSON field {string} should be false")
    public void theJsonFieldShouldBeFalse(String jsonPath) {
        latestResponse.then().body(jsonPath, equalTo(false));
    }

    @And("the JSON field {string} should be {string}")
    public void theJsonFieldShouldBe(String jsonPath, String expectedValue) {
        latestResponse.then().body(jsonPath, equalTo(expectedValue));
    }

    @And("the JSON field {string} should contain {string}")
    public void theJsonFieldShouldContain(String jsonPath, String expectedSubstring) {
        latestResponse.then().body(jsonPath, containsString(expectedSubstring));
    }

    @And("the JSON float field {string} should be greater than {double}")
    public void theJsonFloatFieldShouldBeGreaterThan(String jsonPath, double threshold) {
        Float val = latestResponse.jsonPath().getFloat(jsonPath);
        assertThat(val != null && val.doubleValue() > threshold, is(true));
    }

    @And("the JSON float field {string} should be less than {double}")
    public void theJsonFloatFieldShouldBeLessThan(String jsonPath, double threshold) {
        Float val = latestResponse.jsonPath().getFloat(jsonPath);
        assertThat(val != null && val.doubleValue() < threshold, is(true));
    }

    @And("the JSON integer field {string} should be equal to {int}")
    public void theJsonIntegerFieldShouldBeEqualTo(String jsonPath, int expectedValue) {
        latestResponse.then().body(jsonPath, equalTo(expectedValue));
    }

    @And("the JSON array {string} should contain formatted address {string}")
    public void theJsonArrayShouldContainFormattedAddress(String jsonPath, String expectedAddress) {
        latestResponse.then().body(jsonPath + ".formattedAddress", hasItem(expectedAddress));
    }

    @And("the JSON array {string} should contain item {string}")
    public void theJsonArrayShouldContainItem(String jsonPath, String expectedItem) {
        List<String> items = latestResponse.jsonPath().getList(jsonPath);
        assertThat(items, Matchers.hasItem(expectedItem));
    }
}
