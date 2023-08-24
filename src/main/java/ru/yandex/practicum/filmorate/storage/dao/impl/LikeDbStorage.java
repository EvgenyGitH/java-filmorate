package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorage;

import java.util.HashSet;
import java.util.Set;

@Component
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sqlQuery = "INSERT INTO likes (film_id, user_id) values(?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public Set<Long> getLikes(Long filmId) {
        String sqlQuery = "SELECT user_id FROM likes WHERE film_id = ?";
        SqlRowSet likesRows = jdbcTemplate.queryForRowSet(sqlQuery, filmId);
        Set<Long> likes = new HashSet<>();
        while (likesRows.next()) {
            likes.add(likesRows.getLong("user_id"));
        }
        return likes;
    }


    @Override
    public void deleteLike(Long filmId, Long userId) {
        String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public boolean existLike(Long filmId, Long userId) {
        String sqlQuery = "SELECT film_id, user_id FROM likes WHERE film_id = ? AND user_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, filmId, userId);
        return rowSet.next();
    }


}
