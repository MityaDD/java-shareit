package ru.practicum.server.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.server.booking.model.Status;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.dto.BookingDtoInput;
import ru.practicum.server.booking.dto.BookingDtoSpecial;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDtoInput> jsonBookingDtoInput;
    @Autowired
    private JacksonTester<BookingDtoSpecial> jsonBookingDtoSpecial;
    @Autowired
    private JacksonTester<BookingDto> jsonBookingDto;

    @Test
    @DisplayName("BookingDto  1")
    void testBookingDtoInput() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        BookingDtoInput bookingDtoInputNew = new BookingDtoInput(1L, 1L, start, end, Status.WAITING);

        JsonContent<BookingDtoInput> result = jsonBookingDtoInput.write(bookingDtoInputNew);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.toString());
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo(Status.WAITING.toString());
    }

    @Test
    @DisplayName("BookingDtoSpecial  2")
    void testBookingDtoSpecial() throws Exception {
        LocalDateTime start = LocalDateTime.now().withNano(000000);
        LocalDateTime end = LocalDateTime.now().withNano(000000).plusDays(1);

        BookingDtoSpecial bookingDtoSpecialNew = new BookingDtoSpecial(1L, start, end, 2L, Status.APPROVED);

        JsonContent<BookingDtoSpecial> result = jsonBookingDtoSpecial.write(bookingDtoSpecialNew);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.start").isNotEmpty();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotEmpty();
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo(Status.APPROVED.toString());
    }

    @Test
    @DisplayName("BookingDto  3")
    void testBookingDto() throws Exception {
        LocalDateTime start = LocalDateTime.now().withNano(000000);
        LocalDateTime end = LocalDateTime.now().withNano(000000).plusDays(1);

        BookingDto bookingDtoNew = new BookingDto(1L, null, start, end, null, Status.REJECTED);

        JsonContent<BookingDto> result = jsonBookingDto.write(bookingDtoNew);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isNotEmpty();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotEmpty();
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo(Status.REJECTED.toString());
    }

    @Test
    @DisplayName("BookingDto  4")
    void testBookingDtoWithoutBookerAndItem() throws Exception {
        User booker = new User();
        booker.setId(1L);
        booker.setName("Test Booker");
        booker.setEmail("Booker@Booker.com");

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);


        LocalDateTime start = LocalDateTime.now().withNano(000000);
        LocalDateTime end = LocalDateTime.now().withNano(000000).plusDays(1);

        BookingDto bookingDtoNew = new BookingDto(1L, item, start, end, booker, Status.REJECTED);

        JsonContent<BookingDto> result = jsonBookingDto.write(bookingDtoNew);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isNotEmpty();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotEmpty();
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo(Status.REJECTED.toString());
    }

    @Test
    @DisplayName("BookingDto  5")
    void testBookingDtoWithoutData() throws Exception {

        BookingDto bookingResponseDtoNew = new BookingDto();

        JsonContent<BookingDto> result = jsonBookingDto.write(bookingResponseDtoNew);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(null);

    }
}
