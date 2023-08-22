/*
package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    List<User> users = new ArrayList<>();

    private Long currentUserId = 1L;

    @Override
    public User addUser(User user) throws ValidationException {
        user.setId(currentUserId);
        currentUserId++;
        users.add(user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (getUserById(user.getId())==null) {
            throw new UserNotFoundException("Пользователь с указанным ID не существует");
        }
        users.add( user);
        log.info("Обновлен пользователь: {}", user);
        return user;
    }

    @Override
    public List<User> allUser() {
        log.info("Всего пользователей: {}", users.size());
        return users;
    }

    @Override
    public User getUserById(Long id) {
        User userById = null;
        for (User user:users){
            if (user.getId() == id){
                userById = user;
            }
        }
        if (userById == null) {  //!users.get(id)
            throw new UserNotFoundException("Пользователь не найден");
        }
        log.info("Пользователь найден по id: {}", id);
        return userById;

    }


}
*/
