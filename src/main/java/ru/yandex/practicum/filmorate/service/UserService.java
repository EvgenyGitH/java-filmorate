package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class UserService {
    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void validateUser(User user) throws ValidationException {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("электронная почта не может быть пустой и должна содержать символ @: {}", user.getEmail());
            throw new ValidationException("электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getEmail() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
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
        user.setFriends(new HashSet<>());
        return userStorage.addUser(user);

    }

    public User updateUser(User user) throws ValidationException {
        Set<Long> friends = userStorage.getUserById(user.getId()).getFriends();
        user.setFriends(friends);
        return userStorage.updateUser(user);
    }

    public HashMap<Long, User> allUser() {
        return userStorage.allUser();
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public void checkFriendsId(Long id, Long friendId) {
        if (!userStorage.allUser().containsKey(id) || !userStorage.allUser().containsKey(friendId)) {
            throw new UserNotFoundException("User Not Found");
        }
        if (id == friendId) {
            throw new UserNotFoundException("Incorrect id");
        }
    }

    public User addFriend(Long id, Long friendId) {
        checkFriendsId(id, friendId);
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
        log.info("User: {} add friend {}, friends list: {}", id, friendId, user.getFriends());
        return user;
    }

    public User deleteFriend(Long id, Long friendId) {
        checkFriendsId(id, friendId);
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
        log.info("User: {} delete friend {}", id, friendId);
        return user;
    }

    public List<User> getUserFriends(Long id) {
        User user = userStorage.getUserById(id);
        if (user.getFriends().isEmpty()) {
            throw new UserNotFoundException("User hasn't friends");
        }
        ArrayList<User> friendsList = new ArrayList<>();
        user.getFriends().forEach(friendId -> friendsList.add(userStorage.getUserById(friendId)));
        log.info("User's ID: {} friends", id);
        return friendsList;

    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        checkFriendsId(id, otherId);
        Set<Long> userList = new HashSet<>(userStorage.getUserById(id).getFriends());
        Set<Long> friendList = new HashSet<>(userStorage.getUserById(otherId).getFriends());
        List<User> commonFriends = new ArrayList<>();
        userList.retainAll(friendList);

        userList.forEach(friendId -> commonFriends.add(userStorage.getUserById(friendId)));
        log.info("User's ID: {} CommonFriends {}", id, otherId);
        return commonFriends;
    }


}
