package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorage;
import ru.yandex.practicum.filmorate.storage.dao.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final FilmGenreDbStorage filmGenreDbStorage;
    private final LikeStorage likeStorage;


    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaStorage mpaStorage, FilmGenreDbStorage filmGenreDbStorage, LikeStorage likeStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaStorage = mpaStorage;
        this.filmGenreDbStorage = filmGenreDbStorage;

        this.likeStorage = likeStorage;
    }

    @Override
    public Film addFilm(Film film) throws ValidationException {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        Long id = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
        film.setId(id);
        return film;
    }


    @Override
    public Film updateFilm(Film film) throws ValidationException {
        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? where film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        return film;
    }

    @Override
    public List<Film> allFilms() {
        String sqlQuery = "SELECT * FROM films";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs));
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Long id = rs.getLong("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        int mpaId = rs.getInt("mpa_id");
        Mpa mpa = mpaStorage.getMpaById(mpaId);
        Set<Genre> genres = filmGenreDbStorage.getFilmGenreByFilmId(id);
        Set<Long> likes = likeStorage.getLikes(id);
        Film film = Film.builder().name(name).description(description).releaseDate(releaseDate).duration(duration)
                .mpa(mpa).genres(genres).likes(likes).build();
        film.setId(id);
        return film;
    }

    @Override
    public Film getFilmById(Long filmId) {
        try {
            String sql = "select * from films where film_id = ?";
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeFilm(rs), filmId);
        } catch (EmptyResultDataAccessException exp) {
            throw new IncorrectIdException("фильма с id " + filmId + " не существует!");
        }
    }


    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "select * " +
                "from films as f " +
                "left join (select film_id, " +
                "count(user_id) as count_likes, " +
                "from likes group by film_id) as l " +
                "on f.film_id = l.film_id order by l.count_likes desc limit ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);
    }


}
