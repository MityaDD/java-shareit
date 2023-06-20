package ru.practicum.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.gateway.dto.Validated.Create;
import ru.practicum.gateway.dto.Validated.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;

    @NotBlank(groups = {Create.class})
    private String name;

    @NotNull(groups = {Create.class})
    @Email(groups = {Create.class, Update.class})
    private String email;
}
