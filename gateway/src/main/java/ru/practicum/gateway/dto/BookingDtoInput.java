package ru.practicum.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.gateway.exception.NotValidException;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDtoInput {
    private Long id;
    @NotNull
    private Long itemId;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Future
    private LocalDateTime end;
    private Status status;

    public static void validate(BookingDtoInput bookingDto) {
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new NotValidException("Бронь не может закончиться раньше ее начала");
        }
    }
}
