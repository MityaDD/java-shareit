package ru.practicum.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDtoInput {

    @Size(max = 100, message = "Описание не может быть более 100 символов ")
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
}
