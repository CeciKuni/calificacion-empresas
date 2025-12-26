package com.ckuniyoshi.utils.Responses;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class BaseResponseBody {

    public final Response response;

    public BaseResponseBody(Response response) {
        this.response = response;
    }

    public JsonPath findInResponse() {
        return new JsonPath(getResponse());
    }

    public String getResponse() {
        return response.getBody().asPrettyString();
    }

    public int getStatusCode() {
        return response.getStatusCode();
    }

}
