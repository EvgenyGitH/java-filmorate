package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    HashMap<Long, User> users = new HashMap<>();

    private Long currentUserId = 1L;

    @Override
    public User addUser(User user) throws ValidationException {
        user.setId(currentUserId);
        currentUserId++;
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("Пользователь с указанным ID не существует");
        }
        users.put(user.getId(), user);
        log.info("Обновлен пользователь: {}", user);
        return user;
    }

    @Override
    public HashMap<Long, User> allUser() {
        log.info("Всего пользователей: {}", users.size());
        return users;
    }

    @Override
    public User getUserById(Long id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        return users.get(id);
    }


}
