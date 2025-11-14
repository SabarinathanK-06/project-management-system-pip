package com.project_management.test.mapper;

import com.project_management.test.Dto.ProjectDto;
import com.project_management.test.Dto.RoleDto;
import com.project_management.test.Dto.UserDto;
import com.project_management.test.model.User;

import java.util.List;

public class UserMapper {

    public static UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setAddress(userDto.getAddress());

        List<ProjectDto> projectDtos = user.getProjects().stream()
                .map(ProjectMapper::toDto)
                .toList();
        userDto.setProjects(projectDtos);

        List<RoleDto> roleDtos = user.getRoles().stream()
                .map(RoleMapper::toDto)
                .toList();
        userDto.setRoles(roleDtos);
        return userDto;
    }

}
