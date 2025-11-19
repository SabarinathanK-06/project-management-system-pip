package com.i2i.project_management.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UserDto {

    private UUID id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private List<RoleDto> roles;

    private String phoneNumber;

    private String address;

    private List<ProjectDto> projects;

}
