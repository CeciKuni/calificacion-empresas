package com.ckuniyoshi.utils;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import io.restassured.specification.RequestSpecification;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;

import java.util.Map;

public class RequestBuilder {

        public static Response sendPostRequest(String baseUrl, Map<String, String> headers, Object requestBody) {
                RequestSpecification requestSpec = new RequestSpecBuilder()
                                .setBaseUri(baseUrl)
                                .setContentType(ContentType.JSON)
                                .addHeaders(headers)
                                .build();

                return given()
                                .spec(requestSpec)
                                .body(requestBody)
                                .when()
                                .post();
        }

}
