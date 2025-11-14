package com.project_management.test.service;

import com.project_management.test.Dto.RoleDto;
import com.project_management.test.Dto.UserDto;
import com.project_management.test.exception.DatabaseException;
import com.project_management.test.exception.NotFoundException;
import com.project_management.test.exception.ValidationException;
import com.project_management.test.model.Role;

import java.util.List;
import java.util.UUID;

public interface RoleService {

    RoleDto createRole(RoleDto roleDto) throws ValidationException, DatabaseException;

    RoleDto updateRole(RoleDto roleDto) throws ValidationException, NotFoundException, DatabaseException;

    RoleDto findRoleById(UUID id) throws ValidationException, NotFoundException, DatabaseException;

    List<Role> findAllRoles() throws DatabaseException, NotFoundException;

    void deleteRole(UUID id) throws ValidationException, NotFoundException, DatabaseException;

    UserDto assignRolesToUser(List<UUID> RoleIds, UUID userID) throws ValidationException, NotFoundException, DatabaseException;

}
