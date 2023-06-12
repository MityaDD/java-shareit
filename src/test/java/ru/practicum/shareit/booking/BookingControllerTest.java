package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotValidException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {
    private static final String HEADER = "X-Sharer-User-Id";
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private User booker;
    private User owner;
    private Item item;
    private BookingDtoInput bookingDtoInput;
    private BookingDto bookingDto;

    LocalDateTime start;
    LocalDateTime end;
    @MockBean
    private BookingService bookingService;

    @BeforeEach
    public void setUp() throws Exception {
        booker = new User(1L, "user", "user@user.com");

        owner = new User(2L, "newUser", "newUser@user.com");

        item = new Item(1L, "Дрель", "Простая дрель", owner, true, null);

        start = LocalDateTime.now().plusMinutes(1).withNano(000);
        end = start.plusDays(1).withNano(000);

        bookingDtoInput = new BookingDtoInput(1L, 1L, start, end, null);

        bookingDto = BookingDto
                .builder()
                .id(bookingDtoInput.getId())
                .status(Status.WAITING)
                .start(bookingDtoInput.getStart())
                .end(bookingDtoInput.getEnd())
                .item(item)
                .booker(booker)
                .build();
    }

    @Test
    @DisplayName("Создаем booking")
    public void addReservation() throws Exception {
        when(bookingService.addBooking(anyLong(), any()))
                .thenReturn(bookingDto);

        String jsonBooking = objectMapper.writeValueAsString(bookingDtoInput);

        mockMvc.perform(post("/bookings")
                        .header(HEADER, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBooking))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("Дрель"))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.booker.name").value("user"));
    }

    @Test
    @DisplayName("Получаем booking по id")
    public void shouldReturnOkWhenGetBookingById() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{id}", bookingId)
                        .header(HEADER, userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("Дрель"))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.booker.name").value("user"));
    }

    @Test
    @DisplayName("Обновляем booking по пользователю")
    public void shouldReturnOkWhenSetApprovedByOwner() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;
        bookingDto.setStatus(Status.APPROVED);

        when(bookingService.setApprovedByOwner(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}?approved=true", bookingId)
                        .header(HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("Кидает NotFound, если booking не пользователя")
    public void shouldUpdateBookingByNoBooker() throws Exception {
        Integer bookingId = 99;
        Integer userId = 1;

        when(bookingService.setApprovedByOwner(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new NotFoundException("Booking не найден"));

        mockMvc.perform(patch("/bookings/{bookingId}?approved=true", bookingId)
                        .header(HEADER, userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Не найден Booking не найден")));
    }

    @Test
    @DisplayName("Кидает BadRequest при повторном апруве booking")
    public void shouldUpdateBookingWithSecondApproved() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        when(bookingService.setApprovedByOwner(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new NotValidException("Статус APPROVED уже установлен"));

        mockMvc.perform(patch("/bookings/{bookingId}?approved=true", bookingId)
                        .header(HEADER, userId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Статус APPROVED уже установлен")));

    }

    @Test
    @DisplayName("Отменяем booking")
    public void shouldUpdateBookingWithApprovedFalse() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;
        bookingDto.setStatus(Status.REJECTED);

        when(bookingService.setApprovedByOwner(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}?approved=false", bookingId)
                        .header(HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));

    }

    @Test
    @DisplayName("Кидает NotFound, если user не найден ")
    public void shouldTrowNotFoundWhenUserUnknown() throws Exception {
        Integer bookingId = 1;
        Integer userId = 100;

        when(bookingService.setApprovedByOwner(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new NotFoundException("Booking не найден"));

        mockMvc.perform(patch("/bookings/{bookingId}?approved=true", bookingId)
                        .header(HEADER, userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Не найден Booking не найден")));
    }

    @Test
    @DisplayName("Получаем все booking по стейту All(default)")
    public void shouldFindAllBookingsALL() throws Exception {
        Integer userId = 2;

        when(bookingService.getAllBookings(anyString(), anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto, bookingDto, bookingDto));

        mockMvc.perform(get("/bookings")
                        .header(HEADER, userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @DisplayName("Получаем все booking для OWNER по стейту ALL")
    public void getAllReservationsByOwnerId() throws Exception {
        Integer userId = 2;

        when(bookingService.getAllBookings(anyString(), anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto, bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header(HEADER, userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("Получаем все booking для booker по стейту ALL")
    public void getAllReservationsByUserId() throws Exception {
        Integer userId = 2;

        when(bookingService.getAllBookings(anyString(), anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto, bookingDto));

        mockMvc.perform(get("/bookings/booker")
                        .header(HEADER, userId))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

}

