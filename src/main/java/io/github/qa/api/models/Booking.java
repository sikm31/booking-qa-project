package io.github.qa.api.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record Booking(
        @JsonProperty("firstname") String firstName,
        @JsonProperty("lastname") String lastName,
        @JsonProperty("totalprice") Integer totalPrice,
        @JsonProperty("depositpaid") Boolean depositPaid,
        @JsonProperty("bookingdates") BookingDates bookingDates,
        @JsonProperty("additionalneeds") String additionalNeeds) {

    public Booking withTotalPrice(Integer value) {
        return new Booking(firstName, lastName, value, depositPaid, bookingDates, additionalNeeds);
    }

    public static Booking patch(String firstName, String lastName) {
        return new Booking(firstName, lastName, null, null, null, null);
    }
}
