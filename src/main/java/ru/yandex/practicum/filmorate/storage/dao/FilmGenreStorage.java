package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;

public interface FilmGenreStorage {

    Film addGenre(Film film);

    Set<Genre> getFilmGenreByFilmId(long filmId);

    void deleteByFilmId(Long filmId);
}
