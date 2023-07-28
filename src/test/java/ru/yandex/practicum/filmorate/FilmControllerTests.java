package ru.yandex.practicum.filmorate;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc


public class FilmControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private FilmController controller;

    @Autowired
    FilmStorage filmStorage;

    @Autowired
    UserStorage userStorage;
    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void addFilmCorrectData() throws Exception {
        //  Film film1 = new Film(1, "Movie-1", "Comedy", LocalDate.of(2021, 12, 21), 600);
        Film film0 = Film.builder()
                .id(1)
                .name("Movie-1")
                .description("Comedy")
                .releaseDate(LocalDate.of(2021, 12, 21))
                .duration(600)
                .likes(new HashSet<>())
                .build();

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film0))
                                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(film0)));
    }


    @Test
    public void shoudReturnExReleaseDateAddFilm() throws Exception {
        Film film = new Film(1, "Movie-1", "Comedy", LocalDate.of(1021, 12, 21), 600, new HashSet<>());
       /* Assertions.assertThrows(Exception.class, () -> this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film1)))
                .andDo(print()));*/

        ResultActions response = mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)));
        response.andExpect(status().isBadRequest());
        response.andExpect(content().json(objectMapper.writeValueAsString(Map.of("error",
                "дата релиза — не раньше 28 декабря 1895 года"))));

    }


    @Test
    public void shouldReturnExBlankName() throws Exception {
        Film film = new Film(1, " ", "Comedy", LocalDate.of(2021, 12, 21), 600, new HashSet<>());
        ResultActions response = mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)));
        response.andExpect(status().isBadRequest());
        response.andExpect(content().json(objectMapper.writeValueAsString(Map.of("error",
                "название не может быть пустым"))));
    }

    @Test
    public void shouldReturnExNegativeDuration() throws Exception {
        Film film = new Film(1, "Movie-1", "Comedy", LocalDate.of(2021, 12, 21), -600, new HashSet<>());
        ResultActions response = mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)));
        response.andExpect(status().isBadRequest());
        response.andExpect(content().json(objectMapper.writeValueAsString(Map.of("error",
                "продолжительность фильма должна быть положительной"))));
    }

    @Test
    public void shouldReturnExLongDescription() throws Exception {
        Film film = new Film(1, "Movie-1", "Comedy".repeat(210), LocalDate.of(2021, 12, 21), -600, new HashSet<>());
        ResultActions response = mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)));
        response.andExpect(status().isBadRequest());
        response.andExpect(content().json(objectMapper.writeValueAsString(Map.of("error",
                "максимальная длина описания — 200 символов"))));
    }

    @Test
    public void shouldReturnAllFilms() throws Exception {
        mockMvc.perform(get("/films"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetFilmById() throws Exception {
        Film film = new Film(1, "Movie-1", "Comedy", LocalDate.of(2021, 12, 21), 600, new HashSet<>());
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());


        mockMvc.perform(get("/films/1"))
                .andDo(print())
                .andExpect(content().string("{\"id\":1,\"name\":\"Movie-1\",\"description\":\"Comedy\",\"releaseDate\":\"2021-12-21\",\"duration\":600,\"likes\":[1]}"));

    }

    @Test
    public void shouldPutLikeToFilm() throws Exception {
        Film film = new Film(1, "Movie-1", "Comedy", LocalDate.of(2021, 12, 21), 100, new HashSet<>());
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        User user = new User(1L, "email@email.com", "UserLogin", "UserLogin", LocalDate.of(2010, 01, 01), new HashSet<>());
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        mockMvc.perform(put("http://localhost:8081/films/1/like/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("{\"id\":1,\"name\":\"Movie-1\",\"description\":\"Comedy\",\"releaseDate\":\"2021-12-21\",\"duration\":600,\"likes\":[1]}"));

    }

}
