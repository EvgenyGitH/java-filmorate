package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreStorage;

import java.util.HashSet;
import java.util.Set;


@Component
public class FilmGenreDbStorage implements FilmGenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmGenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addGenre(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            film.setGenres(new HashSet<>());
            return film;
        } else {
            String sqlQuery = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            Long filmId = film.getId();
            for (Genre genre : film.getGenres()) {
                int genreId = genre.getId();
                jdbcTemplate.update(sqlQuery, filmId, genreId);
            }
            return film;
        }
    }

    @Override
    public Set<Genre> getFilmGenreByFilmId(long filmId) {
        SqlRowSet genresRows = jdbcTemplate.queryForRowSet("select * from genres where genre_id in " +
                "(select genre_id from film_genres where film_id = ?) order by genre_id asc ", filmId);
        Set<Genre> filmGenres = new HashSet<>();
        while (genresRows.next()) {
            Genre genreFilm = new Genre(genresRows.getInt("genre_id"),
                    genresRows.getString("name"));
            filmGenres.add(genreFilm);
        }
        return filmGenres;
    }

    @Override
    public void deleteByFilmId(Long filmId) {
        var sqlQuery = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }


}
