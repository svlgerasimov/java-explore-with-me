package ru.practicum.ewm.main.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.user.dto.UserDtoIn;
import ru.practicum.ewm.main.user.dto.UserDtoOut;
import ru.practicum.ewm.main.user.model.UserDtoMapper;
import ru.practicum.ewm.main.user.model.UserEntity;
import ru.practicum.ewm.main.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;

    @Override
    public List<UserDtoOut> find(List<Long> ids, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return userDtoMapper.toDto(
                ids.isEmpty() ? userRepository.findAll(pageable).getContent()
                        : userRepository.findAllByIdIn(ids, pageable)
        );
    }

    @Override
    @Transactional
    public UserDtoOut add(UserDtoIn userDtoIn) {
        UserEntity userEntity = userDtoMapper.fromDto(userDtoIn);
        userEntity = userRepository.save(userEntity);
        UserDtoOut userDtoOut = userDtoMapper.toDto(userEntity);
        log.debug("Add user {}", userDtoOut);
        return userDtoOut;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findUserEntity(id);
        log.debug("Delete user with id={}", id);
        userRepository.deleteById(id);
    }

    private UserEntity findUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " was not found."));
    }
}
