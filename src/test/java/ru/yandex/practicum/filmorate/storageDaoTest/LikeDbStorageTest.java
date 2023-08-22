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
import ru.yandex.practicum.filmorate.storage.dao.LikeStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(value = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/deleteDb.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class LikeDbStorageTest {
    final FilmStorage filmStorage;
    final UserStorage userStorage;
    final LikeStorage likeStorage;
    public Film film;
    public User user1;
    public User user2;

    @BeforeEach
    void beforeEach() {
        film = Film.builder()
                .name("movie1")
                .description("movie1")
                .releaseDate(LocalDate.of(2001, 01, 01))
                .duration(121)
                .mpa(new Mpa(2))
                .build();
        user1 = User.builder()
                .email("one@yandex.ru")
                .login("one")
                .name("One")
                .birthday(LocalDate.of(2001, 01, 01))
                .build();

        user2 = User.builder()
                .email("two@yandex.ru")
                .login("two")
                .name("Two")
                .birthday(LocalDate.of(2002, 02, 02))
                .build();
    }

    @Test
    public void testAddLike() throws ValidationException {
        filmStorage.addFilm(film);
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        likeStorage.addLike(film.getId(),user1.getId());
        likeStorage.addLike(film.getId(),user2.getId());

        assertThat(filmStorage.getFilmById(film.getId()).getLikes().size()).isEqualTo(2);
    }

    @Test
    public void testGetLikes() throws ValidationException {
        filmStorage.addFilm(film);
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        likeStorage.addLike(film.getId(),user1.getId());
        likeStorage.addLike(film.getId(),user2.getId());

        assertThat(likeStorage.getLikes(film.getId()).size()).isEqualTo(2);
    }


    @Test
    void TestDeleteById() throws ValidationException {
        filmStorage.addFilm(film);
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        likeStorage.addLike(film.getId(),user1.getId());
        likeStorage.addLike(film.getId(),user2.getId());

        assertThat(likeStorage.getLikes(film.getId()).size()).isEqualTo(2);
        likeStorage.deleteLike(film.getId(), user2.getId());
        assertThat(likeStorage.getLikes(film.getId()).size()).isEqualTo(1);

    }

    @Test
    void TestExistLike() throws ValidationException {
        filmStorage.addFilm(film);
        userStorage.addUser(user1);
        likeStorage.addLike(film.getId(),user1.getId());

        assertThat(likeStorage.existLike(film.getId(),user1.getId())).isTrue();
    }

}
