package com.visionetsystems.framework.stepdefinitions.apiauth;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.visionetsystems.framework.pages.api.ApiAuthenticator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SendRequestSteps {
    private String url;
    private String method;
    private Map<String, String> headers = new HashMap<>();
    private String jsonBody;
    private Map<String, Object> responseMap;

    @Given("the API endpoint is {string}")
    public void the_api_endpoint_is(String url) {
        this.url = url;
    }

    @Given("the HTTP method is {string}")
    public void the_http_method_is(String method) {
        this.method = method;
    }

    @Given("the request headers are:")
    public void the_request_headers_are(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> map = dataTable.asMap(String.class, String.class);
        headers.putAll(map);
    }

    @Given("the JSON body is:")
    public void the_json_body_is(String jsonBody) {
        this.jsonBody = jsonBody;
    }

    @When("I send the request")
    public void i_send_the_request() throws Exception {
        responseMap = ApiAuthenticator.sendRequest(url, method, headers, jsonBody);
    }

    @Then("I should get a response with status code {int}")
    public void i_should_get_a_response_with_status_code(int statusCode) {
        assertEquals("Expected and actual status codes do not match.", statusCode, Optional.ofNullable(responseMap.get("statusCode")).orElse(-1));
    }

    @Then("the response should contain key {string}")
    public void the_response_should_contain_key(String key) {
        assertTrue("Response does not contain expected key: " + key, responseMap.containsKey(key));
    }
}
