package ru.yandex.practicum.filmorate;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private ObjectMapper objectMapper;


    @Test
    public void addFilmCorrectData() throws Exception {
        //  Film film1 = new Film(1, "Movie-1", "Comedy", LocalDate.of(2021, 12, 21), 600);
        Film film0 = Film.builder()
                .id(1)
                .name("Movie-1")
                .description("Comedy")
                .releaseDate(LocalDate.of(2023, 01, 01))
                .duration(600)
                .build();

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film0))
                                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(film0)));
    }


    @Test
    public void shoudReturnExReleaseDateAddFilm() {
        Film film1 = new Film(1, "Movie-1", "Comedy", LocalDate.of(1021, 12, 21), 600);
        Assertions.assertThrows(Exception.class, () -> this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film1)))
                .andDo(print()));
    }


    @Test
    public void shouldReturnExBlankName() throws Exception {
        Film film1 = new Film(1, " ", "Comedy", LocalDate.of(2021, 12, 21), 600);
        Assertions.assertThrows(Exception.class, () -> this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film1)))
                .andDo(print()));
    }

    @Test
    public void shouldReturnExNegativeDuration() throws Exception {
        Film film1 = new Film(1, "Movie-1", "Comedy", LocalDate.of(2021, 12, 21), -600);
        Assertions.assertThrows(Exception.class, () -> this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film1)))
                .andDo(print()));
    }

    @Test
    public void shouldReturnExLongDescription() throws Exception {
        Film film1 = new Film(1, "Movie-1", "Comedy".repeat(210), LocalDate.of(2021, 12, 21), -600);
        Assertions.assertThrows(Exception.class, () -> this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film1)))
                .andDo(print()));
    }

    @Test
    public void shouldReturnAllFilms() throws Exception {
        mockMvc.perform(get("/films"))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
