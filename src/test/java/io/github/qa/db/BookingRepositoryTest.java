package io.github.qa.db;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("db")
@Epic("Бронирования")
@Feature("Хранилище (Testcontainers)")
@Testcontainers(disabledWithoutDocker = true)
class BookingRepositoryTest {

    @Container
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine");

    @BeforeAll
    static void createSchema() throws SQLException {
        try (Connection c = connect(); Statement s = c.createStatement()) {
            s.execute("""
                    CREATE TABLE booking (
                        id         SERIAL PRIMARY KEY,
                        firstname  VARCHAR(100) NOT NULL,
                        totalprice INT NOT NULL CHECK (totalprice >= 0)
                    )""");
        }
    }

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
    }

    @Test
    @DisplayName("Сохранённое бронирование читается из БД")
    void savedBookingCanBeRead() throws SQLException {
        try (Connection c = connect();
             PreparedStatement insert = c.prepareStatement(
                     "INSERT INTO booking(firstname, totalprice) VALUES (?, ?) RETURNING id")) {
            insert.setString(1, "Ali");
            insert.setInt(2, 500);
            ResultSet rs = insert.executeQuery();
            rs.next();
            int id = rs.getInt(1);

            ResultSet row = c.createStatement().executeQuery("SELECT firstname, totalprice FROM booking WHERE id = " + id);
            assertThat(row.next()).isTrue();
            assertThat(row.getString("firstname")).isEqualTo("Ali");
            assertThat(row.getInt("totalprice")).isEqualTo(500);
        }
    }

    @Test
    @DisplayName("Отрицательная цена отклоняется ограничением CHECK")
    void negativePriceIsRejected() {
        assertThatThrownBy(() -> {
            try (Connection c = connect(); Statement s = c.createStatement()) {
                s.execute("INSERT INTO booking(firstname, totalprice) VALUES ('Bad', -1)");
            }
        }).isInstanceOf(SQLException.class).hasMessageContaining("check constraint");
    }
}
