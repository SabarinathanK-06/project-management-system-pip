package com.i2i.project_management.controller;

import com.i2i.project_management.Dto.RoleDto;
import com.i2i.project_management.Dto.UserDto;
import com.i2i.project_management.exception.DatabaseException;
import com.i2i.project_management.exception.NotFoundException;
import com.i2i.project_management.exception.ValidationException;
import com.i2i.project_management.model.Role;
import com.i2i.project_management.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/roles")
@Slf4j
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RoleDto> createRole(@RequestBody RoleDto roleDto)
            throws ValidationException, DatabaseException {
        log.info("Creating role {}", roleDto.getName());
        RoleDto createdRole = roleService.createRole(roleDto);
        return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RoleDto> updateRole(@RequestBody RoleDto roleDto)
            throws ValidationException, NotFoundException, DatabaseException {
        log.info("Updating role {}", roleDto.getId());
        RoleDto updatedRole = roleService.updateRole(roleDto);

        return ResponseEntity.ok(updatedRole);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RoleDto> findRoleById(@PathVariable UUID id)
            throws ValidationException, NotFoundException, DatabaseException {

        log.debug("Fetching role {}", id);
        RoleDto roleDto = roleService.findRoleById(id);

        return ResponseEntity.ok(roleDto);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Role>> getAllRoles()
            throws DatabaseException, NotFoundException {
        log.debug("Fetching all roles");
        List<Role> roles = roleService.findAllRoles();
        return ResponseEntity.ok(roles);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteRole(@PathVariable UUID id)
            throws ValidationException, NotFoundException, DatabaseException {

        log.warn("Deleting role {}", id);
        roleService.deleteRole(id);
        return ResponseEntity.ok("Role deleted successfully");
    }


    @PostMapping("/assign/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserDto> assignRolesToUser(
            @RequestBody List<UUID> roleIds,
            @PathVariable UUID userId)
            throws ValidationException, NotFoundException, DatabaseException {
        log.info("Assigning roles {} to user {}", roleIds, userId);
        UserDto updatedUser = roleService.assignRolesToUser(roleIds, userId);
        return ResponseEntity.ok(updatedUser);
    }


}
