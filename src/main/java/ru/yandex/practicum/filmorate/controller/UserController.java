package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    public UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) throws ValidationException {
        userService.validateUser(user);
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        userService.validateUser(user);
        return userService.updateUser(user);
    }

    @GetMapping
    public Collection<User> allUser() {
        return userService.allUser().values();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable Long id) {
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.getCommonFriends(id, otherId);
    }

}


/*

+ GET .../users/{id} - получать каждый фильм и данные о пользователях по их уникальному идентификатору
+ PUT /users/{id}/friends/{friendId}  — добавление в друзья.
+ DELETE /users/{id}/friends/{friendId} — удаление из друзей.
+ GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
+ GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.

*/