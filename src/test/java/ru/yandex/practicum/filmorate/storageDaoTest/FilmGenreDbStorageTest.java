package ru.yandex.practicum.filmorate.storageDaoTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(value = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//@Sql(value = {"/deleteDb.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class FilmGenreDbStorageTest {
    public Film film1;
    public Film upFilm;
    public Film film2;
    public Film film3;
    final FilmStorage filmStorage;
    final FilmGenreStorage filmGenreStorage;
    final LikeStorage likeStorage;
    final UserStorage userStorage;

    @BeforeEach
    void beforeEach() {
        film1 = Film.builder()
                .name("movie1")
                .description("movie1")
                .releaseDate(LocalDate.of(2001, 01, 01))
                .duration(121)
                .mpa(new Mpa(2))
                .build();
        upFilm = Film.builder()
                .name("MOVIE1")
                .description("MOVIE1")
                .releaseDate(LocalDate.of(2001, 05, 01))
                .duration(123)
                .mpa(new Mpa(2))
                .build();
        film2 = Film.builder()
                .name("movie2")
                .description("movie2")
                .releaseDate(LocalDate.of(2002, 02, 02))
                .duration(122)
                .mpa(new Mpa(3))
                .build();
        film3 = Film.builder()
                .name("movie3")
                .description("movie3")
                .releaseDate(LocalDate.of(2003, 03, 03))
                .duration(123)
                .mpa(new Mpa(1))
                .build();
    }


    @Test
    public void testAddGenreAndGetFilmGenreByFilmId() throws ValidationException {
        filmStorage.addFilm(film1);
        Genre genre1 = new Genre(2, "Драма");
        Genre genre2 = new Genre(1, "Комедия");
        Set<Genre> filmGenre = new HashSet<>();
        filmGenre.add(genre1);
        filmGenre.add(genre2);
        film1.setGenres(filmGenre);
        Film addedFilm = filmGenreStorage.addGenre(film1);

        assertThat(filmGenreStorage.getFilmGenreByFilmId(addedFilm.getId()).size()).isEqualTo(2);
    }

    @Test
    public void testDeleteByFilmId() throws ValidationException {
        filmStorage.addFilm(film1);
        Genre genre1 = new Genre(2, "Драма");
        Genre genre2 = new Genre(1, "Комедия");
        Set<Genre> filmGenre = new HashSet<>();
        filmGenre.add(genre1);
        filmGenre.add(genre2);
        film1.setGenres(filmGenre);

        Film addedFilm = filmGenreStorage.addGenre(film1);
        assertThat(filmGenreStorage.getFilmGenreByFilmId(addedFilm.getId()).size()).isEqualTo(2);

        filmGenreStorage.deleteByFilmId(addedFilm.getId());
        assertThat(filmGenreStorage.getFilmGenreByFilmId(addedFilm.getId()).size()).isEqualTo(0);
    }


}
