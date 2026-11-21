package io.github.qa.api.models;

public record BookingDates(String checkin, String checkout) {
    public Booking withFirstname(String value, Booking booking) {
        return new Booking(value, booking.lastName(), booking.totalPrice(), booking.depositPaid(), this, booking.additionalNeeds());
    }
}
