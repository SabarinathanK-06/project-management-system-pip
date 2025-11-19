package com.i2i.project_management.mapper;

import com.i2i.project_management.Dto.ProjectDto;
import com.i2i.project_management.Dto.RoleDto;
import com.i2i.project_management.Dto.UserDto;
import com.i2i.project_management.model.User;

import java.util.List;

public class UserMapper {

    public static UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setAddress(user.getAddress());

        if (user.getProjects() != null) {
            List<ProjectDto> projectDtos = user.getProjects().stream()
                    .map(ProjectMapper::toDto)
                    .toList();
            userDto.setProjects(projectDtos);
        }

        if (user.getRoles() != null) {
            List<RoleDto> roleDtos = user.getRoles().stream()
                    .map(RoleMapper::toDto)
                    .toList();
            userDto.setRoles(roleDtos);
        }
        return userDto;
    }

}
