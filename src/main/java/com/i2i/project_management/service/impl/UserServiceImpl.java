package com.i2i.project_management.service.impl;

import com.i2i.project_management.exception.DatabaseException;
import com.i2i.project_management.exception.ValidationException;
import com.i2i.project_management.Dto.RoleDto;
import com.i2i.project_management.Dto.UserDto;
import com.i2i.project_management.Dto.UserUpdateDto;
import com.i2i.project_management.exception.NotFoundException;
import com.i2i.project_management.mapper.UserMapper;
import com.i2i.project_management.model.Role;
import com.i2i.project_management.model.User;
import com.i2i.project_management.repository.RoleRepository;
import com.i2i.project_management.repository.UserRepository;
import com.i2i.project_management.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto createUser(UserDto userDto) throws ValidationException {
        if (userDto == null) {
            throw new ValidationException("User details should not be null");
        }

        if (!StringUtils.hasText(userDto.getEmail())
                || !StringUtils.hasText(userDto.getFirstName())
                || !StringUtils.hasText(userDto.getLastName())
                || !StringUtils.hasText(userDto.getPassword())) {
            throw new ValidationException("Email, first name, last name and password are required");
        }

        log.info("Creating user {}", userDto.getEmail());

        User user = User.builder()
                .email(userDto.getEmail())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .isDeleted(false)
                .build();
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
            List<Role> roles = roleRepository.findAllById(
                    userDto.getRoles().stream()
                            .map(RoleDto::getId)
                            .toList()
            );
            Set<Role> activeRoles = roles.stream()
                    .filter(role -> !Boolean.TRUE.equals(role.getIsDeleted()))
                    .collect(Collectors.toSet());
            user.setRoles(activeRoles);
        } else {
            Role role = roleRepository.findRoleByName("EMPLOYEE")
                    .orElseThrow(() -> new ValidationException("Default EMPLOYEE role not found"));
            user.setRoles(Set.of(role));
        }
        User savedUser = userRepository.save(user);
        log.info("User {} created successfully", savedUser.getEmail());
        return UserMapper.toDto(savedUser);
    }

    @Override
    public UserDto updateUser(UUID userID, UserUpdateDto userUpdateDto) throws ValidationException, NotFoundException {
        if (userUpdateDto == null) throw new ValidationException("User update details should not be null");

        log.info("Updating user {}", userID);
        User user = userRepository.findUserById(userID)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userID));

        if (userUpdateDto.getFirstName() != null) user.setFirstName(userUpdateDto.getFirstName());
        if (userUpdateDto.getLastName() != null) user.setLastName(userUpdateDto.getLastName());
        if (userUpdateDto.getAddress() != null) user.setAddress(userUpdateDto.getAddress());
        if (userUpdateDto.getPhoneNumber() != null) user.setPhoneNumber(userUpdateDto.getPhoneNumber());

        User updatedUser = userRepository.save(user);
        return UserMapper.toDto(updatedUser);
    }

    @Override
    public UserDto findUserById(UUID id) throws ValidationException, DatabaseException {
        if(id == null) throw new ValidationException("User id should not be null");

        User user;
        try {
            user = userRepository.findUserById(id)
                    .orElseThrow(()-> new NotFoundException("User not found with ID: " + id));
            return UserMapper.toDto(user);
        }  catch (Exception e) {
            log.error("Error fetching user {}", id, e);
            throw new DatabaseException("Failed to fetch user for id: " + id);
        }
    }

    @Override
    public List<UserDto> findAllUser() throws DatabaseException, NotFoundException {
        List<UserDto> userDtos;
        try {
            List<User> users = userRepository.findAllUsers();
            if (users.isEmpty()) throw new NotFoundException("User list is Empty");
            userDtos = users.stream()
                    .map(UserMapper :: toDto)
                    .toList();
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching users", e);
            throw new DatabaseException("Failed to fetch users");
        }
        return userDtos;
    }

    @Override
    public void deleteUser(UUID id) throws DatabaseException {
        log.warn("Deleting user {}", id);
        try {
            userRepository.deleteEmployee(id);
        } catch (Exception e) {
            log.error("Error deleting user {}", id, e);
            throw new DatabaseException("Failed to delete user with id: " + id);
        }
    }

    @Override
    public UserDto updateOwnProfile(String email, UserUpdateDto userUpdateDto)
            throws ValidationException, NotFoundException {
        if (!StringUtils.hasText(email)) {
            throw new ValidationException("Authenticated user email is required");
        }
        if (userUpdateDto == null) {
            throw new ValidationException("Update details should not be null");
        }

        User user = userRepository.findActiveByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        boolean updated = false;
        if (StringUtils.hasText(userUpdateDto.getFirstName())) {
            user.setFirstName(userUpdateDto.getFirstName());
            updated = true;
        }
        if (StringUtils.hasText(userUpdateDto.getLastName())) {
            user.setLastName(userUpdateDto.getLastName());
            updated = true;
        }
        if (userUpdateDto.getPhoneNumber() != null) {
            user.setPhoneNumber(userUpdateDto.getPhoneNumber());
            updated = true;
        }
        if (userUpdateDto.getAddress() != null) {
            user.setAddress(userUpdateDto.getAddress());
            updated = true;
        }

        if (!updated) {
            throw new ValidationException("Provide at least one field to update");
        }

        User saved = userRepository.save(user);
        return UserMapper.toDto(saved);
    }

}
