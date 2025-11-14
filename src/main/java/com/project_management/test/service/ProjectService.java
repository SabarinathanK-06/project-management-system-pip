package com.project_management.test.service;

import com.project_management.test.Dto.ProjectDto;
import com.project_management.test.Dto.UpdateProjectDto;
import com.project_management.test.exception.DatabaseException;
import com.project_management.test.exception.NotFoundException;
import com.project_management.test.exception.ValidationException;

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
