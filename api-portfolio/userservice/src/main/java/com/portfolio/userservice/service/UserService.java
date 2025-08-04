package com.portfolio.userservice.service;

import com.portfolio.userservice.entity.User;
import com.portfolio.userservice.entity.dto.GetUserDto;
import com.portfolio.userservice.entity.dto.UserDto;
import com.portfolio.userservice.exception.UnauthorizedAccessException;
import com.portfolio.userservice.exception.UserNotFoundException;
import com.portfolio.userservice.mappers.UserMapper;
import com.portfolio.userservice.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;


    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.encoder = encoder;
    }

    public UserDto getUserById(UUID id) {
        if (id == null) {
            log.info("Id is null");
            throw new IllegalArgumentException("User ID must not be null.");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));

        return userMapper.fromUserEntity(user);
    }

    public List<UserDto> getAllUsers(Pageable page) {
        return userRepository.findAll(page).stream()
                .map(userMapper::fromUserEntity)
                .toList();
    }

    public UserDto createUser(GetUserDto userDto) {
        Assert.notNull(userDto, "UserDto must not be null.");

        boolean existingUser = userRepository.existsByUsername(userDto.getUserName()) || userRepository.existsByEmail(userDto.getEmail());
        if(existingUser){
            throw new UnauthorizedAccessException("User already exist " + userDto);
        }
    // Map DTO to entity and save
    userDto.setPassword(encoder.encode(userDto.getPassword()));
        User user = userMapper.fromGetUserDto(userDto);

        User savedUser = userRepository.save(user);
        return userMapper.fromUserEntity(savedUser);
    }

    public UserDto updateUser(UUID id, UserDto userDto) {
        if (id == null || userDto == null) {
            throw new IllegalArgumentException("ID and UserDto must not be null.");
        }

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));

        // Update fields
        Optional.ofNullable(userDto.getName()).ifPresent(existingUser::setName);
        Optional.ofNullable(userDto.getEmail()).ifPresent(existingUser::setEmail);
        Optional.ofNullable(userDto.getBio()).ifPresent(existingUser::setBio);
        Optional.ofNullable(userDto.getRole()).ifPresent(existingUser::setRole);
        Optional.ofNullable(userDto.getProjects()).ifPresent(existingUser::setProjectIds);
        Optional.ofNullable(userDto.getSkills()).ifPresent(existingUser::setSkillIds);

        User updatedUser = userRepository.save(existingUser);
        return userMapper.fromUserEntity(updatedUser);
    }

    public UserDto deleteUser(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID must not be null.");
        }

        var user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id.toString()));
        userRepository.deleteById(id);
        return userMapper.fromUserEntity(user);
    }
}
