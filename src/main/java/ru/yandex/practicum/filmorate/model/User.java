package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

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


    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("email", getEmail());
        values.put("login", getLogin());
        values.put("name", getName());
        values.put("birthday", getBirthday());
        return values;
    }
}
