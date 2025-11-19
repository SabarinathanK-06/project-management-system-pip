package com.i2i.project_management.Dto;

import lombok.Data;

import java.util.UUID;

@Data
public class RoleDto {

    private UUID id;

    private String name;

    private String description;

}
