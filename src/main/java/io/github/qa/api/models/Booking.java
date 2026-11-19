package io.github.qa.api.models;

import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record Booking(
        String firstname,
        String lastname,
        Integer totalprice,
        Boolean depositpaid,
        BookingDates bookingdates,
        String additionalneeds) {

    public Booking withFirstname(String value) {
        return new Booking(value, lastname, totalprice, depositpaid, bookingdates, additionalneeds);
    }

    public Booking withTotalprice(Integer value) {
        return new Booking(firstname, lastname, value, depositpaid, bookingdates, additionalneeds);
    }

    public static Booking patch(String firstname, String lastname) {
        return new Booking(firstname, lastname, null, null, null, null);
    }
}
