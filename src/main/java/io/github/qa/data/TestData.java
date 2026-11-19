package io.github.qa.data;

import io.github.qa.api.models.Booking;
import io.github.qa.api.models.BookingDates;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;


public final class TestData {

    private static final ThreadLocal<Faker> FAKER = ThreadLocal.withInitial(Faker::new);

    private TestData() {
    }

    public static Customer customer() {
        Faker f = FAKER.get();
        return new Customer(f.name().firstName(), f.name().lastName(), f.address().zipCode());
    }

    public static Booking booking() {
        Faker f = FAKER.get();
        LocalDate checkin = LocalDate.now().plusDays(ThreadLocalRandom.current().nextInt(1, 60));
        return new Booking(
                f.name().firstName() + "-" + uniqueSuffix(),
                f.name().lastName(),
                ThreadLocalRandom.current().nextInt(50, 1000),
                true,
                new BookingDates(checkin.toString(), checkin.plusDays(3).toString()),
                "Breakfast");
    }

    public static String uniqueSuffix() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
