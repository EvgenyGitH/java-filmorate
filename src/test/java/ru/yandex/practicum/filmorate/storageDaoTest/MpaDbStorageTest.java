package ru.yandex.practicum.filmorate.storageDaoTest;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(value = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//@Sql(value = {"/deleteDb.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class MpaDbStorageTest {
    final MpaStorage mpaStorage;

    @Test
    public void testGetAllMpa() {
        List<Mpa> allMpa = mpaStorage.getAllMpa();
        assertThat(allMpa.size()).isEqualTo(5);
    }

    @Test
    public void testGetMpaById() {
        assertThat(mpaStorage.getMpaById(2))
                .hasFieldOrPropertyWithValue("id", 2)
                .hasFieldOrPropertyWithValue("name", "PG");
    }

}
