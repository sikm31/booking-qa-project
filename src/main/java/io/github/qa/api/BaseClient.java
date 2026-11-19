package io.github.qa.api;

import io.github.qa.core.Config;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;


public abstract class BaseClient {

    private final String baseUrl;

    protected BaseClient() {
        this(Config.apiBaseUrl());
    }

    protected BaseClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    protected RequestSpecification request() {
        int timeout = Config.timeoutMs();
        return given()
                .spec(new RequestSpecBuilder()
                        .setBaseUri(baseUrl)
                        .setContentType(ContentType.JSON)
                        .setAccept("application/json")
                        .setConfig(RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig()
                                .setParam("http.connection.timeout", timeout)
                                .setParam("http.socket.timeout", timeout)))
                        .addFilter(new AllureRestAssured())
                        .addFilter(new ErrorLoggingFilter())
                        .build());
    }
}
