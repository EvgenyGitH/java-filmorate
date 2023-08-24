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
import ru.yandex.practicum.filmorate.storage.dao.FriendStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(value = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//@Sql(value = {"/deleteDb.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class FriendsDbStorageTest {
    final UserStorage userStorage;
    final FriendStorage friendStorage;

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

    @Test
    public void testAddFriend() throws ValidationException {
        userStorage.addUser(user);
        userStorage.addUser(friend);
        friendStorage.addFriend(user.getId(), friend.getId());

        List<User> userFriends = friendStorage.getFriendsByUserId(user.getId());
        assertThat(userFriends)
                .hasSize(1)
                .contains(userStorage.getUserById(friend.getId()));
    }

    @Test
    public void testIsExist() throws ValidationException {
        userStorage.addUser(user);
        userStorage.addUser(friend);
        friendStorage.addFriend(user.getId(), friend.getId());

        boolean isFriend = friendStorage.isExist(user.getId(), friend.getId());
        assertThat(isFriend).isTrue();
    }

    @Test
    public void testGetFriendsByUserId() throws ValidationException {
        userStorage.addUser(user);
        userStorage.addUser(friend);
        friendStorage.addFriend(user.getId(), friend.getId());

        List<User> userFriends = friendStorage.getFriendsByUserId(user.getId());
        assertThat(userFriends)
                .hasSize(1)
                .contains(userStorage.getUserById(friend.getId()));
    }

    @Test
    public void testGetCommonFriends() throws ValidationException {
        userStorage.addUser(user);
        userStorage.addUser(friend);
        userStorage.addUser(commonFriend);
        friendStorage.addFriend(user.getId(), commonFriend.getId());
        friendStorage.addFriend(friend.getId(), commonFriend.getId());

        List<User> usersCommonFriends = friendStorage.getFriendsByUserId(user.getId());
        assertThat(usersCommonFriends)
                .hasSize(1)
                .contains(userStorage.getUserById(commonFriend.getId()));
    }

    @Test
    public void testIsConfirmedAndConfirm() throws ValidationException {
        userStorage.addUser(user);
        userStorage.addUser(friend);
        friendStorage.addFriend(user.getId(), friend.getId());
        friendStorage.addFriend(friend.getId(), user.getId());

        boolean isConfirmed = friendStorage.isConfirmed(user.getId(), friend.getId());
        assertThat(isConfirmed).isTrue();
    }

    @Test
    public void testDeleteFriend() throws ValidationException {
        userStorage.addUser(user);
        userStorage.addUser(friend);

        friendStorage.addFriend(user.getId(), friend.getId());
        assertThat(friendStorage.getFriendsByUserId(user.getId()).size()).isEqualTo(1);

        friendStorage.deleteFriend(user.getId(), friend.getId());
        assertThat(friendStorage.getFriendsByUserId(user.getId()).size()).isEqualTo(0);
    }

}
