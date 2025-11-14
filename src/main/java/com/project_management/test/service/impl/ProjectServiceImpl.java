package com.project_management.test.service.impl;

import com.project_management.test.Dto.ProjectDto;
import com.project_management.test.Dto.UpdateProjectDto;
import com.project_management.test.exception.DatabaseException;
import com.project_management.test.exception.NotFoundException;
import com.project_management.test.exception.ValidationException;
import com.project_management.test.mapper.ProjectMapper;
import com.project_management.test.model.Project;
import com.project_management.test.model.User;
import com.project_management.test.repository.ProjectRepository;
import com.project_management.test.repository.UserRepository;
import com.project_management.test.service.ProjectService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectServiceImpl implements ProjectService {

    private ProjectRepository projectRepository;

    private UserRepository userRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

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
            throw new DatabaseException("Failed to fetch all Projects");
        }
    }

    @Override
    public void delete(UUID id) throws ValidationException, DatabaseException {
        if (id == null) throw new ValidationException("Project ID should not be null");

        try{
            projectRepository.deleteProject(id);
        } catch (Exception e) {
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

            boolean isPresent = project.getUsers().stream()
                    .map(emp -> emp.getId() == userId)
                    .findFirst().isPresent();
            if (isPresent)
                throw new ValidationException("User with Id: " + userId
                        + "is already present in the project with Id: " + projectId);

            else project.getUsers().add(user);

            Project savedProject = projectRepository.save(project);

            return ProjectMapper.toDto(savedProject);
        } catch (NotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Failed to assign project to user");
        }
    }

    @Override
    public ProjectDto removeProjectFromEmployee(UUID projectId, UUID userId)
            throws ValidationException, DatabaseException, NotFoundException {
        if (projectId == null && userId == null)
            throw new ValidationException("Project or Employee ID should not be null");

        try {
            Project project = projectRepository.findProjectById(userId)
                    .orElseThrow(() -> new NotFoundException("Project not found with ID: " + projectId));

            User user = userRepository.findUserById(userId)
                    .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

            boolean isPresent = project.getUsers().stream()
                    .map(emp -> emp.getId() == userId)
                    .findFirst().isPresent();
            if (!isPresent)
                throw new ValidationException("User with Id: " + userId
                        + "is not present in the project with Id: " + projectId);

            else project.getUsers().remove(user);

            Project savedProject = projectRepository.save(project);

            return ProjectMapper.toDto(savedProject);
        } catch (NotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Failed to remove project from employee");
        }
    }
}
