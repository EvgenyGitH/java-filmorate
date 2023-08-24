package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final LikeStorage likeStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, UserStorage userStorage, FilmGenreStorage filmGenreStorage, LikeStorage likeStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmGenreStorage = filmGenreStorage;
        this.likeStorage = likeStorage;
    }


    private void validateFilm(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("название не может быть пустым");
            throw new ValidationException("название не может быть пустым");
        }
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            log.error("максимальная длина описания — 200 символов");
            throw new ValidationException("максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("дата релиза — не раньше 28 декабря 1895 года: {}", film.getReleaseDate());
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            log.error("продолжительность фильма должна быть положительной");
            throw new ValidationException("продолжительность фильма должна быть положительной");
        }
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        } else {
            Set<Genre> filmGenre = film.getGenres();
            for (Genre genre : filmGenre) {
                if (genre.getId() == 0 || genre.getId() > 6) {
                    log.error("информация о жанре указана неверно");
                    throw new ValidationException("информация о жанре указана неверно");
                }
            }
        }
    }

    private void checkDoubleFilm(Film film) throws ValidationException {
        List<Film> getAllFilms = filmStorage.allFilms();
        for (Film allFilm : getAllFilms) {
            if ((allFilm.getName().equals(film.getName())) && (allFilm.getReleaseDate().equals(film.getReleaseDate()))
                    && (allFilm.getDuration() == film.getDuration())) {
                log.error("дублирование, фильм уже добавлен в хранилище");
                throw new ValidationException("дублирование, фильм уже добавлен в хранилище");
            }
        }
    }

    public Film addFilm(Film film) throws ValidationException {
        validateFilm(film);
        checkDoubleFilm(film);
        filmStorage.addFilm(film);
        filmGenreStorage.addGenre(film);
        log.info("Создан Film: {}", film);
        return film;
    }

    public Film updateFilm(Film film) throws ValidationException {
        validateFilm(film);
        filmStorage.getFilmById(film.getId());
        filmStorage.updateFilm(film);
        filmGenreStorage.deleteByFilmId(film.getId());
        filmGenreStorage.addGenre(film);
        film.setLikes(likeStorage.getLikes(film.getId()));
        log.info("Обновлен Film: {}", film);
        return filmStorage.getFilmById(film.getId());
    }

    public List<Film> allFilms() {
        return filmStorage.allFilms();
    }

    public Film getFilmById(Long filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public void addLikeFilm(Long filmId, Long userId) {
        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);
        if (!likeStorage.existLike(filmId, userId)) {
            likeStorage.addLike(filmId, userId);
        } else {
            throw new IncorrectIdException("Пользователь ранее поставил лайк");
        }
    }

    public void deleteLikeFilm(Long filmId, Long userId) {
        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);
        if (likeStorage.existLike(filmId, userId)) {
            likeStorage.deleteLike(filmId, userId);
        } else {
            throw new IncorrectIdException("Пользователь не ставил лайк");
        }
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }


}
