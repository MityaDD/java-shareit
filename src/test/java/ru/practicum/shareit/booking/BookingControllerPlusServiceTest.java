package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerPlusServiceTest {
    private static final String HEADER = "X-Sharer-User-Id";
    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    private User booker;
    private User owner;

    private BookingDtoInput bookingInput;
    LocalDateTime start;
    LocalDateTime end;

    @BeforeEach
    public void setUp() throws Exception {
        booker = new User(1L, "user", "user@user.com");
        String jsonBooker = objectMapper.writeValueAsString(booker);


        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBooker));

        owner = new User(2L, "newUser", "newUser@user.com");
        String jsonOwner = objectMapper.writeValueAsString(owner);


        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonOwner));

        Item item = new Item(1L, "Дрель", "Простая дрель", owner, true, null);
        String jsonItem = objectMapper.writeValueAsString(item);

        Long userId = 1L;
        mockMvc.perform(post("/items")
                .header(HEADER, userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonItem));


        Long bookerId = 2L;
        start = LocalDateTime.now().plusMinutes(1);
        end = start.plusDays(1);

        bookingInput = new BookingDtoInput(1L, 1L, start, end, null);
        String jsonBooking = objectMapper.writeValueAsString(bookingInput);

        mockMvc.perform(post("/bookings")
                .header(HEADER, bookerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBooking));
    }


    @Test
    @DisplayName("Получаем список по id")
    public void shouldGetBookingsById() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(get("/bookings/{id}", bookingId)
                        .header(HEADER, userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    @DisplayName("Обновляем booking по пользователю")
    public void shouldUpdateBookingByUser() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(patch("/bookings/{bookingId}?approved=true", bookingId)
                        .header(HEADER, userId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/bookings/{id}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("Кидает NotFound, если booking не пользователя")
    public void shouldUpdateBookingByNoBooker() throws Exception {
        Integer bookingId = 99;
        Integer userId = 1;

        mockMvc.perform(patch("/bookings/{bookingId}?approved=true", bookingId)
                        .header(HEADER, userId))
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Кидает BadRequest при повторном апруве booking")
    public void shouldUpdateBookingWithSecondApproved() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(patch("/bookings/{bookingId}?approved=true", bookingId)
                        .header(HEADER, userId))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/bookings/{bookingId}?approved=true", bookingId)
                        .header(HEADER, userId))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("Отменяем booking")
    public void shouldUpdateBookingWithApprovedFalse() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(patch("/bookings/{bookingId}?approved=false", bookingId)
                        .header(HEADER, userId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/bookings/{id}", bookingId)
                        .header(HEADER, userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));

    }

    @Test
    @DisplayName("Кидает NotFound, если user не найден ")
    public void shouldTrowEcsWhenUserUnknown() throws Exception {
        Integer bookingId = 1;
        Integer userId = 100;


        mockMvc.perform(get("/bookings/{id}", bookingId)
                        .header(HEADER, userId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Кидает NotFound, если booking не найден ")
    public void shouldTrowEcsWhenBookingUnknown() throws Exception {
        Integer bookingId = 100;
        Integer userId = 1;

        mockMvc.perform(get("/bookings/{id}", bookingId)
                        .header(HEADER, userId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Получаем все booking по стейту All(default)")
    public void shouldFindAllBookingsALL() throws Exception {
        Integer bookingId = 1;
        Integer userId = 2;

        mockMvc.perform(get("/bookings", bookingId)
                        .header(HEADER, userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("Получаем все booking по стейту FUTURE")
    public void shouldFindAllBookingsFUTURE() throws Exception {
        Integer bookingId = 1;
        Integer userId = 2;

        mockMvc.perform(get("/bookings?state=FUTURE", bookingId)
                        .header(HEADER, userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("Получаем все booking по стейту WAITING")
    public void shouldFindAllBookingsWAITING() throws Exception {
        Integer bookingId = 1;
        Integer userId = 2;

        mockMvc.perform(get("/bookings?state=WAITING", bookingId)
                        .header(HEADER, userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("Получаем все booking по стейту REJECTED")
    public void shouldFindAllBookingsREJECTED() throws Exception {
        Integer bookingId = 1;
        Integer userId = 2;

        mockMvc.perform(get("/bookings?state=REJECTED", bookingId)
                        .header(HEADER, userId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("Получаем все booking для OWNER по стейту ALL")
    public void shouldFindAllBookingsForOwnerALL() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(get("/bookings/owner", bookingId)
                        .header(HEADER, userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("Получаем все booking для OWNER по стейту FUTURE")
    public void shouldFindAllBookingsForOwnerFUTURE() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(get("/bookings/owner?state=FUTURE", bookingId)
                        .header(HEADER, userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("Получаем все booking для OWNER по стейту WAITING")
    public void shouldFindAllBookingsForOwnerWaiting() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(get("/bookings/owner?state=WAITING", bookingId)
                        .header(HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("Получаем все booking для OWNER по стейту REJECTED")
    public void shouldFindAllBookingsForOwnerREJECTED() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(get("/bookings/owner?state=REJECTED", bookingId)
                        .header(HEADER, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Получаем все booking по стейту PAST")
    public void shouldFindAllBookingsPAST() throws Exception {
        Integer bookingId = 1;
        Integer userId = 2;

        mockMvc.perform(get("/bookings?state=PAST", bookingId)
                        .header(HEADER, userId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Получаем все booking по стейту CURRENT")
    public void shouldFindAllBookingsCURRENT() throws Exception {
        Integer bookingId = 1;
        Integer userId = 2;

        mockMvc.perform(get("/bookings?state=CURRENT", bookingId)
                        .header(HEADER, userId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Получаем все booking для OWNER по стейту PAST")
    public void shouldFindAllBookingsForOwnerPAST() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(get("/bookings/owner?state=PAST", bookingId)
                        .header(HEADER, userId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Получаем все booking для OWNER по стейту CURRENT")
    public void shouldFindAllBookingsForOwnerCURRENT() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(get("/bookings/owner?state=CURRENT", bookingId)
                        .header(HEADER, userId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Получаем все booking по стейту от балды aka UnsupportedStatus")
    public void shouldFindAllBookingsUnsupportedStatus() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(get("/bookings/owner?state=PASTPast", bookingId)
                        .header(HEADER, userId))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Кидаем BadRequest, если start=end")
    public void shouldThrowEcsWhenStartEqualsEnd() throws Exception {

        Long bookerId = 2L;
        start = LocalDateTime.now().plusHours(1);
        end = start.minusMinutes(30);

        bookingInput = new BookingDtoInput(1L, 1L, start, end, null);
        String jsonBooking = objectMapper.writeValueAsString(bookingInput);

        mockMvc.perform(post("/bookings")
                        .header(HEADER, bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBooking))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Кидаем BadRequest, если нет даты")
    public void shouldThrowEcsWhenEmptyData() throws Exception {

        Long bookerId = 2L;

        bookingInput = new BookingDtoInput(1L, 1L, null, null, null);
        String jsonBooking = objectMapper.writeValueAsString(bookingInput);

        mockMvc.perform(post("/bookings")
                        .header(HEADER, bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBooking))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Кидаем BadRequest, если item недоступен для booking")
    public void shouldThrowEcsWhenAvailableFalse() throws Exception {

        Long bookerId = 2L;
        start = LocalDateTime.now().plusMinutes(1);
        end = start.plusDays(1);

        Item item = new Item(2L, "Дрель++", "Простая дрель++", owner, false, null);
        String jsonItem = objectMapper.writeValueAsString(item);

        Long userId = 1L;
        mockMvc.perform(post("/items")
                .header(HEADER, userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonItem));

        bookingInput = new BookingDtoInput(1L, 2L, start, end, null);
        String jsonBooking = objectMapper.writeValueAsString(bookingInput);

        mockMvc.perform(post("/bookings")
                        .header(HEADER, bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBooking))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Получаем список всех пользователей")
    public void shouldBookingCreateWithFalseUserEqualsOwner() throws Exception {

        Long bookerId = 1L;
        start = LocalDateTime.now().plusMinutes(1);
        end = start.plusDays(1);

        bookingInput = new BookingDtoInput(1L, 1L, start, end, null);
        String jsonBooking = objectMapper.writeValueAsString(bookingInput);

        mockMvc.perform(post("/bookings")
                        .header(HEADER, bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBooking))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Кидаем BadRequest, если size отрицателен")
    public void shouldThrowEcsWhenSizeNegative() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(get("/bookings/owner?size=-1&from=0", bookingId)
                        .header(HEADER, userId))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Кидаем BadRequest, если from отрицателен")
    public void shouldThrowEcsWhenFromNegative() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(get("/bookings/owner?size=10&from=-1", bookingId)
                        .header(HEADER, userId))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}

