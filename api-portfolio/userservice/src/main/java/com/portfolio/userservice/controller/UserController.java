package com.portfolio.userservice.controller;

import com.portfolio.userservice.entity.dto.GetUserDto;
import com.portfolio.userservice.entity.dto.ResponseDto;
import com.portfolio.userservice.entity.dto.UserDto;
import com.portfolio.userservice.service.UserService;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseDto<UserDto>> getUserById(@PathVariable UUID id) {
    log.info("Request received: GET user by id {}", id);
    UserDto user = userService.getUserById(id);
    log.info("User fetched successfully for id {}", id);
    return ResponseEntity.ok(ResponseDto.success(user));
  }

  @GetMapping("/getAll")
  public ResponseEntity<ResponseDto<List<UserDto>>> getAllUsers(Pageable page) {
    log.info("Request received: GET all users with pagination - page: {}, size: {}", page.getPageNumber(), page.getPageSize());
    List<UserDto> users = userService.getAllUsers(page);
    log.info("Fetched {} users", users.size());
    return ResponseEntity.ok(ResponseDto.success(users));
  }

  @PostMapping("/create")
  public ResponseEntity<ResponseDto<UserDto>> createUser(@RequestBody GetUserDto userDto) {
    log.info("Request received: CREATE user - payload: {}", userDto);
    UserDto createdUser = userService.createUser(userDto);
    log.info("User created with ID: {}", createdUser.getId());
    return ResponseEntity.ok(ResponseDto.success(createdUser));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ResponseDto<UserDto>> updateUser(
          @PathVariable UUID id, @RequestBody UserDto userDto) {
    log.info("Request received: UPDATE user with id {} - payload: {}", id, userDto);
    UserDto updatedUser = userService.updateUser(id, userDto);
    log.info("User updated successfully with id {}", id);
    return ResponseEntity.ok(ResponseDto.success(updatedUser));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ResponseDto<UserDto>> deleteUser(@PathVariable UUID id) {

    log.info("Request received: DELETE user with id {}", id);
    UserDto deletedUser = userService.deleteUser(id);
    log.info("User deleted successfully with id {}", id);
    return ResponseEntity.ok(ResponseDto.success(deletedUser));
  }
}
