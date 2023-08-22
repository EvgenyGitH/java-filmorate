package ru.yandex.practicum.filmorate.storage.dao;

import java.util.Set;

public interface LikeStorage {
    void addLike(Long filmId, Long userId);

    Set<Long> getLikes(Long filmId);

    void deleteLike(Long filmId, Long userId);

    boolean existLike(Long filmId, Long userId);
}
