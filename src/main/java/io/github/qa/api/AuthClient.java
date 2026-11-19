package io.github.qa.api;

import io.github.qa.api.models.AuthRequest;
import io.github.qa.api.models.AuthResponse;
import io.github.qa.core.Config;
import io.qameta.allure.Step;

public class AuthClient extends BaseClient {

    public AuthClient() {
        super();
    }

    public AuthClient(String baseUrl) {
        super(baseUrl);
    }

    @Step("Получить токен авторизации")
    public String token() {
        AuthResponse response = request()
                .body(new AuthRequest(Config.get("api.user"), Config.get("api.password")))
                .post("/auth")
                .then().statusCode(200)
                .extract().as(AuthResponse.class);
        if (response.token() == null) {
            throw new IllegalStateException("Авторизация не прошла: " + response.reason());
        }
        return response.token();
    }
}
