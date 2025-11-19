package com.i2i.project_management.mapper;

import com.i2i.project_management.Dto.RoleDto;
import com.i2i.project_management.model.Role;

public class RoleMapper {

    public static RoleDto toDto(Role role) {
        RoleDto roleDto = new RoleDto();
        roleDto.setId(role.getId());
        roleDto.setName(role.getName());
        roleDto.setDescription(role.getDescription());

        return roleDto;

    }

}
