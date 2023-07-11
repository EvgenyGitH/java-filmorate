package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;


@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    HashMap<Integer, Film> films = new HashMap<>();
    private int currentId = 1;


    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (film.getName().isEmpty() || film.getName().isBlank()) {
            log.error("название не может быть пустым");
            throw new ValidationException("название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.error("максимальная длина описания — 200 символов"  );
            throw new ValidationException("максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("дата релиза — не раньше 28 декабря 1895 года: {}", film.getReleaseDate());
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");

        }
        if (film.getDuration() < 0) {
            log.error("продолжительность фильма должна быть положительной");
            throw new ValidationException("продолжительность фильма должна быть положительной");
        }
        film.setId(currentId);
        currentId++;
        films.put(film.getId(), film);

        log.info("Добавлен фильм: {}", film );
        return film;

    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (!films.containsKey(film.getId())){
            log.error("фильм с указанным ID не существует: {}", film.getId() );
            throw new ValidationException("фильм с указанным ID не существует");
        }
        if (film.getName().isEmpty() || film.getName().isBlank()) {
            log.error("название не может быть пустым");
            throw new ValidationException("название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.error("максимальная длина описания — 200 символов"  );
            throw new ValidationException("максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("дата релиза — не раньше 28 декабря 1895 года: {}", film.getReleaseDate());
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");

        }
        if (film.getDuration() < 0) {
            log.error("продолжительность фильма должна быть положительной");
            throw new ValidationException("продолжительность фильма должна быть положительной");
        }

        log.info("Обновлен фильм: {}", film );
        films.put(film.getId(), film);
        return film;
    }

    @GetMapping
    public Collection<Film> allFilms() {
        log.info("Количество фильмов: {}", films.size() );
        return films.values();
    }


}
