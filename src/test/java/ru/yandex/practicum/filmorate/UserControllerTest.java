package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc


public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserController controller;

    @Autowired
    UserService userService;

    @Autowired
    UserStorage userStorage;
    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void addUserCorrectData() throws Exception {

        User user = User.builder()
                .id(3L)
                .email("email@email.com")
                .login("UserLogin1")
                .name("UserName")
                .birthday(LocalDate.of(2010, 01, 01))
                .friends(new HashSet<>())
                .build();

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }


    @Test
    public void shoudReturnExBlankEmailOrWithoutEmailSimbol() throws Exception {

        User user = User.builder()
                .id(1L)
                .email(" ")
                .login("UserLogin1")
                .name("UserName")
                .birthday(LocalDate.of(2010, 01, 01))
                .friends(new HashSet<>())
                .build();

        ResultActions response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));
        response.andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));

    }


    @Test
    public void shouldReturnExBlankName() throws Exception {
        User user = new User(1L, "email@email.com", " ", "UserName", LocalDate.of(2010, 01, 01), new HashSet<>());

        ResultActions response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));
        response.andExpect(status().isBadRequest());
        response.andExpect(content().json(objectMapper.writeValueAsString(Map.of("error",
                "логин не может быть пустым и содержать пробелы"))));


    }


    @Test
    public void shouldReturnExDateOfBirthday() throws Exception {
        User user = new User(1L, "email@email.com", "UserLogin", "UserName", LocalDate.of(2025, 01, 01), new HashSet<>());

        ResultActions response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));
        response.andExpect(status().isBadRequest());
        response.andExpect(content().json(objectMapper.writeValueAsString(Map.of("error",
                "дата рождения не может быть в будущем"))));
    }

    @Test
    public void shouldReturnNameFromLogin() throws Exception {
        User user = new User(1L, "email@email.com", "UserLogin", null, LocalDate.of(2010, 01, 01), new HashSet<>());
        User user1 = new User(6L, "email@email.com", "UserLogin", "UserLogin", LocalDate.of(2010, 01, 01), new HashSet<>());
        mockMvc.perform(post("/users")
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

    @Test
    public void shouldUpDateUser() throws Exception {
        User user = new User(3L, "email@email.com", "UserLogin", "UserLogin", LocalDate.of(2010, 01, 01), new HashSet<>());
        User user1 = new User(2L, "newEmail@email.com", "UserLogin", "UserLogin", LocalDate.of(2010, 01, 01), new HashSet<>());
        User user2 = new User(2L, "newEmail@email.com", "UserLogin", "UserLogin", LocalDate.of(2010, 01, 01), new HashSet<>());
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());


        mockMvc.perform(put("/users")
                        .content(objectMapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user2)));

    }

    @Test
    public void shouldGetUserById() throws Exception {
        User user = new User(1L, "email@email.com", "UserLogin", "UserLogin", LocalDate.of(2010, 01, 01), new HashSet<>());
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());


        mockMvc.perform(get("/users/1"))
                .andDo(print())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));

    }


    @Test
    public void shouldPutLikeToFilm() throws Exception {
        User user = new User(1L, "email@email.com", "UserLogin", "UserLogin", LocalDate.of(2010, 01, 01), new HashSet<>());
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        User user1 = new User(1L, "e2mail@email.com", "User2Login", "User2Login", LocalDate.of(2000, 05, 05), new HashSet<>());
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        this.mockMvc.perform(put("/users/1/friends/2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("{\"id\":1,\"email\":\"email@email.com\",\"login\":\"UserLogin\",\"name\":\"UserLogin\",\"birthday\":\"2010-01-01\",\"friends\":[2]}"));

    }

}
