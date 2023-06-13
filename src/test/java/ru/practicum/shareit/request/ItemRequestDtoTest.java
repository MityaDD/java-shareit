package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDtoInput> jsonRequest;
    @Autowired
    private JacksonTester<ItemRequestDto> jsonResponse;

    @Test
    @DisplayName("temRequestDtoInput # 1")
    void testItemRequestDtoInput() throws Exception {
        ItemRequestDtoInput itemRequestDtoInput = new ItemRequestDtoInput("Описание для запроса вещи");

        JsonContent<ItemRequestDtoInput> result = jsonRequest.write(itemRequestDtoInput);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Описание для запроса вещи");
    }

    @Test
    @DisplayName("ItemRequestDto # 2")
    void testItemRequestDto() throws Exception {
        LocalDateTime time = LocalDateTime.now().withNano(000000);

        ItemRequestDto itemRequestDto = new ItemRequestDto(2L, "Отвертка с ручкой", time, new ArrayList<>());
        JsonContent<ItemRequestDto> result = jsonResponse.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Отвертка с ручкой");
        assertThat(result).extractingJsonPathStringValue("$.created").isNotEmpty();
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(0);
    }
}