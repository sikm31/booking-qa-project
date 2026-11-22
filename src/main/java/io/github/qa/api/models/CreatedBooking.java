package io.github.qa.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreatedBooking(@JsonProperty("bookingid") int bookingId, Booking booking) {
}
