package com.i2i.project_management.service.impl;

import com.i2i.project_management.mapper.ProjectMapper;
import com.i2i.project_management.Dto.ProjectDto;
import com.i2i.project_management.Dto.UpdateProjectDto;
import com.i2i.project_management.exception.DatabaseException;
import com.i2i.project_management.exception.NotFoundException;
import com.i2i.project_management.exception.ValidationException;
import com.i2i.project_management.model.Project;
import com.i2i.project_management.model.User;
import com.i2i.project_management.repository.ProjectRepository;
import com.i2i.project_management.repository.UserRepository;
import com.i2i.project_management.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    private final UserRepository userRepository;

    @Override
    public ProjectDto createProject(ProjectDto projectDto) throws ValidationException, DatabaseException {
        if(projectDto == null) throw new ValidationException("Project related details should not be null");

        if (projectDto.getName() == null) throw new ValidationException("Project name should not be null");

        Project project = Project.builder()
                .name(projectDto.getName())
                .description(projectDto.getDescription())
                .isDeleted(false)
                .build();

        try {
            Project savedProject = projectRepository.save(project);
            return ProjectMapper.toDto(savedProject);
        } catch (Exception e) {
            log.error("Failed to create project {}", projectDto.getName(), e);
            throw new DatabaseException("Failed to create project");
        }
    }

    @Override
    public ProjectDto updateProject(UUID id, UpdateProjectDto updateProjectDto)
            throws ValidationException, DatabaseException, NotFoundException {
        if (updateProjectDto.getName() == null && updateProjectDto.getDescription() == null)
            throw new ValidationException("Project name that want to update should not be null");

        Project project = projectRepository.findProjectById(id)
                .orElseThrow(() -> new NotFoundException("Project not found with ID: " + id));

        if (updateProjectDto.getName() != null) project.setName(updateProjectDto.getName());
        if (updateProjectDto.getDescription() != null) project.setDescription(updateProjectDto.getDescription());

        Project updatedProject;
        try{
            updatedProject = projectRepository.save(project);
            return ProjectMapper.toDto(updatedProject);
        } catch (Exception e) {
            log.error("Failed to update project {}", id, e);
            throw new DatabaseException("Failed to update project with ID: " + id);
        }
    }

    @Override
    public ProjectDto findById(UUID id) throws ValidationException, NotFoundException, DatabaseException {
        if (id == null) throw new ValidationException("Project Id should not be null");

        try{
            Project project = projectRepository.findProjectById(id)
                    .orElseThrow(()-> new NotFoundException("Project not found with ID: " + id));
            return ProjectMapper.toDto(project);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to fetch project {}", id, e);
            throw new DatabaseException("Failed to fetch project with ID: " + id);
        }
    }

    @Override
    public List<ProjectDto> findAllProjects() throws DatabaseException {
        List<ProjectDto> projectDtos;
        try{
            List<Project> projects = projectRepository.findAllProjects();
            projectDtos = projects.stream()
                    .map(ProjectMapper::toDto)
                    .toList();
            return projectDtos;
        } catch (Exception e) {
            log.error("Failed to fetch projects", e);
            throw new DatabaseException("Failed to fetch all Projects");
        }
    }

    @Override
    public void delete(UUID id) throws ValidationException, DatabaseException {
        if (id == null) throw new ValidationException("Project ID should not be null");

        try{
            projectRepository.deleteProject(id);
        } catch (Exception e) {
            log.error("Failed to delete project {}", id, e);
            throw new DatabaseException("Failed to delete project with ID: " + id);
        }
    }

    @Override
    public ProjectDto assignProjectToEmployee(UUID projectId, UUID userId)
            throws ValidationException, NotFoundException, DatabaseException {
        if (projectId == null && userId == null)
            throw new ValidationException("Project or Employee ID should not be null");

        try {
            Project project = projectRepository.findProjectById(projectId)
                    .orElseThrow(() -> new NotFoundException("Project not found with ID: " + projectId));
            User user = userRepository.findUserById(userId)
                    .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
            if (project.getUsers() == null) {
                project.setUsers(new HashSet<>());
            }

            boolean isPresent = project.getUsers().stream()
                    .anyMatch(emp -> userId.equals(emp.getId()));
            if (isPresent)
                throw new ValidationException("User with Id: " + userId
                        + " is already present in the project with Id: " + projectId);

            else project.getUsers().add(user);
            Project savedProject = projectRepository.save(project);
            return ProjectMapper.toDto(savedProject);
        } catch (NotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to assign project {} to user {}", projectId, userId, e);
            throw new DatabaseException("Failed to assign project to user");
        }
    }

    @Override
    public ProjectDto removeProjectFromEmployee(UUID projectId, UUID userId)
            throws ValidationException, DatabaseException, NotFoundException {
        if (projectId == null && userId == null)
            throw new ValidationException("Project or Employee ID should not be null");

        try {
            Project project = projectRepository.findProjectById(projectId)
                    .orElseThrow(() -> new NotFoundException("Project not found with ID: " + projectId));

            User user = userRepository.findUserById(userId)
                    .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

            boolean isPresent = project.getUsers() != null && project.getUsers().stream()
                    .anyMatch(emp -> userId.equals(emp.getId()));
            if (!isPresent)
                throw new ValidationException("User with Id: " + userId
                        + " is not present in the project with Id: " + projectId);

            else project.getUsers().remove(user);

            Project savedProject = projectRepository.save(project);
            return ProjectMapper.toDto(savedProject);
        } catch (NotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to remove project {} from user {}", projectId, userId, e);
            throw new DatabaseException("Failed to remove project from employee");
        }
    }
}
