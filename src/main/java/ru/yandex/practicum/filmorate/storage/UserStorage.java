package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;

public interface UserStorage {
    User addUser(User user) throws ValidationException;

    User updateUser(User user) throws ValidationException;

    HashMap<Long, User> allUser();

    User getUserById(Long id);
}
