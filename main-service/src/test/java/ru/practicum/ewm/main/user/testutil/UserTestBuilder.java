package ru.practicum.ewm.main.user.testutil;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.practicum.ewm.main.user.dto.UserDtoIn;
import ru.practicum.ewm.main.user.dto.UserDtoOut;
import ru.practicum.ewm.main.user.dto.UserDtoOutShort;
import ru.practicum.ewm.main.user.model.UserEntity;

@NoArgsConstructor(staticName = "defaultBuilder")
@Getter
@Setter
@Accessors(chain = true, fluent = true)
public class UserTestBuilder {
    private Long id = 1L;
    private String email = "mail1@mail.com";
    private String name = "User Name 1";

    public UserDtoOut buildDtoOut() {
        return UserDtoOut.builder()
                .id(id)
                .email(email)
                .name(name)
                .build();
    }

    public UserDtoIn buildDtoIn() {
        return UserDtoIn.builder()
                .email(email)
                .name(name)
                .build();
    }

    public UserDtoOutShort buildDtoOutShort() {
        return UserDtoOutShort.builder()
                .id(id)
                .name(name)
                .build();
    }

    public UserEntity buildUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(id);
        userEntity.setEmail(email);
        userEntity.setName(name);
        return userEntity;
    }
}
