package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User addUser(User user) throws ValidationException;

    User updateUser(User user) throws ValidationException;

    List<User> allUser();

    User getUserById(Long id);

    boolean existUser(Long id);

    void deleteAllUsers();
}
