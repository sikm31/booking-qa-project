package io.github.qa.steps;

import io.github.qa.api.AuthClient;
import io.github.qa.api.BookingClient;
import io.github.qa.api.models.Booking;
import io.github.qa.api.models.CreatedBooking;
import io.qameta.allure.Step;

public class BookingSteps {

    private final BookingClient bookings = new BookingClient();
    private final AuthClient auth = new AuthClient();
    private String token;

    @Step("Создать бронирование")
    public CreatedBooking createBooking(Booking booking) {
        return bookings.create(booking)
                .then().statusCode(200)
                .extract().as(CreatedBooking.class);
    }

    @Step("Удалить бронирование {id} (очистка)")
    public void deleteBooking(int id) {
        bookings.delete(id, token());
    }

    public String token() {
        if (token == null) {
            token = auth.token();
        }
        return token;
    }
}
