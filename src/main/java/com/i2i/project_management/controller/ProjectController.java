package com.i2i.project_management.controller;

import com.i2i.project_management.Dto.ProjectDto;
import com.i2i.project_management.Dto.UpdateProjectDto;
import com.i2i.project_management.exception.DatabaseException;
import com.i2i.project_management.exception.NotFoundException;
import com.i2i.project_management.exception.ValidationException;
import com.i2i.project_management.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/projects")
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PROJECT_MANAGER')")
    public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectDto projectDto)
            throws ValidationException, DatabaseException {
        log.info("Creating project {}", projectDto.getName());
        ProjectDto createdProject = projectService.createProject(projectDto);

        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROJECT_MANAGER')")
    public ResponseEntity<ProjectDto> updateProject(
            @PathVariable UUID id,
            @RequestBody UpdateProjectDto updateProjectDto)
            throws ValidationException, DatabaseException, NotFoundException {

        log.info("Updating project {}", id);
        ProjectDto updatedProject = projectService.updateProject(id, updateProjectDto);
        return ResponseEntity.ok(updatedProject);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROJECT_MANAGER','EMPLOYEE')")
    public ResponseEntity<ProjectDto> findById(@PathVariable UUID id)
            throws ValidationException, NotFoundException, DatabaseException {

        log.debug("Fetching project {}", id);
        ProjectDto project = projectService.findById(id);

        return ResponseEntity.ok(project);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PROJECT_MANAGER','EMPLOYEE')")
    public ResponseEntity<List<ProjectDto>> findAllProjects()
            throws DatabaseException {

        log.debug("Fetching all projects");
        List<ProjectDto> projects = projectService.findAllProjects();
        return ResponseEntity.ok(projects);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteProject(@PathVariable UUID id)
            throws ValidationException, DatabaseException {

        log.warn("Deleting project {}", id);
        projectService.delete(id);

        return ResponseEntity.ok("Project deleted successfully");
    }

    @PostMapping("/{projectId}/assign/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROJECT_MANAGER')")
    public ResponseEntity<ProjectDto> assignProjectToEmployee(
            @PathVariable UUID projectId,
            @PathVariable UUID userId)
            throws ValidationException, NotFoundException, DatabaseException {
        log.info("Assigning project {} to user {}", projectId, userId);
        ProjectDto updatedProject = projectService.assignProjectToEmployee(projectId, userId);

        return ResponseEntity.ok(updatedProject);
    }

    @PostMapping("/{projectId}/remove/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROJECT_MANAGER')")
    public ResponseEntity<ProjectDto> removeProjectFromEmployee(
            @PathVariable UUID projectId,
            @PathVariable UUID userId)
            throws ValidationException, NotFoundException, DatabaseException {

        log.info("Removing user {} from project {}", userId, projectId);
        ProjectDto updatedProject = projectService.removeProjectFromEmployee(projectId, userId);
        return ResponseEntity.ok(updatedProject);
    }
}
