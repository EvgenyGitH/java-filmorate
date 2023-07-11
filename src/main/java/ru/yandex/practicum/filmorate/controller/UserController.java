package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;


@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    HashMap<Integer, User> users = new HashMap<>();
    private int currentUserId = 1;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) throws ValidationException {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@") || user.getEmail() == null) {
            log.error("электронная почта не может быть пустой и должна содержать символ @: {}", user.getEmail());
            throw new ValidationException("электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("логин не может быть пустым и содержать пробелы: {}", user.getLogin());
            throw new ValidationException("логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("дата рождения не может быть в будущем: {}", user.getBirthday());
            throw new ValidationException("дата рождения не может быть в будущем");
        }
        user.setId(currentUserId);
        currentUserId++;
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User updateFilm(@Valid @RequestBody User user) throws ValidationException {
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("фильм с указанным ID не существует");
        }
        if (user.getEmail().isBlank() || !user.getEmail().contains("@") || user.getEmail() == null) {
            log.error("электронная почта не может быть пустой и должна содержать символ @: {}", user.getEmail());
            throw new ValidationException("электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("логин не может быть пустым и содержать пробелы: {}", user.getLogin());
            throw new ValidationException("логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("дата рождения не может быть в будущем: {}", user.getBirthday());
            throw new ValidationException("дата рождения не может быть в будущем");
        }

        users.put(user.getId(), user);
        log.info("Обновлен пользователь: {}", user);
        return user;
    }

    @GetMapping
    public Collection<User> allUser() {
        log.info("Всего пользователей: {}", users.size());
        return users.values();
    }


}
