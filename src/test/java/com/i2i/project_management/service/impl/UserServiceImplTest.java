package com.i2i.project_management.service.impl;

import com.i2i.project_management.Dto.RoleDto;
import com.i2i.project_management.Dto.UserDto;
import com.i2i.project_management.Dto.UserUpdateDto;
import com.i2i.project_management.exception.DatabaseException;
import com.i2i.project_management.exception.NotFoundException;
import com.i2i.project_management.exception.ValidationException;
import com.i2i.project_management.mapper.UserMapper;
import com.i2i.project_management.model.Role;
import com.i2i.project_management.model.User;
import com.i2i.project_management.repository.RoleRepository;
import com.i2i.project_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, roleRepository, passwordEncoder);
    }

    @Test
    void createUser_shouldPersist_whenValidInput() throws ValidationException {
        //arrange
        UserDto request = new UserDto();
        request.setEmail("user@i2i.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("Secret1!");
        RoleDto roleDto = new RoleDto();
        roleDto.setId(UUID.randomUUID());
        request.setRoles(List.of(roleDto));

        Role role = Role.builder().id(roleDto.getId()).isDeleted(false).build();
        when(roleRepository.findAllById(List.of(roleDto.getId()))).thenReturn(List.of(role));
        when(passwordEncoder.encode("Secret1!")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenReturn(User.builder().id(UUID.randomUUID()).build());

        //act
        UserDto result = userService.createUser(request);

        //assert
        assertNotNull(result);
        verify(passwordEncoder, times(1)).encode("Secret1!");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_shouldAssignDefaultRole_whenRolesMissing() throws ValidationException {
        UserDto request = new UserDto();
        request.setEmail("user@i2i.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("Secret1!");

        Role defaultRole = Role.builder().id(UUID.randomUUID()).isDeleted(false).build();
        when(roleRepository.findRoleByName("EMPLOYEE")).thenReturn(Optional.of(defaultRole));
        when(passwordEncoder.encode("Secret1!")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenReturn(User.builder().id(UUID.randomUUID()).build());

        UserDto result = userService.createUser(request);

        assertNotNull(result);
        verify(roleRepository, times(1)).findRoleByName("EMPLOYEE");
    }

    @Test
    void createUser_shouldThrowValidation_whenMandatoryMissing() {
        UserDto request = new UserDto();

        assertThrows(ValidationException.class, () -> userService.createUser(request));
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowValidation_whenUserDtoIsNull() {
        assertThrows(ValidationException.class, () -> userService.createUser(null));
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void updateUser_shouldPersistChanges_whenValid() throws ValidationException, NotFoundException {
        UUID userId = UUID.randomUUID();
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setFirstName("New");

        User user = User.builder().id(userId).build();
        when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserDto result = userService.updateUser(userId, updateDto);

        assertEquals(userId, result.getId());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUser_shouldThrowValidation_whenPayloadNull() {
        assertThrows(ValidationException.class, () -> userService.updateUser(UUID.randomUUID(), null));
    }

    @Test
    void updateUser_shouldThrowNotFound_whenUserMissing() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findUserById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(userId, new UserUpdateDto()));
    }

    @Test
    void findUserById_shouldReturnDto_whenFound() throws ValidationException, DatabaseException {
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).build();
        when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));

        UserDto result = userService.findUserById(userId);

        assertEquals(userId, result.getId());
    }

    @Test
    void findUserById_shouldThrowValidation_whenIdNull() {
        assertThrows(ValidationException.class, () -> userService.findUserById(null));
        verify(userRepository, times(0)).findUserById(any());
    }

    @Test
    void findUserById_shouldWrapDatabaseException_whenRepositoryFails() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findUserById(userId)).thenThrow(new RuntimeException("db"));

        assertThrows(DatabaseException.class, () -> userService.findUserById(userId));
    }

    @Test
    void findAllUser_shouldReturnDtos_whenUsersPresent() throws DatabaseException, NotFoundException {
        User user = User.builder().id(UUID.randomUUID()).build();
        when(userRepository.findAllUsers()).thenReturn(List.of(user));

        List<UserDto> result = userService.findAllUser();

        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAllUsers();
    }

    @Test
    void findAllUser_shouldThrowNotFound_whenEmpty() {
        when(userRepository.findAllUsers()).thenReturn(List.of());

        assertThrows(NotFoundException.class, () -> userService.findAllUser());
    }

    @Test
    void findAllUser_shouldWrapDatabaseException_whenRepositoryFails() {
        when(userRepository.findAllUsers()).thenThrow(new RuntimeException("db"));

        assertThrows(DatabaseException.class, () -> userService.findAllUser());
    }

    @Test
    void deleteUser_shouldInvokeRepository() throws DatabaseException {
        UUID userId = UUID.randomUUID();

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteEmployee(userId);
    }

    @Test
    void deleteUser_shouldWrapDatabaseException_whenRepositoryFails() {
        UUID userId = UUID.randomUUID();
        doThrow(new RuntimeException("db")).when(userRepository).deleteEmployee(userId);

        assertThrows(DatabaseException.class, () -> userService.deleteUser(userId));
    }

    @Test
    void updateOwnProfile_shouldPersistAllowedFields() throws ValidationException, NotFoundException {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setAddress("New addr");
        updateDto.setLastName("test");
        updateDto.setFirstName("test");
        updateDto.setPhoneNumber("1234567899");
        User user = User.builder().id(UUID.randomUUID()).build();
        when(userRepository.findActiveByEmail("user@i2i.com")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserDto result = userService.updateOwnProfile("user@i2i.com", updateDto);

        assertNotNull(result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateOwnProfile_shouldThrowValidation_whenEmailMissing() {
        assertThrows(ValidationException.class, () -> userService.updateOwnProfile("", new UserUpdateDto()));
    }

    @Test
    void updateOwnProfile_shouldThrowValidation_whenUserDtoIsNull() {
        assertThrows(ValidationException.class, () -> userService.updateOwnProfile("user@i2i.com", null));
    }

    @Test
    void updateOwnProfile_shouldThrowValidation_whenNoFieldsProvided() {
        when(userRepository.findActiveByEmail("user@i2i.com")).thenReturn(Optional.of(User.builder().build()));

        assertThrows(ValidationException.class, () -> userService.updateOwnProfile("user@i2i.com", new UserUpdateDto()));
    }

    @Test
    void updateOwnProfile_shouldThrowNotFound_whenUserMissing() {
        when(userRepository.findActiveByEmail("user@i2i.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateOwnProfile("user@i2i.com", new UserUpdateDto()));
    }


}

