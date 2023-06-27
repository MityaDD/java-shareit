package ru.practicum.server.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDtoInput {
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
}