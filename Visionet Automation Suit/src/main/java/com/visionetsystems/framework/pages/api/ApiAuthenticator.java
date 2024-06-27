package com.visionetsystems.framework.pages.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.XML;
import org.testng.ITestResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.visionetsystems.framework.utils.TestReportUtility;
import com.visionetsystems.framework.utils.UIConstantsUtil;
import com.visionetsystems.framework.utils.UtilityHelper;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
public class ApiAuthenticator {

	public static Map<String, Object> getAccessToken(String url, String authType, Map<String, String> headers, String jsonBody) throws Exception {
        if (headers == null || headers.isEmpty()) {
            headers = getDefaultHeaders();
        }
        long startTime = System.currentTimeMillis();
        Response response;
        switch (authType) {
            case "Bearer Token":
                response = RestAssured.given()
                        .headers(headers)
                        .body(jsonBody != null ? jsonBody : "")
                        .when()
                        .post(url);
                break;
            case "Basic Auth":
                response = RestAssured.given()
                        .auth()
                        .preemptive()
                        .basic(headers.get("username"), headers.get("password"))
                        .headers(headers)
                        .body(jsonBody != null ? jsonBody : "")
                        .when()
                        .post(url);
                break;
            case "API Key":
                response = RestAssured.given()
                        .headers(headers)
                        .body(jsonBody != null ? jsonBody : "")
                        .when()
                        .post(url);
                break;
            case "OAuth 2.0":
                response = RestAssured.given()
                        .auth()
                        .oauth2(headers.get("access_token"))
                        .headers(headers)
                        .body(jsonBody != null ? jsonBody : "")
                        .when()
                        .post(url);
                break;
            default:
                throw new IllegalArgumentException("Unsupported authentication type: " + authType);
        }
        long endTime = System.currentTimeMillis();
        long responseTimeInMillis = endTime - startTime;
       String jsonBody1 = TestReportUtility.logApiResponse(url, "POST", TestReportUtility.prettyPrintJson(jsonBody), response.getStatusCode(),  getStatusMessage( response.getStatusCode()), headers.size(), responseTimeInMillis, TestReportUtility.prettyPrintJson(response.asString()), headers);
        Map<String, Object> tokenMap = new HashMap<>();
        if (response.jsonPath().get("access_token") != null) {
        	  parseResponse(response, tokenMap, "");
        	TestReportUtility.apilogTestStep(UIConstantsUtil.SCENARIO_TEST, "Expected to receive a valid access token", "Received the correct access token: "+ response.jsonPath().getString("access_token"),  UtilityHelper.convertStatusToLogStatus(ITestResult.SUCCESS).name(), jsonBody1);
            tokenMap.put("access_token", response.jsonPath().getString("access_token"));
        } else {
        	TestReportUtility.apilogTestStep(UIConstantsUtil.SCENARIO_TEST, "Expected to receive a valid access token", "Access token not found in the response",  UtilityHelper.convertStatusToLogStatus(ITestResult.FAILURE).name(), jsonBody1);

            tokenMap.put("error", "Access token not found in the response");
        }

        return tokenMap;
    }

