package ru.yandex.practicum.filmorate.storageDaoTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
//@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)

@Sql(value = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//@Sql(value = {"/deleteDb.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)

public class UserDbStorageTest {

    final UserStorage userStorage;
    private User user;
    private User friend;
    private User commonFriend;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .email("one@yandex.ru")
                .login("one")
                .name("One")
                .birthday(LocalDate.of(2001, 01, 01))
                .build();

        friend = User.builder()
                .email("two@yandex.ru")
                .login("two")
                .name("Two")
                .birthday(LocalDate.of(2002, 02, 02))
                .build();

        commonFriend = User.builder()
                .email("three@yandex.ru")
                .login("three")
                .name("Three")
                .birthday(LocalDate.of(2003, 03, 03))
                .build();

    }

  /*  @AfterEach
    void afterEach() {
        userStorage.deleteAllUsers();
    }*/

    @Test
    public void testAddUser() throws ValidationException {
        User addedUser = userStorage.addUser(user);
        assertThat(addedUser.getId()).isEqualTo(1L);
    }


    @Test
    public void testUpdateUser() throws ValidationException {
        User addedUser = userStorage.addUser(user);
        User updatedUser = User.builder()
                .email("onemillion@yandex.ru")
                .login("one million")
                .name("One Million")
                .birthday(LocalDate.of(2001, 01, 01))
                .build();
        updatedUser.setId(addedUser.getId());
        User update = userStorage.updateUser(updatedUser);
        assertThat(update).hasFieldOrPropertyWithValue("email", "onemillion@yandex.ru")
                .hasFieldOrPropertyWithValue("login", "one million")
                .hasFieldOrPropertyWithValue("name", "One Million")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2001, 01, 01));

    }

    @Test
    public void testGetAllUser() throws ValidationException {
        User addedUser = userStorage.addUser(user);
        User addedFriend = userStorage.addUser(friend);
        User addedCommonFriend = userStorage.addUser(commonFriend);
        List<User> allUser = userStorage.allUser();
        assertThat(allUser.size()).isEqualTo(3);
    }

    @Test
    public void getUserById() throws ValidationException {
        User addedUser = userStorage.addUser(user);
        User userOptional = userStorage.getUserById(addedUser.getId());
        assertThat(userOptional).hasFieldOrPropertyWithValue("id", addedUser.getId());
    }


    @Test
    void testExistsById() throws ValidationException {
        assertFalse(userStorage.existUser(1L));
        User addedUser = userStorage.addUser(user);
        Long userId = addedUser.getId();
        assertTrue(userStorage.existUser(userId));
    }


}
