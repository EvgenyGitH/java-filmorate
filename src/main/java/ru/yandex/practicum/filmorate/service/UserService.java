package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UpdateException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.FriendStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    @Autowired
    public UserService(UserStorage userStorage, FriendStorage friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    public void validateUser(User user) throws ValidationException {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("электронная почта не может быть пустой и должна содержать символ @: {}", user.getEmail());
            throw new ValidationException("электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getEmail() == null || user.getLogin().isBlank()) {
            log.error("логин не может быть пустым и содержать пробелы: {}", user.getLogin());
            throw new ValidationException("логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getEmail() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.error("дата рождения не может быть в будущем: {}", user.getBirthday());
            throw new ValidationException("дата рождения не может быть в будущем");
        }
    }

    public User addUser(User user) throws ValidationException {
        validateUser(user);
        return userStorage.addUser(user);


    }

    public User updateUser(User user) throws ValidationException {
        validateUser(user);
        if (userStorage.existUser(user.getId())) {
            User userUpdate = userStorage.updateUser(user);
            log.info("Пользователь обновлён: {}", userUpdate);
            return userUpdate;
        } else {
            throw new UserNotFoundException(String.format("Пользователь: id=%d не найден", user.getId()));
        }
    }


    public List<User> allUser() {
        return userStorage.allUser();
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public void checkFriendsId(Long id, Long friendId) {
        if (id == friendId) {
            throw new UserNotFoundException("Incorrect id");
        }
    }

    public User addFriend(Long userId, Long friendId) {
        checkFriendsId(userId, friendId);
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (!friendStorage.isExist(userId, friendId)) {
            friendStorage.addFriend(userId, friendId);
            log.info("Пользователь: {} отправил запрос на дружбу пользователю: {}", userId, friendId);
        } else if (friendStorage.isConfirmed(userId, friendId)) {
            friendStorage.confirm(userId, friendId);
            log.info("Пользователь: {} и пользователь: {} друзья", userId, friendId);
        } else {
            throw new UpdateException("Пользователи уже являются друзьями");
        }
        return user;
    }

    public User deleteFriend(Long userId, Long friendId) {
        checkFriendsId(userId, friendId);
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (friendStorage.isExist(userId, friendId)) {
            friendStorage.deleteFriend(userId, friendId);
            log.info("Пользователь: {} и пользователь: {} больше не друзья", userId, friendId);
        } else {
            throw new UserNotFoundException("Пользователь не является другом");
        }
        return user;
    }

    public List<User> getUserFriends(Long userId) {
        User user = userStorage.getUserById(userId);
        log.debug("Запроc на получение списока друзей для пользователя: {}", userId);
        return friendStorage.getFriendsByUserId(userId);

    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        checkFriendsId(userId, otherId);
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(otherId);
        log.debug("Запрос общих друзей пользователей: {} и {}", userId, otherId);
        return friendStorage.getCommonFriends(userId, otherId);

    }


}
