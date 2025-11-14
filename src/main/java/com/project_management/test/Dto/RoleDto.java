package com.project_management.test.Dto;

import lombok.Data;

import java.util.UUID;

@Data
public class RoleDto {

    private UUID id;

    private String name;

    private String description;

}
