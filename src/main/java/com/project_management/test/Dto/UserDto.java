package com.project_management.test.Dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UserDto {

    private UUID id;

    private String firstName;

    private String lastName;

    private String email;

    private List<RoleDto> roles;

    private String phoneNumber;

    private String Address;

    private List<ProjectDto> projects;

}
