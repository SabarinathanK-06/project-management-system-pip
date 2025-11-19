package com.i2i.project_management.mapper;

import com.i2i.project_management.Dto.ProjectDto;
import com.i2i.project_management.model.Project;

public class ProjectMapper {

    public static ProjectDto toDto(Project project) {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(project.getId());
        projectDto.setName(project.getName());
        projectDto.setDescription(project.getDescription());
        return projectDto;
    }

}
