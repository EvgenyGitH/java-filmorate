package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendStorage {

    void addFriend(Long userId, Long friendId);

    boolean isExist(Long userId, Long friendId);

    List<User> getFriendsByUserId(Long userId);

    List<User> getCommonFriends(Long userId, Long otherId);

    boolean isConfirmed(Long userId, Long friendId);

    boolean confirm(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);
}
