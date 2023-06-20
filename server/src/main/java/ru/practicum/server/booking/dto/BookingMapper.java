package ru.practicum.server.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.model.Status;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.model.User;

@UtilityClass
public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .item(booking.getItem())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking(BookingDtoInput dto, Item item, User booker) {
        return Booking.builder()
                .id(dto.getId())
                .item(item)
                .start(dto.getStart())
                .end(dto.getEnd())
                .booker(booker)
                .status(Status.WAITING)
                .build();
    }

    public static BookingDtoSpecial toBookingDtoForItem(Booking booking) {
        return BookingDtoSpecial.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }

}