package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;
    @NotEmpty(message = "name should not be empty")
    private String name;
    @Email(message = "email should be valid")
    @NotEmpty(message = "email should not be empty")
    @NotNull
    private String email;
}
