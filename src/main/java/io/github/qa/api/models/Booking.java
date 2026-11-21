package io.github.qa.api.models;

import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record Booking(
        String firstName,
        String lastName,
        Integer totalPrice,
        Boolean depositPaid,
        BookingDates bookingDates,
        String additionalNeeds) {

    public Booking withTotalPrice(Integer value) {
        return new Booking(firstName, lastName, value, depositPaid, bookingDates, additionalNeeds);
    }

    public static Booking patch(String firstName, String lastName) {
        return new Booking(firstName, lastName, null, null, null, null);
    }
}
