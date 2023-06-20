package ru.practicum.server.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.server.util.Log;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ErrorHandlerTest {

    @Test
    void testNotFoundException() {
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> Log.andThrowNotFound("не найдено")
        );

        assertEquals("не найдено", exception.getMessage());
    }

    @Test
    void testNotValidException() {
        final NotValidException exception = assertThrows(
                NotValidException.class,
                () -> Log.andThrowNotValid("не подходит")
        );

        assertEquals("не подходит", exception.getMessage());
    }

    @Test
    void testEmailException() {
        final EmailException exception = assertThrows(
                EmailException.class,
                () -> Log.andThrowEmailConflict("конфликт email")
        );

        assertEquals("конфликт email", exception.getMessage());
    }
}