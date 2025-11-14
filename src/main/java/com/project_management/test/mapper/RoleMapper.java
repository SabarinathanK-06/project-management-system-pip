package com.project_management.test.mapper;

import com.project_management.test.Dto.RoleDto;
import com.project_management.test.model.Role;

public class RoleMapper {

    public static RoleDto toDto(Role role) {
        RoleDto roleDto = new RoleDto();
        roleDto.setId(role.getId());
        roleDto.setName(role.getName());
        roleDto.setDescription(role.getDescription());

        return roleDto;

    }

}
