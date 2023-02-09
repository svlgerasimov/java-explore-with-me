package ru.practicum.ewm.main.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.user.dto.UserDtoIn;
import ru.practicum.ewm.main.user.dto.UserDtoOut;
import ru.practicum.ewm.main.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping("/admin/users")
    public List<UserDtoOut> find(
            @RequestParam(defaultValue = "") List<Long> ids,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        return userService.find(ids, from, size);
    }

    @PostMapping("/admin/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDtoOut post(@Valid @RequestBody UserDtoIn userDtoIn) {
        return userService.add(userDtoIn);
    }

    @DeleteMapping("admin/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
