package com.project_management.test.mapper;

import com.project_management.test.Dto.ProjectDto;
import com.project_management.test.model.Project;

public class ProjectMapper {

    public static ProjectDto toDto(Project project) {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(project.getId());
        projectDto.setName(project.getName());
        projectDto.setDescription(project.getDescription());
        return projectDto;
    }

}
