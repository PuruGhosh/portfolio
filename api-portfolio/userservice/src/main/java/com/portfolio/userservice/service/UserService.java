package com.portfolio.userservice.service;

import com.portfolio.userservice.entity.User;
import com.portfolio.userservice.entity.dto.GetUserDto;
import com.portfolio.userservice.entity.dto.UserResponseDto;
import com.portfolio.userservice.exception.UnauthorizedAccessException;
import com.portfolio.userservice.exception.UserNotFoundException;
import com.portfolio.userservice.mappers.UserMapper;
import com.portfolio.userservice.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
//    private final PasswordEncoder encoder;


    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponseDto getUserById(UUID id) {
        if (id == null) {
            log.info("Id is null");
            throw new IllegalArgumentException("User ID must not be null.");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));

        return userMapper.fromUserEntity(user);
    }

    public List<UserResponseDto> getAllUsers(Pageable page) {
        return userRepository.findAll(page).stream()
                .map(userMapper::fromUserEntity)
                .toList();
    }

    public UserResponseDto createUser(GetUserDto userDto) {
        Assert.notNull(userDto, "UserDto must not be null.");

        boolean existingUser = userRepository.existsByEmail(userDto.getEmail());
        if(existingUser){
            throw new IllegalArgumentException("User already exist " + userDto);
        }

        User user = userMapper.fromGetUserDto(userDto);

        User savedUser = userRepository.save(user);
        return userMapper.fromUserEntity(savedUser);
    }

    public UserResponseDto updateUser(UUID id, UserResponseDto userResponseDto) {
        if (id == null || userResponseDto == null) {
            throw new IllegalArgumentException("ID and UserDto must not be null.");
        }

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));

        // Update fields
        Optional.ofNullable(userResponseDto.getName()).ifPresent(existingUser::setName);
        Optional.ofNullable(userResponseDto.getEmail()).ifPresent(existingUser::setEmail);
        Optional.ofNullable(userResponseDto.getBio()).ifPresent(existingUser::setBio);
        Optional.ofNullable(userResponseDto.getRoles()).ifPresent(existingUser::setRoles);
        Optional.ofNullable(userResponseDto.getProjectIds()).ifPresent(existingUser::setProjectIds);
        Optional.ofNullable(userResponseDto.getSkills()).ifPresent(existingUser::setSkillIds);

        User updatedUser = userRepository.save(existingUser);
        return userMapper.fromUserEntity(updatedUser);
    }

    public UserResponseDto deleteUser(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID must not be null.");
        }

        var user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id.toString()));
        userRepository.deleteById(id);
        return userMapper.fromUserEntity(user);
    }
}
