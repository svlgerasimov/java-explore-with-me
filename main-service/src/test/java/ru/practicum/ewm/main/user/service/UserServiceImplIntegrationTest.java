package ru.practicum.ewm.main.user.service;

import lombok.RequiredArgsConstructor;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.user.dto.UserDtoIn;
import ru.practicum.ewm.main.user.dto.UserDtoOut;
import ru.practicum.ewm.main.user.model.UserEntity;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class UserServiceImplIntegrationTest {

    private final UserServiceImpl userService;
    private final EntityManager em;

    @Test
    @Sql(scripts = "/ru/practicum/ewm/main/sql/clear-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void saveUserTest() {
        String email = "mail@mail.com";
        String name = "User Name";
        UserDtoIn userDtoIn = UserDtoIn.builder()
                .email(email)
                .name(name)
                .build();

        userService.saveUser(userDtoIn);

        List<UserEntity> userEntities =
                em.createQuery("select u from UserEntity u", UserEntity.class)
                        .getResultList();

        assertThat(userEntities)
                .hasSize(1)
                .element(0)
                .extracting("email", "name")
                .containsExactly(email, name);

        assertThat(userEntities)
                .extracting("id")
                .doesNotContainNull();
    }

    @Test
    @Sql(scripts = "/ru/practicum/ewm/main/sql/clear-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void saveUser_whenSaveUserWithSameEmail_thenThrowDataIntegrityViolationException() {
        String email = "mail@mail.com";
        UserDtoIn userDtoIn1 = UserDtoIn.builder()
                .email(email)
                .name("User Name 1")
                .build();
        UserDtoIn userDtoIn2 = UserDtoIn.builder()
                .email(email)
                .name("User Name 2")
                .build();

        userService.saveUser(userDtoIn1);

        assertThatThrownBy(() -> userService.saveUser(userDtoIn2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @Sql(scripts = "/ru/practicum/ewm/main/sql/clear-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/ru/practicum/ewm/main/sql/get-users-prepare.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findUsers_noIdFiltersAndAllUsersInPage() {
        List<UserDtoOut> userDtoOuts = userService.findUsers(Collections.emptyList(), 0, 10);

        assertThat(userDtoOuts)
                .extracting("id", "name", "email")
                .containsExactlyInAnyOrder(
                        Tuple.tuple(1L, "name1", "mail1@mail.com"),
                        Tuple.tuple(2L, "name2", "mail2@mail.com"),
                        Tuple.tuple(3L, "name3", "mail3@mail.com"),
                        Tuple.tuple(4L, "name4", "mail4@mail.com"),
                        Tuple.tuple(5L, "name5", "mail5@mail.com")
                );
    }

    @Test
    @Sql(scripts = "/ru/practicum/ewm/main/sql/clear-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/ru/practicum/ewm/main/sql/get-users-prepare.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findUsers_noIdFiltersAndPartOfUsersInPage() {
        List<UserDtoOut> userDtoOuts = userService.findUsers(Collections.emptyList(), 2, 2);

        assertThat(userDtoOuts)
                .extracting("id", "name", "email")
                .hasSize(2);
    }

    @Test
    @Sql(scripts = "/ru/practicum/ewm/main/sql/clear-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/ru/practicum/ewm/main/sql/get-users-prepare.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findUsers_withIdFiltersAndAllUsersInPage() {
        List<UserDtoOut> userDtoOuts = userService.findUsers(List.of(2L, 3L, 4L), 0, 10);

        assertThat(userDtoOuts)
                .extracting("id", "name", "email")
                .containsExactlyInAnyOrder(
                        Tuple.tuple(2L, "name2", "mail2@mail.com"),
                        Tuple.tuple(3L, "name3", "mail3@mail.com"),
                        Tuple.tuple(4L, "name4", "mail4@mail.com")
                );
    }

    @Test
    @Sql(scripts = "/ru/practicum/ewm/main/sql/clear-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/ru/practicum/ewm/main/sql/get-users-prepare.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findUsers_withIdFiltersAndPartUsersInPage() {
        List<UserDtoOut> userDtoOuts = userService.findUsers(List.of(2L, 3L, 4L), 2, 1);

        assertThat(userDtoOuts)
                .extracting("id", "name", "email")
                .hasSize(1);
    }

    @Test
    @Sql(scripts = "/ru/practicum/ewm/main/sql/clear-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/ru/practicum/ewm/main/sql/get-users-prepare.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteUserTest() {
        userService.deleteUser(3L);

        List<UserEntity> userEntities =
                em.createQuery("select u from UserEntity u", UserEntity.class)
                        .getResultList();

        assertThat(userEntities)
                .hasSize(4)
                .extracting("id")
                .doesNotContain(3L);
    }

    @Test
    @Sql(scripts = "/ru/practicum/ewm/main/sql/clear-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/ru/practicum/ewm/main/sql/get-users-prepare.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteUser_whenDeleteUserByAbsentId_thenThrowNotFoundException() {
        assertThatThrownBy(() -> userService.deleteUser(10000L))
                .isInstanceOf(NotFoundException.class);
    }
}