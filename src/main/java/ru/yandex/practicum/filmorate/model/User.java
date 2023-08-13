package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    @Email(message = "Email - не соответствует формат")
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Long> friends;

}
