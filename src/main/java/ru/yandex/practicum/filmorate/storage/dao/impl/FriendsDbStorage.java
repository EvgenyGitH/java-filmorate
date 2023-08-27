package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FriendStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Component
public class FriendsDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void addFriend(Long userId, Long friendId) {
        String sqlQuery = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    public boolean isExist(Long userId, Long friendId) {
        String sqlQuery = "SELECT user_id, friend_id FROM friends WHERE (user_id = ? AND friend_id = ?)";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlQuery, userId, friendId);
        return rs.next();
    }

    @Override
    public List<User> getFriendsByUserId(Long userId) {
        String sql = "select * from users where user_id in " +
                "(select friend_id from friends where user_id = ?)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), userId);
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        try {
            String sql = "select * from users where user_id in " +
                    "(select friend_id from friends where user_id = ? and friend_id in " +          // 1 - 2 4
                    "(select friend_id from friends where user_id = ?))";                           // 4 - 3 2
            return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), userId, otherId);
        } catch (EmptyResultDataAccessException exp) {
            throw new IncorrectIdException("пользователя с таким id не существует!");
        }
    }


    private User makeUser(ResultSet rs) throws SQLException {
        Long id = rs.getLong("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        User user = User.builder()
                .id(id)
                .email(email)
                .login(login)
                .name(name)
                .birthday(birthday)
                .build();
        return user;
    }


    @Override
    public boolean isConfirmed(Long userId, Long friendId) {
        String sqlQuery = "SELECT user_id, friend_id FROM friends WHERE (user_id = ? AND friend_id = ?) OR (friend_id = ? AND user_id = ?) ";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlQuery, userId, friendId, userId, friendId);
        int count = 0;
        boolean flag = false;
        while (rs.next()) {
            count++;
        }
        if (count == 2) {
            flag = true;
        }
        return flag;
    }

    @Override
    public boolean confirm(Long userId, Long friendId) {
        String sqlQuery = "UPDATE friends SET confirmed = true WHERE (user_id = ? AND friend_id = ?) OR (friend_id = ? AND user_id = ?) ";
        jdbcTemplate.update(sqlQuery, userId, friendId, userId, friendId);
        return true;
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        jdbcTemplate.update("delete from friends where user_id = ? and friend_id = ?", userId, friendId);
        jdbcTemplate.update("delete from friends where user_id = ? and friend_id = ?", friendId, userId);
    }

}
