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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc


public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private FilmController controller;
    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void addUserCorrectData() throws Exception {

        User user = User.builder()
                .id(1)
                .email("email@email.com")
                .login("UserLogin1")
                .name("UserName")
                .birthday(LocalDate.of(2010, 01, 01))
                .build();

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }


    @Test
    public void shoudReturnExBlankEmailOrWithoutEmailSimbol() {
        //   User user = new User(1, "email@email.com", "UserLogin1", "UserName", LocalDate.of(2010, 01, 01) );
        User user = User.builder()
                .id(1)
                .email(" ")
                .login("UserLogin1")
                .name("UserName")
                .birthday(LocalDate.of(2010, 01, 01))
                .build();

        Assertions.assertThrows(Exception.class, () -> this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print()));
    }


    @Test
    public void shouldReturnExBlankName() {
        User user = new User(1, "email@email.com", " ", "UserName", LocalDate.of(2010, 01, 01));
        Assertions.assertThrows(Exception.class, () -> this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print()));
    }


    @Test
    public void shouldReturnExDateOfBirthday() throws Exception {
        User user = new User(1, "email@email.com", "UserLogin", "UserName", LocalDate.of(2025, 01, 01));
        Assertions.assertThrows(Exception.class, () -> this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print()));
    }

    @Test
    public void shouldReturnNameFromLogin() throws Exception {
        User user = new User(1, "email@email.com", "UserLogin", null, LocalDate.of(2010, 01, 01));
        User user1 = new User(2, "email@email.com", "UserLogin", "UserLogin", LocalDate.of(2010, 01, 01));
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(user1)));
    }

    @Test
    public void shouldReturnAllUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
