package ru.practicum.ewm.main.user.model;

import org.junit.jupiter.api.Test;
import ru.practicum.ewm.main.user.dto.UserDtoIn;
import ru.practicum.ewm.main.user.dto.UserDtoOut;

import static org.assertj.core.api.Assertions.assertThat;

class UserDtoMapperTest {

    private final UserDtoMapper mapper = new UserDtoMapperImpl();
    private final String email = "mail@mail.com";
    private final String name = "User Name";

    @Test
    void fromDtoTest() {
        UserDtoIn userDtoIn = UserDtoIn.builder()
                .email(email)
                .name(name)
                .build();

        UserEntity userEntity = mapper.fromDto(userDtoIn);

        assertThat(userEntity)
                .extracting("id", "email", "name")
                .containsExactly(null, email, name);
    }

    @Test
    void toDtoTest() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail(email);
        userEntity.setName(name);

        UserDtoOut userDtoOut = mapper.toDto(userEntity);

        assertThat(userDtoOut).isEqualTo(
                UserDtoOut.builder()
                        .id(1L)
                        .email(email)
                        .name(name)
                        .build()
        );
    }
}