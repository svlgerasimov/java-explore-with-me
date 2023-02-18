package ru.practicum.ewm.main.user.service;

import ru.practicum.ewm.main.user.dto.UserDtoIn;
import ru.practicum.ewm.main.user.dto.UserDtoOut;

import java.util.List;

public interface UserService {
    List<UserDtoOut> find(List<Long> ids, Integer from, Integer size);

    UserDtoOut add(UserDtoIn userDtoIn);

    void delete(Long id);
}
