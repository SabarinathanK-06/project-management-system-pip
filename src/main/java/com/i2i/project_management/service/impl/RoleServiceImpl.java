package com.i2i.project_management.service.impl;

import com.i2i.project_management.Dto.RoleDto;
import com.i2i.project_management.Dto.UserDto;
import com.i2i.project_management.exception.DatabaseException;
import com.i2i.project_management.exception.NotFoundException;
import com.i2i.project_management.exception.ValidationException;
import com.i2i.project_management.mapper.RoleMapper;
import com.i2i.project_management.mapper.UserMapper;
import com.i2i.project_management.model.Role;
import com.i2i.project_management.model.User;
import com.i2i.project_management.repository.RoleRepository;
import com.i2i.project_management.repository.UserRepository;
import com.i2i.project_management.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public RoleDto createRole(RoleDto roleDto) throws ValidationException, DatabaseException {
        if (roleDto == null || roleDto.getName() == null) {
            throw new ValidationException("Role details should not be null");
        }

        try {
            Role role = Role.builder()
                    .name(roleDto.getName())
                    .description(roleDto.getDescription())
                    .isDeleted(false)
                    .build();

            Role savedRole = roleRepository.save(role);
            return RoleMapper.toDto(savedRole);

        } catch (Exception e) {
            log.error("Failed to create role {}", roleDto.getName(), e);
            throw new DatabaseException("Failed to create role");
        }
    }

    @Override
    public RoleDto updateRole(RoleDto roleDto)
            throws ValidationException, NotFoundException, DatabaseException {

        if (roleDto == null || roleDto.getId() == null) {
            throw new ValidationException("Role ID cannot be null");
        }
        Role existingRole;
        try {
            existingRole = roleRepository.findRoleById(roleDto.getId())
                    .orElseThrow(() -> new NotFoundException("Role not found"));
            if (Boolean.TRUE.equals(existingRole.getIsDeleted())) {
                throw new ValidationException("Role is deleted and cannot be updated");
            }
            existingRole.setName(roleDto.getName());
            existingRole.setDescription(roleDto.getDescription());

            Role updatedRole = roleRepository.save(existingRole);
            return RoleMapper.toDto(updatedRole);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to update role {}", roleDto.getId(), e);
            throw new DatabaseException("Failed to update role");
        }
    }

    @Override
    public RoleDto findRoleById(UUID id)
            throws ValidationException, NotFoundException, DatabaseException {

        if (id == null) {
            throw new ValidationException("Role ID cannot be null");
        }
        try {
            Role role = roleRepository.findRoleById(id)
                    .orElseThrow(() -> new NotFoundException("Role not found"));
            return RoleMapper.toDto(role);

        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to fetch role {}", id, e);
            throw new DatabaseException("Failed to fetch role with id: " + id);
        }
    }

    @Override
    public List<Role> findAllRoles() throws DatabaseException, NotFoundException {

        try {
            List<Role> roles = roleRepository.findAllRoles();
            if (roles.isEmpty()) {
                throw new NotFoundException("Role list is empty");
            }
            return roles;
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to fetch roles", e);
            throw new DatabaseException("Failed to fetch roles");
        }
    }

    @Override
    public void deleteRole(UUID id) throws ValidationException, NotFoundException, DatabaseException {

        if (id == null) {
            throw new ValidationException("Role ID cannot be null");
        }

        Role role;
        try {
            role = roleRepository.findRoleById(id)
                    .orElseThrow(() -> new NotFoundException("Role not found"));
            role.setIsDeleted(true);
            roleRepository.save(role);

        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to delete role {}", id, e);
            throw new DatabaseException("Failed to delete role with id: " + id);
        }
    }

    @Override
    public UserDto assignRolesToUser(List<UUID> roleIds, UUID userId)
            throws ValidationException, NotFoundException, DatabaseException {

        if (roleIds == null || roleIds.isEmpty() || userId == null) {
            throw new ValidationException("User ID or Role IDs should not be null or empty");
        }

        try {
            User user = userRepository.findUserById(userId)
                    .orElseThrow(() -> new NotFoundException("User not found"));
            List<Role> roles = roleRepository.findAllById(roleIds);

            Set<Role> activeRoles = roles.stream()
                    .filter(role -> !Boolean.TRUE.equals(role.getIsDeleted()))
                    .collect(Collectors.toSet());

            if (activeRoles.isEmpty()) {
                throw new ValidationException("No valid active roles found to assign");
            }
            Set<Role> updatedRoles = new HashSet<>(
                    user.getRoles() == null ? Set.of() : user.getRoles()
            );
            updatedRoles.addAll(activeRoles);

            user.setRoles(updatedRoles);

            User savedUser = userRepository.save(user);
            return UserMapper.toDto(savedUser);

        } catch (NotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to assign roles {} to user {}", roleIds, userId, e);
            throw new DatabaseException("Failed to assign roles to user");
        }
    }

}
