package com.i2i.project_management.controller;

import com.i2i.project_management.Dto.UserDto;
import com.i2i.project_management.Dto.UserUpdateDto;
import com.i2i.project_management.exception.DatabaseException;
import com.i2i.project_management.exception.NotFoundException;
import com.i2i.project_management.exception.ValidationException;
import com.i2i.project_management.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto)
            throws ValidationException {
        log.info("Received request to create user with email {}", userDto.getEmail());
        UserDto createdUser = userService.createUser(userDto);

        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable UUID userId,
            @RequestBody UserUpdateDto userUpdateDto)
            throws ValidationException, NotFoundException {

        log.info("Received request to update user {}", userId);
        UserDto updatedUser = userService.updateUser(userId, userUpdateDto);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROJECT_MANAGER')")
    public ResponseEntity<UserDto> findUserById(@PathVariable UUID id)
            throws ValidationException, DatabaseException {

        log.debug("Fetching user with id {}", id);
        UserDto userDto = userService.findUserById(id);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PROJECT_MANAGER')")
    public ResponseEntity<List<UserDto>> getAllUsers()
            throws DatabaseException, NotFoundException {

        log.debug("Fetching all users");
        List<UserDto> users = userService.findAllUser();

        return ResponseEntity.ok(users);
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> updateOwnProfile(
            @RequestBody UserUpdateDto userUpdateDto,
            Authentication authentication) throws ValidationException, NotFoundException {

        log.info("User {} updating own profile", authentication.getName());
        UserDto userDto = userService.updateOwnProfile(authentication.getName(), userUpdateDto);
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable UUID id)
            throws DatabaseException {

        log.warn("Deleting user with id {}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
