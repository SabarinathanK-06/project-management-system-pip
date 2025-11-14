package com.project_management.test.controller;

import com.project_management.test.Dto.RoleDto;
import com.project_management.test.Dto.UserDto;
import com.project_management.test.exception.DatabaseException;
import com.project_management.test.exception.NotFoundException;
import com.project_management.test.exception.ValidationException;
import com.project_management.test.model.Role;
import com.project_management.test.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<RoleDto> createRole(@RequestBody RoleDto roleDto)
            throws ValidationException, DatabaseException {
        RoleDto createdRole = roleService.createRole(roleDto);
        return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<RoleDto> updateRole(@RequestBody RoleDto roleDto)
            throws ValidationException, NotFoundException, DatabaseException {
        RoleDto updatedRole = roleService.updateRole(roleDto);

        return ResponseEntity.ok(updatedRole);
    }


    @GetMapping("/{id}")
    public ResponseEntity<RoleDto> findRoleById(@PathVariable UUID id)
            throws ValidationException, NotFoundException, DatabaseException {

        RoleDto roleDto = roleService.findRoleById(id);

        return ResponseEntity.ok(roleDto);
    }

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles()
            throws DatabaseException, NotFoundException {
        List<Role> roles = roleService.findAllRoles();
        return ResponseEntity.ok(roles);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRole(@PathVariable UUID id)
            throws ValidationException, NotFoundException, DatabaseException {

        roleService.deleteRole(id);
        return ResponseEntity.ok("Role deleted successfully");
    }


    @PostMapping("/assign/{userId}")
    public ResponseEntity<UserDto> assignRolesToUser(
            @RequestBody List<UUID> roleIds,
            @PathVariable UUID userId)
            throws ValidationException, NotFoundException, DatabaseException {
        UserDto updatedUser = roleService.assignRolesToUser(roleIds, userId);
        return ResponseEntity.ok(updatedUser);
    }


}
