package com.sharedframework.api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

public class RestAssuredClient {

    private String baseUri;
    private final Map<String, String> defaultHeaders;
    private String authToken;
    private boolean logRequests = false;
    private boolean logResponses = false;
    private int connectionTimeout = 30000;
    private int readTimeout = 60000;

    public RestAssuredClient(String baseUri) {
        this.baseUri = baseUri;
        this.defaultHeaders = new HashMap<>();
        this.defaultHeaders.put("Content-Type", "application/json");
        this.defaultHeaders.put("Accept", "application/json");
    }

    public RestAssuredClient withHeader(String name, String value) {
        this.defaultHeaders.put(name, value);
        return this;
    }

    public RestAssuredClient withHeaders(Map<String, String> headers) {
        this.defaultHeaders.putAll(headers);
        return this;
    }

    public void setAuthToken(String token) {
        this.authToken = token;
        this.defaultHeaders.put("Authorization", token);
    }

    public void setBearerAuth(String token) {
        this.authToken = token;
        this.defaultHeaders.put("Authorization", "Bearer " + token);
    }

    public RestAssuredClient withLogging(boolean logRequests, boolean logResponses) {
        this.logRequests = logRequests;
        this.logResponses = logResponses;
        return this;
    }

    public RestAssuredClient withTimeout(int connectionTimeoutMs, int readTimeoutMs) {
        this.connectionTimeout = connectionTimeoutMs;
        this.readTimeout = readTimeoutMs;
        return this;
    }

    private RequestSpecification buildRequest() {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setContentType(ContentType.JSON)
                .addHeaders(defaultHeaders);

        if (logRequests) {
            builder.log(LogDetail.ALL);
        }

        return builder.build();
    }

    public Response get(String path) {
        RequestSpecification spec = RestAssured.given().spec(buildRequest());
        if (logResponses) {
            spec = spec.log().all();
        }
        return spec.when().get(path).then().extract().response();
    }

    public Response get(String path, Map<String, ?> queryParams) {
        RequestSpecification spec = RestAssured.given().spec(buildRequest())
                .queryParams(queryParams);
        if (logResponses) {
            spec = spec.log().all();
        }
        return spec.when().get(path).then().extract().response();
    }

    public Response post(String path, Object body) {
        RequestSpecification spec = RestAssured.given().spec(buildRequest())
                .body(body);
        if (logResponses) {
            spec = spec.log().all();
        }
        return spec.when().post(path).then().extract().response();
    }

    public Response put(String path, Object body) {
        RequestSpecification spec = RestAssured.given().spec(buildRequest())
                .body(body);
        if (logResponses) {
            spec = spec.log().all();
        }
        return spec.when().put(path).then().extract().response();
    }

    public Response patch(String path, Object body) {
        RequestSpecification spec = RestAssured.given().spec(buildRequest())
                .body(body);
        if (logResponses) {
            spec = spec.log().all();
        }
        return spec.when().patch(path).then().extract().response();
    }

    public Response delete(String path) {
        RequestSpecification spec = RestAssured.given().spec(buildRequest());
        if (logResponses) {
            spec = spec.log().all();
        }
        return spec.when().delete(path).then().extract().response();
    }

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }
}
