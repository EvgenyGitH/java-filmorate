/*
package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    HashMap<Integer, Film> films = new HashMap<>();
    private int currentId = 1;

    @Override
    public Film addFilm(@RequestBody Film film) throws ValidationException {
        film.setId(currentId);
        currentId++;
        films.put(film.getId(), film);

        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(@RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("фильм с указанным ID не существует: {}", film.getId());
            throw new FilmNotFoundException("фильм с указанным ID не существует");
        }

        log.info("Обновлен фильм: {}", film);
        films.put(film.getId(), film);
        return film;
    }


    @Override
    public HashMap<Integer, Film> allFilms() {
        log.info("Количество фильмов: {}", films.size());
        return films;
    }

    @Override
    public Film getFilmById(int filmId) {
        if (!films.containsKey(filmId)) {
            throw new FilmNotFoundException("Film Not Found");
        }
        return films.get(filmId);
    }


}
*/
