package io.github.qa.mock;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.github.qa.api.BookingClient;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("mock")
@Epic("Бронирования")
@Feature("Клиент API (на моках)")
class BookingClientMockTest {

    private WireMockServer wireMock;
    private BookingClient client;

    @BeforeEach
    void startMock() {
        wireMock = new WireMockServer(wireMockConfig().dynamicPort());
        wireMock.start();
        client = new BookingClient(wireMock.baseUrl());
    }

    @AfterEach
    void stopMock() {
        wireMock.stop();
    }

    @Test
    @Tag("smoke")
    @DisplayName("Клиент передаёт токен в cookie при удалении")
    void deleteSendsTokenCookie() {
        wireMock.stubFor(delete("/booking/42").willReturn(aResponse().withStatus(201)));

        client.delete(42, "secret-token");

        wireMock.verify(deleteRequestedFor(urlEqualTo("/booking/42"))
                .withCookie("token", equalTo("secret-token")));
    }

    @Test
    @DisplayName("Ответ 503 от сервиса доходит до теста без потери статуса и тела")
    void serviceUnavailableIsReported() {
        wireMock.stubFor(get("/booking/1").willReturn(aResponse()
                .withStatus(503)
                .withHeader("Retry-After", "30")
                .withBody("Service Unavailable")));

        var response = client.get(1);

        assertThat(response.statusCode()).isEqualTo(503);
        assertThat(response.header("Retry-After")).isEqualTo("30");
        assertThat(response.asString()).isEqualTo("Service Unavailable");
    }
}
