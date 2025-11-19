package com.i2i.project_management.Dto;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class ProjectDto {

    private UUID id;

    private String name;

    private String description;

    private Set<UUID> userIds;

}
