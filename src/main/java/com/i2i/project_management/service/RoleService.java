package com.i2i.project_management.service;

import com.i2i.project_management.Dto.RoleDto;
import com.i2i.project_management.exception.DatabaseException;
import com.i2i.project_management.exception.NotFoundException;
import com.i2i.project_management.exception.ValidationException;
import com.i2i.project_management.Dto.UserDto;
import com.i2i.project_management.model.Role;

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
