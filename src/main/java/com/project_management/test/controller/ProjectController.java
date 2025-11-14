package com.project_management.test.controller;

import com.project_management.test.Dto.ProjectDto;
import com.project_management.test.Dto.UpdateProjectDto;
import com.project_management.test.exception.DatabaseException;
import com.project_management.test.exception.NotFoundException;
import com.project_management.test.exception.ValidationException;
import com.project_management.test.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectDto projectDto)
            throws ValidationException, DatabaseException {
        ProjectDto createdProject = projectService.createProject(projectDto);

        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto> updateProject(
            @PathVariable UUID id,
            @RequestBody UpdateProjectDto updateProjectDto)
            throws ValidationException, DatabaseException, NotFoundException {

        ProjectDto updatedProject = projectService.updateProject(id, updateProjectDto);
        return ResponseEntity.ok(updatedProject);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> findById(@PathVariable UUID id)
            throws ValidationException, NotFoundException, DatabaseException {

        ProjectDto project = projectService.findById(id);

        return ResponseEntity.ok(project);
    }

    @GetMapping
    public ResponseEntity<List<ProjectDto>> findAllProjects()
            throws DatabaseException {

        List<ProjectDto> projects = projectService.findAllProjects();
        return ResponseEntity.ok(projects);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable UUID id)
            throws ValidationException, DatabaseException {

        projectService.delete(id);

        return ResponseEntity.ok("Project deleted successfully");
    }

    @PostMapping("/{projectId}/assign/{userId}")
    public ResponseEntity<ProjectDto> assignProjectToEmployee(
            @PathVariable UUID projectId,
            @PathVariable UUID userId)
            throws ValidationException, NotFoundException, DatabaseException {
        ProjectDto updatedProject = projectService.assignProjectToEmployee(projectId, userId);

        return ResponseEntity.ok(updatedProject);
    }

    @PostMapping("/{projectId}/remove/{userId}")
    public ResponseEntity<ProjectDto> removeProjectFromEmployee(
            @PathVariable UUID projectId,
            @PathVariable UUID userId)
            throws ValidationException, NotFoundException, DatabaseException {

        ProjectDto updatedProject = projectService.removeProjectFromEmployee(projectId, userId);
        return ResponseEntity.ok(updatedProject);
    }
}
