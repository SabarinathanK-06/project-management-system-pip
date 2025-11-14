package com.project_management.test.service.impl;

import com.project_management.test.Dto.RoleDto;
import com.project_management.test.Dto.UserDto;
import com.project_management.test.Dto.UserUpdateDto;
import com.project_management.test.exception.DatabaseException;
import com.project_management.test.exception.NotFoundException;
import com.project_management.test.exception.ValidationException;
import com.project_management.test.mapper.UserMapper;
import com.project_management.test.model.Role;
import com.project_management.test.model.User;
import com.project_management.test.repository.RoleRepository;
import com.project_management.test.repository.UserRepository;
import com.project_management.test.service.UserService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDto createUser(UserDto userDto) throws ValidationException {
        if (userDto == null) {
            throw new ValidationException("User details should not be null");
        }

        if (userDto.getEmail() != null && userDto.getFirstName() != null && userDto.getLastName() != null) {
            throw new ValidationException("User email and name should not be null");
        }

        User user = User.builder()
                .email(userDto.getEmail())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .isDeleted(false)
                .build();

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
        return UserMapper.toDto(savedUser);
    }

    @Override
    public UserDto updateUser(UUID userID, UserUpdateDto userUpdateDto) throws ValidationException, NotFoundException {
        if (userUpdateDto == null) throw new ValidationException("User update details should not be null");

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
            throw new DatabaseException("Failed to fetch users");
        }
        return userDtos;
    }

    @Override
    public void deleteUser(UUID id) throws DatabaseException {
        try {
            userRepository.deleteEmployee(id);
        } catch (Exception e) {
            throw new DatabaseException("Failed to delete user with id: " + id);
        }
    }

}
