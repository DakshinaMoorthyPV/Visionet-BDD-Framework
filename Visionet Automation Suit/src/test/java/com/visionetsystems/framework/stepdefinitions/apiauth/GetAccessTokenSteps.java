package com.visionetsystems.framework.stepdefinitions.apiauth;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.Map;

import com.visionetsystems.framework.pages.api.ApiAuthenticator;

import static org.junit.Assert.assertNotNull;

public class GetAccessTokenSteps {
    private String url;
    private String authType;
    private Map<String, String> headers = new HashMap<>();
    private String jsonBody;
    private Map<String, Object> tokenMap;

    @Given("the API endpoint for auth is {string}")
    public void the_api_endpoint_for_auth_is(String url) {
        this.url = url;
    }

    @Given("authentication type is {string}")
    public void authentication_type_is(String authType) {
        this.authType = authType;
    }

    @Given("the auth headers are:")
    public void the_auth_headers_are(io.cucumber.datatable.DataTable dataTable) {
        dataTable.asMaps().forEach(map -> headers.putAll(map));
    }

    @Given("the JSON body for auth is:")
    public void the_json_body_for_auth_is(String jsonBody) {
        this.jsonBody = jsonBody;
    }

    @When("I request an access token")
    public void i_request_an_access_token() throws Exception {
        tokenMap = ApiAuthenticator.getAccessToken(url, authType, headers, jsonBody);
    }

    @Then("I should get an access token in the response")
    public void i_should_get_an_access_token_in_the_response() {
        assertNotNull(tokenMap.get("access_token"));
    }
}