    public static Map<String, Object> sendRequest(String url, String method, Map<String, String> headers, String jsonBody) throws Exception {
        if (headers == null || headers.isEmpty()) {
            headers = getDefaultHeaders();
        }

        RequestSpecification request = RestAssured.given().headers(headers);
        if (jsonBody != null) {
            request.body(jsonBody);
        }
        long startTime = System.currentTimeMillis();

        Response response;
        switch (method.toUpperCase()) {
            case "GET":
                response = request.get(url);
                break;
            case "POST":
                response = request.post(url);
                break;
            case "PUT":
                response = request.put(url);
                break;
            case "DELETE":
                response = request.delete(url);
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
        long endTime = System.currentTimeMillis();
        long responseTimeInMillis = endTime - startTime;
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("statusCode", response.getStatusCode());
        responseMap.put("responseTime", response.getTime());

        response.getHeaders().forEach(header -> responseMap.put("header_" + header.getName(), header.getValue()));

        String contentType = response.getHeader("Content-Type");
        responseMap.put("Content-Type", contentType);
        if (contentType != null) {
            if (contentType.contains("application/json")) {
                String jsonBody1 = TestReportUtility.logApiResponse(url, method, TestReportUtility.prettyPrintJson(jsonBody), response.getStatusCode(),  getStatusMessage( response.getStatusCode()), headers.size(), responseTimeInMillis, TestReportUtility.prettyPrintJson(response.asString()), headers);
            	TestReportUtility.apilogTestStep(UIConstantsUtil.SCENARIO_TEST, "Expected a successful response with the correct data structure.", "Received the data:"+url,  UtilityHelper.convertStatusToLogStatus(ITestResult.SUCCESS).name(), jsonBody1);
                parseResponse(response, responseMap, "");
            } else if (contentType.contains("application/xml") || contentType.contains("text/xml")) {
                String xmlBody = TestReportUtility.logApiResponse(url, method, TestReportUtility.formatXML(jsonBody), response.getStatusCode(),  getStatusMessage( response.getStatusCode()), headers.size(), responseTimeInMillis, TestReportUtility.prettyPrintJson(response.asString()), headers);
            	TestReportUtility.apilogTestStep(UIConstantsUtil.SCENARIO_TEST,"Expected a successful response with the correct data structure.", "Received the data:"+url,  UtilityHelper.convertStatusToLogStatus(ITestResult.SUCCESS).name(), xmlBody);
                parseXmlResponse(response, responseMap);
            } else if (contentType.contains("text/html")) {
                String htmlBody = TestReportUtility.logApiResponse(url, method, TestReportUtility.prettyPrintHtml(jsonBody), response.getStatusCode(),  getStatusMessage( response.getStatusCode()), headers.size(), responseTimeInMillis, TestReportUtility.prettyPrintJson(response.asString()), headers);
            	TestReportUtility.apilogTestStep(UIConstantsUtil.SCENARIO_TEST, "Expected a successful response with the correct data structure.", "Received the data:"+url,  UtilityHelper.convertStatusToLogStatus(ITestResult.SUCCESS).name(), htmlBody);

            }
        }

        return responseMap;
    }

   

private static HttpResponse fetchJson(String url) throws Exception {
    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpGet request = new HttpGet(url);
    HttpResponse httpResponse = new HttpResponse();
    long startTime = System.currentTimeMillis();

    try (CloseableHttpResponse response = httpClient.execute(request)) {
        long responseTime = System.currentTimeMillis() - startTime;
        httpResponse.setContent(EntityUtils.toString(response.getEntity()));
        httpResponse.setStatusCode(response.getStatusLine().getStatusCode());
        httpResponse.setResponseTime(responseTime);
    }
    return httpResponse;
}

public static Map<String, Object> validateJson(String jsonDataUrl, String schemaStr) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> validationMap = new HashMap<>();
    // Fetch JSON data
    HttpResponse jsonDataResponse = fetchJson(jsonDataUrl);

    // Check HTTP status code
    if (jsonDataResponse.getStatusCode() != 200) {
        throw new RuntimeException("Failed to fetch JSON data. HTTP status code: " + jsonDataResponse.getStatusCode());
    }

    // Check response time
    if (jsonDataResponse.getResponseTime() >= 1000) {
        throw new RuntimeException("Response time is equal to or longer than 1 second: " + jsonDataResponse.getResponseTime() + " ms");
    }

    // Parse the schema and data into JsonNode
    JsonNode schemaNode = mapper.readTree(schemaStr);
    JsonNode data = mapper.readTree(jsonDataResponse.getContent());

    // Get the JsonSchemaFactory
    JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
    JsonSchema schema = factory.getJsonSchema(schemaNode);

    // Validate the JSON data against the schema
    ProcessingReport report = schema.validate(data);

    if (!report.isSuccess()) {
        validationMap.put("isValid", false);
    }

    validationMap.put("isValid", true);

    return validationMap;
}


    private static Map<String, String> getDefaultHeaders() {
        Map<String, String> defaultHeaders = new HashMap<>();
        defaultHeaders.put("Content-Type", "application/json");
        return defaultHeaders;
    }

    private static void parseResponse(Response response, Map<String, Object> responseMap, String parentKey) {
        response.jsonPath().getMap("$").forEach((key, value) -> {
            String newKey = parentKey.isEmpty() ? (String) key : parentKey + "." + key;
            if (value instanceof Map) {
                parseResponseRecursive((Map<?, ?>) value, responseMap, newKey);
            } else if (value instanceof List) {
                parseResponseArray((List<?>) value, responseMap, newKey);
            } else {
                responseMap.put(newKey, value);
            }
        });
    }

    private static void parseResponseRecursive(Map<?, ?> map, Map<String, Object> responseMap, String parentKey) {
        map.forEach((key, value) -> {
            String newKey = parentKey.isEmpty() ? (String) key : parentKey + "." + key;
            if (value instanceof Map) {
                parseResponseRecursive((Map<?, ?>) value, responseMap, newKey);
            } else if (value instanceof List) {
                parseResponseArray((List<?>) value, responseMap, newKey);
            } else {
                responseMap.put(newKey, value);
            }
        });
    }

    private static void parseResponseArray(List<?> array, Map<String, Object> responseMap, String parentKey) {
        for (int i = 0; i < array.size(); i++) {
            Object element = array.get(i);
            String elementKey = parentKey + "[" + i + "]";
            if (element instanceof Map) {
                parseResponseRecursive((Map<?, ?>) element, responseMap, elementKey);
            } else {
                responseMap.put(elementKey, element);
            }
        }
    }


    

    private static void parseXmlResponse(Response response, Map<String, Object> responseMap) {
        String xmlContent = response.getBody().asString();
        Map<String, Object> xmlMap = XML.toJSONObject(xmlContent).toMap();
        parseResponseRecursive(xmlMap, responseMap, "");
    }
    private static String getStatusMessage(int statusCode) {
        switch (statusCode) {
            case 200:
                return "OK";
            case 201:
                return "Created";
            case 400:
                return "Bad Request";
            case 401:
                return "Unauthorized";
            case 403:
                return "Forbidden";
            case 404:
                return "Not Found";
            case 500:
                return "Server Error";
            case 502:
                return "Bad Gateway";
            case 503:
                return "Service Unavailable";
            default:
                return "Unknown Status";
        }
    }
}

