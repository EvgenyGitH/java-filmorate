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
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)

@Sql(value = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//@Sql(value = {"/deleteDb.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)

public class FilmDbStorageTest {
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
    public void testAddFilm() throws ValidationException {
        Film addedFilm = filmStorage.addFilm(film1);

        assertThat(addedFilm.getId()).isNotZero();
        assertThat(addedFilm.getName()).isEqualTo(film1.getName());
        assertThat(addedFilm.getDescription()).isEqualTo(film1.getDescription());
        assertThat(addedFilm.getReleaseDate()).isEqualTo(film1.getReleaseDate());
        assertThat(addedFilm.getDuration()).isEqualTo(film1.getDuration());
        assertThat(addedFilm.getMpa()).isEqualTo(film1.getMpa());
    }

    @Test
    public void testUpdateFilm() throws ValidationException {
        Film addedFilm = filmStorage.addFilm(film1);
        upFilm.setId(addedFilm.getId());
        Film updatedFilm = filmStorage.updateFilm(upFilm);

        assertThat(updatedFilm.getName()).isEqualTo(upFilm.getName());
        assertThat(updatedFilm.getDescription()).isEqualTo(upFilm.getDescription());
        assertThat(updatedFilm.getReleaseDate()).isEqualTo(upFilm.getReleaseDate());
        assertThat(updatedFilm.getDuration()).isEqualTo(upFilm.getDuration());
        assertThat(updatedFilm.getMpa()).isEqualTo(upFilm.getMpa());
    }

    @Test
    public void testAllFilms() throws ValidationException {
        Film addedFilm1 = filmStorage.addFilm(film1);
        Film addedFilm2 = filmStorage.addFilm(film2);
        Film addedFilm3 = filmStorage.addFilm(film3);
        final List<Film> allFilms = filmStorage.allFilms();

        assertThat(allFilms).isNotNull();
        assertThat(allFilms.size()).isEqualTo(3);
    }

    @Test
    public void testGetFilmById() throws ValidationException {
        Long id = filmStorage.addFilm(film1).getId();
        Film filmById = filmStorage.getFilmById(id);

        assertThat(filmById).isNotNull();
        assertThat(filmById.getId()).isEqualTo(film1.getId());
        assertThat(filmById.getName()).isEqualTo(film1.getName());
        assertThat(filmById.getDescription()).isEqualTo(film1.getDescription());

    }

    @Test
    public void testGetPopularFilms() throws ValidationException {
        Film addedFilm1 = filmStorage.addFilm(film1);
        Film addedFilm2 = filmStorage.addFilm(film2);
        Film addedFilm3 = filmStorage.addFilm(film3);
        User user = User.builder()
                .email("one@yandex.ru")
                .login("one")
                .name("One")
                .birthday(LocalDate.of(2001, 01, 01))
                .build();
        likeStorage.addLike(addedFilm3.getId(), user.getId());
        List<Film> popularFilms = filmStorage.getPopularFilms(1);

        assertThat(popularFilms)
                .isNotEmpty()
                .hasSize(1)
                .containsExactly(filmStorage.getFilmById(addedFilm3.getId()));
    }

}
