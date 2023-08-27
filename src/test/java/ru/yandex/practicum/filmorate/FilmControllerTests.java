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
import ru.yandex.practicum.filmorate.model.Mpa;
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

        Film film0 = Film.builder()
                .name("movie1")
                .description("movie1")
                .releaseDate(LocalDate.of(2001, 01, 01))
                .duration(121)
                .mpa(new Mpa(2))
                .build();

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film0))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));

        mockMvc.perform(get("/films/1"))
                .andDo(print())
                .andExpect(content().string("{\"id\":1,\"name\":\"movie1\",\"description\":\"movie1\",\"releaseDate\":\"2001-01-01\",\"duration\":121,\"genres\":[],\"mpa\":{\"id\":2,\"name\":\"PG\"},\"likes\":[]}"));

    }


    @Test
    public void shoudReturnExReleaseDateAddFilm() throws Exception {
        Film film = Film.builder()
                .name("Movie-1")
                .description("Comedy")
                .releaseDate(LocalDate.of(1021, 12, 21))
                .duration(600)
                .build();

        ResultActions response = mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)));
        response.andExpect(status().isBadRequest());
        response.andExpect(content().json(objectMapper.writeValueAsString(Map.of("error",
                "дата релиза — не раньше 28 декабря 1895 года"))));

    }


    @Test
    public void shouldReturnExBlankName() throws Exception {
        Film film = Film.builder()
                .name(" ")
                .description("Comedy")
                .releaseDate(LocalDate.of(2021, 12, 21))
                .duration(600)
                .likes(new HashSet<>())
                .build();

        ResultActions response = mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)));
        response.andExpect(status().isBadRequest());
        response.andExpect(content().json(objectMapper.writeValueAsString(Map.of("error",
                "название не может быть пустым"))));
    }

    @Test
    public void shouldReturnExNegativeDuration() throws Exception {
        Film film = Film.builder()
                .name("Movie-1")
                .description("Comedy")
                .releaseDate(LocalDate.of(2021, 12, 21))
                .duration(-600)
                .build();
        ResultActions response = mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)));
        response.andExpect(status().isBadRequest());
        response.andExpect(content().json(objectMapper.writeValueAsString(Map.of("error",
                "продолжительность фильма должна быть положительной"))));
    }

    @Test
    public void shouldReturnExLongDescription() throws Exception {
        Film film = Film.builder()
                .name("Movie-1")
                .description("Comedy".repeat(210))
                .releaseDate(LocalDate.of(2021, 12, 21))
                .duration(600)
                .likes(new HashSet<>())
                .build();

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
        Film film = Film.builder()
                .name("movie1ById")
                .description("movie1")
                .releaseDate(LocalDate.of(2001, 01, 01))
                .duration(121)
                .mpa(new Mpa(2))
                .build();
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        mockMvc.perform(get("/films/3"))
                .andDo(print())
                .andExpect(content().string("{\"id\":3,\"name\":\"movie1ById\",\"description\":\"movie1\",\"releaseDate\":\"2001-01-01\",\"duration\":121,\"genres\":[],\"mpa\":{\"id\":2,\"name\":\"PG\"},\"likes\":[]}"));

    }

    @Test
    public void shouldPutLikeToFilm() throws Exception {
        Film film = Film.builder()
                .name("movie1Like")
                .description("movie1")
                .releaseDate(LocalDate.of(2001, 01, 01))
                .duration(121)
                .mpa(new Mpa(2))
                .build();
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        User user = new User(1L, "email@email.com", "UserLogin", "UserLogin", LocalDate.of(2010, 01, 01));
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        mockMvc.perform(put("http://localhost:8081/films/2/like/1"))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/films/2"))
                .andDo(print())
                .andExpect(content().string("{\"id\":2,\"name\":\"movie1Like\",\"description\":\"movie1\",\"releaseDate\":\"2001-01-01\",\"duration\":121,\"genres\":[],\"mpa\":{\"id\":2,\"name\":\"PG\"},\"likes\":[1]}"));

    }

}
