package com.i2i.project_management.service.impl;

import com.i2i.project_management.Dto.RoleDto;
import com.i2i.project_management.Dto.UserDto;
import com.i2i.project_management.exception.DatabaseException;
import com.i2i.project_management.exception.NotFoundException;
import com.i2i.project_management.exception.ValidationException;
import com.i2i.project_management.model.Role;
import com.i2i.project_management.model.User;
import com.i2i.project_management.repository.RoleRepository;
import com.i2i.project_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    private RoleServiceImpl roleService;

    @BeforeEach
    void setUp() {
        roleService = new RoleServiceImpl(roleRepository, userRepository);
    }

    @Test
    void createRole_shouldReturnDto_whenValid() throws ValidationException, DatabaseException {
        //arrange
        RoleDto request = new RoleDto();
        request.setName("ADMIN");
        Role saved = Role.builder()
                .id(UUID.randomUUID())
                .name("ADMIN")
                .build();
        when(roleRepository.save(any(Role.class))).thenReturn(saved);

        //act
        RoleDto result = roleService.createRole(request);

        //assert
        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void createRole_shouldThrowValidation_whenNameMissing() {
        RoleDto request = new RoleDto();

        assertThrows(ValidationException.class, () -> roleService.createRole(request));
        verify(roleRepository, times(0)).save(any(Role.class));
    }

    @Test
    void createRole_shouldWrapDatabaseException_whenSaveFails() {
        RoleDto request = new RoleDto();
        request.setName("ADMIN");
        when(roleRepository.save(any(Role.class))).thenThrow(new RuntimeException("db"));

        assertThrows(DatabaseException.class, () -> roleService.createRole(request));
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void updateRole_shouldReturnDto_whenValid() throws ValidationException, NotFoundException, DatabaseException {
        UUID roleId = UUID.randomUUID();
        RoleDto request = new RoleDto();
        request.setId(roleId);
        request.setName("EMP");

        Role existing = Role.builder()
                .id(roleId)
                .name("OLD")
                .isDeleted(false)
                .build();

        when(roleRepository.findRoleById(roleId)).thenReturn(Optional.of(existing));
        when(roleRepository.save(existing)).thenReturn(existing);

        RoleDto result = roleService.updateRole(request);

        assertEquals(roleId, result.getId());
        verify(roleRepository, times(1)).save(existing);
    }

    @Test
    void updateRole_shouldThrowValidation_whenIdMissing() {
        RoleDto request = new RoleDto();

        assertThrows(ValidationException.class, () -> roleService.updateRole(request));
        verify(roleRepository, times(0)).findRoleById(any());
    }

    @Test
    void updateRole_shouldThrowNotFound_whenRoleMissing() {
        UUID roleId = UUID.randomUUID();
        RoleDto request = new RoleDto();
        request.setId(roleId);
        when(roleRepository.findRoleById(roleId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> roleService.updateRole(request));
        verify(roleRepository, times(1)).findRoleById(roleId);
    }

    @Test
    void updateRole_shouldWrapDatabaseException_whenSaveFails() {
        UUID roleId = UUID.randomUUID();
        RoleDto request = new RoleDto();
        request.setId(roleId);
        request.setName("name");

        Role existing = Role.builder().id(roleId).build();
        when(roleRepository.findRoleById(roleId)).thenReturn(Optional.of(existing));
        when(roleRepository.save(existing)).thenThrow(new RuntimeException("db"));

        assertThrows(DatabaseException.class, () -> roleService.updateRole(request));
        verify(roleRepository, times(1)).save(existing);
    }

    @Test
    void findRoleById_shouldReturnDto_whenFound() throws ValidationException, DatabaseException, NotFoundException {
        UUID roleId = UUID.randomUUID();
        Role role = Role.builder().id(roleId).build();
        when(roleRepository.findRoleById(roleId)).thenReturn(Optional.of(role));

        RoleDto result = roleService.findRoleById(roleId);

        assertEquals(roleId, result.getId());
        verify(roleRepository, times(1)).findRoleById(roleId);
    }

    @Test
    void findRoleById_shouldThrowValidation_whenIdNull() {
        assertThrows(ValidationException.class, () -> roleService.findRoleById(null));
        verify(roleRepository, times(0)).findRoleById(any());
    }

    @Test
    void findRoleById_shouldThrowValidation_whenNotFoundExceptionReturns() {
        UUID roleId = UUID.randomUUID();
        when(roleRepository.findRoleById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> roleService.findRoleById(roleId));
        verify(roleRepository, times(1)).findRoleById(any());
    }

    @Test
    void findRoleById_shouldWrapDatabaseException_whenRepositoryFails() {
        UUID roleId = UUID.randomUUID();
        when(roleRepository.findRoleById(roleId)).thenThrow(new RuntimeException("db"));

        assertThrows(DatabaseException.class, () -> roleService.findRoleById(roleId));
        verify(roleRepository, times(1)).findRoleById(roleId);
    }

    @Test
    void findAllRoles_shouldReturnList_whenExists() throws DatabaseException, NotFoundException {
        Role role = Role.builder().id(UUID.randomUUID()).build();
        when(roleRepository.findAllRoles()).thenReturn(List.of(role));

        List<Role> result = roleService.findAllRoles();

        assertEquals(1, result.size());
        verify(roleRepository, times(1)).findAllRoles();
    }

    @Test
    void findAllRoles_shouldThrowNotFound_whenEmpty() {
        when(roleRepository.findAllRoles()).thenReturn(List.of());

        assertThrows(NotFoundException.class, () -> roleService.findAllRoles());
    }

    @Test
    void findAllRoles_shouldWrapDatabaseException_whenRepositoryFails() {
        when(roleRepository.findAllRoles()).thenThrow(new RuntimeException("db"));

        assertThrows(DatabaseException.class, () -> roleService.findAllRoles());
    }

    @Test
    void deleteRole_shouldMarkDeleted_whenValid() throws ValidationException, NotFoundException, DatabaseException {
        UUID roleId = UUID.randomUUID();
        Role role = Role.builder().id(roleId).build();
        when(roleRepository.findRoleById(roleId)).thenReturn(Optional.of(role));

        roleService.deleteRole(roleId);

        verify(roleRepository, times(1)).save(role);
    }

    @Test
    void deleteRole_shouldThrowValidation_whenIdNull() {
        assertThrows(ValidationException.class, () -> roleService.deleteRole(null));
        verify(roleRepository, times(0)).findRoleById(any());
    }

    @Test
    void deleteRole_shouldThrowValidation_whenNotFoundExceptionReturns() {
        UUID roleId = UUID.randomUUID();
        when(roleRepository.findRoleById(roleId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> roleService.deleteRole(roleId));
        verify(roleRepository, times(1)).findRoleById(any());
    }

    @Test
    void deleteRole_shouldWrapDatabaseException_whenSaveFails() {
        UUID roleId = UUID.randomUUID();
        Role role = Role.builder().id(roleId).build();
        when(roleRepository.findRoleById(roleId)).thenReturn(Optional.of(role));
        when(roleRepository.save(role)).thenThrow(new RuntimeException("db"));

        assertThrows(DatabaseException.class, () -> roleService.deleteRole(roleId));
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    void assignRolesToUser_shouldReturnDto_whenValid() throws ValidationException, NotFoundException, DatabaseException {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        Role activeRole = Role.builder().id(roleId).isDeleted(false).build();
        User user = User.builder().id(userId).roles(new HashSet<>()).build();

        when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findAllById(List.of(roleId))).thenReturn(List.of(activeRole));
        when(userRepository.save(user)).thenReturn(user);

        UserDto result = roleService.assignRolesToUser(List.of(roleId), userId);

        assertNotNull(result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void assignRolesToUser_shouldThrowValidation_whenIdsInvalid() {
        assertThrows(ValidationException.class, () -> roleService.assignRolesToUser(null, null));
        verify(userRepository, times(0)).findUserById(any());
    }

    @Test
    void assignRolesToUser_shouldThrowValidation_whenNoActiveRoles() {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        Role deletedRole = Role.builder().id(roleId).isDeleted(true).build();

        when(userRepository.findUserById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));
        when(roleRepository.findAllById(List.of(roleId))).thenReturn(List.of(deletedRole));

        assertThrows(ValidationException.class, () -> roleService.assignRolesToUser(List.of(roleId), userId));
    }

    @Test
    void assignRolesToUser_shouldWrapDatabaseException_whenSaveFails() {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        Role activeRole = Role.builder().id(roleId).isDeleted(false).build();
        User user = User.builder().id(userId).roles(new HashSet<>()).build();

        when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findAllById(List.of(roleId))).thenReturn(List.of(activeRole));
        when(userRepository.save(user)).thenThrow(new RuntimeException("db"));

        assertThrows(DatabaseException.class, () -> roleService.assignRolesToUser(List.of(roleId), userId));
        verify(userRepository, times(1)).save(user);
    }
}

