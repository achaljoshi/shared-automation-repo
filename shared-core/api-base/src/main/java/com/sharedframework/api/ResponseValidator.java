package com.sharedframework.api;

import io.restassured.response.Response;

import static org.junit.Assert.*;

public class ResponseValidator {

    private ResponseValidator() {
        // Utility class
    }

    public static void validateStatusCode(Response response, int expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();
        assertEquals(
                String.format("Expected status code %d but got %d. Response body: %s",
                        expectedStatusCode, actualStatusCode, response.getBody().asString()),
                expectedStatusCode,
                actualStatusCode
        );
    }

    public static void validateJsonField(Response response, String jsonPath, Object expectedValue) {
        Object actualValue = response.jsonPath().get(jsonPath);
        assertNotNull(
                String.format("JSON path '%s' returned null. Response body: %s", jsonPath, response.getBody().asString()),
                actualValue
        );
        assertEquals(
                String.format("JSON path '%s' expected '%s' but got '%s'", jsonPath, expectedValue, actualValue),
                expectedValue,
                actualValue
        );
    }

    public static void validateResponseTime(Response response, long maxMilliseconds) {
        long actualTime = response.getTime();
        assertTrue(
                String.format("Response time %dms exceeded maximum allowed %dms", actualTime, maxMilliseconds),
                actualTime <= maxMilliseconds
        );
    }

    public static void validateNotEmpty(Response response) {
        String body = response.getBody().asString();
        assertNotNull("Response body is null", body);
        assertFalse("Response body is empty", body.trim().isEmpty());
    }

    public static <T> T extractField(Response response, String jsonPath) {
        T value = response.jsonPath().get(jsonPath);
        assertNotNull(
                String.format("Could not extract field at JSON path '%s' from response: %s",
                        jsonPath, response.getBody().asString()),
                value
        );
        return value;
    }

    public static String extractStringField(Response response, String jsonPath) {
        return extractField(response, jsonPath);
    }

    public static void validateJsonFieldExists(Response response, String jsonPath) {
        Object value = response.jsonPath().get(jsonPath);
        assertNotNull(
                String.format("JSON path '%s' does not exist in response: %s", jsonPath, response.getBody().asString()),
                value
        );
    }

    public static void validateJsonFieldContains(Response response, String jsonPath, String expectedSubstring) {
        String actualValue = extractStringField(response, jsonPath);
        assertTrue(
                String.format("JSON path '%s' value '%s' does not contain '%s'", jsonPath, actualValue, expectedSubstring),
                actualValue.contains(expectedSubstring)
        );
    }

    public static void validateContentType(Response response, String expectedContentType) {
        String actualContentType = response.getContentType();
        assertTrue(
                String.format("Expected content type containing '%s' but got '%s'", expectedContentType, actualContentType),
                actualContentType != null && actualContentType.contains(expectedContentType)
        );
    }
}
