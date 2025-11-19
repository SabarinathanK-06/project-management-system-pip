package com.i2i.project_management.service;

import com.i2i.project_management.Dto.ProjectDto;
import com.i2i.project_management.Dto.UpdateProjectDto;
import com.i2i.project_management.exception.DatabaseException;
import com.i2i.project_management.exception.NotFoundException;
import com.i2i.project_management.exception.ValidationException;

import java.util.List;
import java.util.UUID;

public interface ProjectService {

    ProjectDto createProject(ProjectDto projectDto) throws ValidationException, DatabaseException;

    ProjectDto updateProject(UUID id, UpdateProjectDto projectDto) throws ValidationException, DatabaseException, NotFoundException;

    ProjectDto findById(UUID id) throws ValidationException, NotFoundException, DatabaseException;

    List<ProjectDto> findAllProjects() throws DatabaseException;

    void delete(UUID id) throws ValidationException, DatabaseException;

    ProjectDto assignProjectToEmployee(UUID projectId, UUID employeeId) throws ValidationException, NotFoundException, DatabaseException;

    ProjectDto removeProjectFromEmployee(UUID projectId, UUID employeeId) throws ValidationException, DatabaseException, NotFoundException;

}
