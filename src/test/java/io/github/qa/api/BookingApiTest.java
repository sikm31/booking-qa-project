package io.github.qa.api;

import io.github.qa.api.models.Booking;
import io.github.qa.api.models.CreatedBooking;
import io.github.qa.data.TestData;
import io.github.qa.steps.BookingSteps;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Tag("api")
@Epic("Бронирования")
@Feature("CRUD /booking")
class BookingApiTest {

    private final BookingClient client = new BookingClient();
    private final BookingSteps steps = new BookingSteps();
    private final List<Integer> created = new ArrayList<>();

    @AfterEach
    void cleanUp() {
        created.forEach(steps::deleteBooking);
    }

    private CreatedBooking givenBooking() {
        CreatedBooking booking = steps.createBooking(TestData.booking());
        created.add(booking.bookingId());
        return booking;
    }

    @Test
    @Tag("smoke")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("POST создаёт бронирование, GET возвращает те же данные")
    void createAndGet() {
        Booking expected = TestData.booking();

        CreatedBooking created = steps.createBooking(expected);
        this.created.add(created.bookingId());

        assertThat(created.bookingId()).isPositive();
        Booking actual = client.get(created.bookingId())
                .then().statusCode(200)
                .extract().as(Booking.class);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Tag("regression")
    @DisplayName("PUT полностью заменяет бронирование и идемпотентен")
    void putReplacesAndIsIdempotent() {
        int id = givenBooking().bookingId();
        Booking replacement = TestData.booking().withTotalPrice(777);

        for (int i = 0; i < 2; i++) {
            client.update(id, replacement, steps.token()).then().statusCode(200);
        }

        assertThat(client.get(id).as(Booking.class)).isEqualTo(replacement);
    }

    @Test
    @Tag("regression")
    @DisplayName("PATCH меняет только переданные поля")
    void patchChangesOnlyGivenFields() {
        CreatedBooking original = givenBooking();
        String newName = "Patched-" + TestData.uniqueSuffix();

        client.partialUpdate(original.bookingId(), Booking.patch(newName, null), steps.token())
                .then().statusCode(200);

        Booking actual = client.get(original.bookingId()).as(Booking.class);
        assertThat(actual.firstName()).isEqualTo(newName);
        assertThat(actual).usingRecursiveComparison().ignoringFields("firstName")
                .isEqualTo(original.booking());
    }

    @Test
    @Tag("regression")
    @DisplayName("После DELETE бронирование недоступно (404)")
    void deleteRemovesBooking() {
        int id = givenBooking().bookingId();

        client.delete(id, steps.token()).then().statusCode(201);
        created.remove(Integer.valueOf(id));

        client.get(id).then().statusCode(404);
    }

    @Test
    @Tag("regression")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("DELETE без токена запрещён (403)")
    void deleteWithoutTokenIsForbidden() {
        int id = givenBooking().bookingId();

        client.delete(id, null).then().statusCode(403);
        client.get(id).then().statusCode(200);
    }
}
