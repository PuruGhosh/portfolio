package com.portfolio.userservice.controller;

import com.portfolio.userservice.entity.dto.GetUserDto;
import com.portfolio.userservice.entity.dto.ResponseDto;
import com.portfolio.userservice.entity.dto.UserResponseDto;
import com.portfolio.userservice.service.UserService;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseDto<UserResponseDto>> getUserById(@PathVariable UUID id, @RequestHeader("jwt-claim") String token) {
    log.info("Request received: GET user by id {}", id);
    log.info("token: {}",token);
    UserResponseDto user = userService.getUserById(id);
    log.info("User fetched successfully for id {}", id);
    return ResponseEntity.ok(ResponseDto.success(user));
  }

  @GetMapping("/getAll")
  public ResponseEntity<ResponseDto<List<UserResponseDto>>> getAllUsers(Pageable page) {
    log.info("Request received: GET all users with pagination - page: {}, size: {}", page.getPageNumber(), page.getPageSize());
    List<UserResponseDto> users = userService.getAllUsers(page);
    log.info("Fetched {} users", users.size());
    return ResponseEntity.ok(ResponseDto.success(users));
  }

  @PostMapping("/create")
  public ResponseEntity<ResponseDto<UserResponseDto>> createUser(@RequestBody GetUserDto userDto) {
    log.info("Request received: CREATE user - payload: {}", userDto);
    UserResponseDto createdUser = userService.createUser(userDto);
    log.info("User created with ID: {}", createdUser.getId());
    return ResponseEntity.ok(ResponseDto.success(createdUser));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ResponseDto<UserResponseDto>> updateUser(
          @PathVariable UUID id, @RequestBody UserResponseDto userResponseDto) {
    log.info("Request received: UPDATE user with id {} - payload: {}", id, userResponseDto);
    UserResponseDto updatedUser = userService.updateUser(id, userResponseDto);
    log.info("User updated successfully with id {}", id);
    return ResponseEntity.ok(ResponseDto.success(updatedUser));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ResponseDto<UserResponseDto>> deleteUser(@PathVariable UUID id) {

    log.info("Request received: DELETE user with id {}", id);
    UserResponseDto deletedUser = userService.deleteUser(id);
    log.info("User deleted successfully with id {}", id);
    return ResponseEntity.ok(ResponseDto.success(deletedUser));
  }
}
