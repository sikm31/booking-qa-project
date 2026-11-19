package io.github.qa.api;

import io.github.qa.api.models.Booking;
import io.qameta.allure.Step;
import io.restassured.response.Response;


public class BookingClient extends BaseClient {

    public BookingClient() {
        super();
    }

    public BookingClient(String baseUrl) {
        super(baseUrl);
    }

    @Step("POST /booking")
    public Response create(Booking booking) {
        return request().body(booking).post("/booking");
    }

    @Step("GET /booking/{id}")
    public Response get(int id) {
        return request().get("/booking/{id}", id);
    }

    @Step("PUT /booking/{id}")
    public Response update(int id, Booking booking, String token) {
        return request().cookie("token", token).body(booking).put("/booking/{id}", id);
    }

    @Step("PATCH /booking/{id}")
    public Response partialUpdate(int id, Booking patch, String token) {
        return request().cookie("token", token).body(patch).patch("/booking/{id}", id);
    }

    @Step("DELETE /booking/{id}")
    public Response delete(int id, String token) {
        var spec = request();
        if (token != null) {
            spec.cookie("token", token);
        }
        return spec.delete("/booking/{id}", id);
    }
}
