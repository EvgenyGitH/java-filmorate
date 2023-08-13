package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
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

    }


    public Film addFilm(@RequestBody Film film) throws ValidationException {
        validateFilm(film);
        film.setLikes(new HashSet<>());
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(@RequestBody Film film) throws ValidationException {
        validateFilm(film);
        Set<Long> likes = filmStorage.getFilmById(film.getId()).getLikes();
        film.setLikes(likes);
        return filmStorage.updateFilm(film);
    }

    public HashMap<Integer, Film> allFilms() {
        return filmStorage.allFilms();
    }

    public Film getFilmById(int filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public Film addLikeFilm(int filmId, Long userId) {
        if (!allFilms().containsKey(filmId)) {
            throw new FilmNotFoundException("Film Not Found");
        }
        if (!userStorage.allUser().containsKey(userId)) {
            throw new UserNotFoundException("User Not Found");
        }
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().add(userId);
        return film;
    }

    public Film deleteLikeFilm(int filmId, Long userId) {
        if (!allFilms().containsKey(filmId)) {
            throw new FilmNotFoundException("Film Not Found");
        }
        if (!userStorage.allUser().containsKey(userId)) {
            throw new UserNotFoundException("User Not Found");
        }
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().remove(userId);
        return film;
    }


    public List<Film> getPopularFilms(int count) {
        Comparator<Film> comparator = (film1, film2) -> -1 * Integer.compare(film1.getLikes().size(),
                film2.getLikes().size());
        return filmStorage.allFilms().values().stream().sorted(comparator).limit(count).collect(Collectors.toList());
    }
}
