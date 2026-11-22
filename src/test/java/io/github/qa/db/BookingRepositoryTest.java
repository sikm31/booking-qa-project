package io.github.qa.db;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("db")
@Epic("Бронирования")
@Feature("Хранилище (PostgreSQL)")
@SpringBootTest(classes = DbTestApplication.class)
class BookingRepositoryTest {

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void createSchema() {
        jdbc.execute("""
                CREATE TABLE IF NOT EXISTS booking (
                    id         SERIAL PRIMARY KEY,
                    firstname  VARCHAR(100) NOT NULL,
                    totalprice INT NOT NULL CHECK (totalprice >= 0)
                )""");
    }

    @Test
    @DisplayName("Сохранённое бронирование читается из БД")
    void savedBookingCanBeRead() {
        Integer id = jdbc.queryForObject(
                "INSERT INTO booking(firstname, totalprice) VALUES (?, ?) RETURNING id", Integer.class, "Ali", 500);

        Map<String, Object> row = jdbc.queryForMap("SELECT firstname, totalprice FROM booking WHERE id = ?", id);
        assertThat(row).containsEntry("firstname", "Ali").containsEntry("totalprice", 500);

        jdbc.update("DELETE FROM booking WHERE id = ?", id);
    }

    @Test
    @DisplayName("Отрицательная цена отклоняется ограничением CHECK")
    void negativePriceIsRejected() {
        assertThatThrownBy(() -> jdbc.update("INSERT INTO booking(firstname, totalprice) VALUES (?, ?)", "Bad", -1))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("check constraint");
    }
}
