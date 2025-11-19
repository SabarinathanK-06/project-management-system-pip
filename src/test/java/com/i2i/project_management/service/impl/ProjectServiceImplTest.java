package com.i2i.project_management.service.impl;

import com.i2i.project_management.Dto.ProjectDto;
import com.i2i.project_management.Dto.UpdateProjectDto;
import com.i2i.project_management.exception.DatabaseException;
import com.i2i.project_management.exception.NotFoundException;
import com.i2i.project_management.exception.ValidationException;
import com.i2i.project_management.model.Project;
import com.i2i.project_management.model.User;
import com.i2i.project_management.repository.ProjectRepository;
import com.i2i.project_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    private ProjectServiceImpl projectService;

    @BeforeEach
    void setUp() {
        projectService = new ProjectServiceImpl(projectRepository, userRepository);
    }

    @Test
    void createProject_shouldPersistAndReturnDto_whenInputIsValid() throws ValidationException, DatabaseException {
        //arrange
        ProjectDto request = new ProjectDto();
        request.setName("New Project");
        request.setDescription("My project description");

        Project persistedProject = Project.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .description(request.getDescription())
                .isDeleted(false)
                .build();
        when(projectRepository.save(any(Project.class))).thenReturn(persistedProject);

        //act
        ProjectDto result = projectService.createProject(request);

        //assert
        verify(projectRepository, times(1)).save(any(Project.class));
        assertNotNull(result);
        assertEquals(persistedProject.getId(), result.getId());
        assertEquals(request.getName(), result.getName());
        assertEquals(request.getDescription(), result.getDescription());
    }

    @Test
    void createProject_shouldThrowValidationException_whenNameMissing() {
        //arrange
        ProjectDto request = new ProjectDto();

        //act & assert
        assertThrows(ValidationException.class, () -> projectService.createProject(request));
        verify(projectRepository, times(0)).save(any(Project.class));
    }

    @Test
    void createProject_shouldWrapAndThrowDatabaseException_whenRepositoryFails() {
        //arrange
        ProjectDto request = new ProjectDto();
        request.setName("Failing project");
        when(projectRepository.save(any(Project.class))).thenThrow(new RuntimeException("DB down"));

        //act & assert
        assertThrows(DatabaseException.class, () -> projectService.createProject(request));
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void updateProject_shouldPersistChanges_whenValidPayload() throws ValidationException, NotFoundException, DatabaseException {
        //arrange
        UUID projectId = UUID.randomUUID();
        UpdateProjectDto updateDto = new UpdateProjectDto();
        updateDto.setName("Updated");
        updateDto.setDescription("New desc");

        Project existing = Project.builder()
                .id(projectId)
                .name("Old")
                .description("Old desc")
                .build();

        when(projectRepository.findProjectById(projectId)).thenReturn(java.util.Optional.of(existing));
        when(projectRepository.save(existing)).thenReturn(existing);

        //act
        ProjectDto result = projectService.updateProject(projectId, updateDto);

        //assert
        verify(projectRepository, times(1)).findProjectById(projectId);
        verify(projectRepository, times(1)).save(existing);
        assertEquals("Updated", result.getName());
        assertEquals("New desc", result.getDescription());
    }

    @Test
    void updateProject_shouldThrowValidation_whenNoFieldsProvided() {
        UUID projectId = UUID.randomUUID();
        UpdateProjectDto updateDto = new UpdateProjectDto();

        assertThrows(ValidationException.class, () -> projectService.updateProject(projectId, updateDto));
        verify(projectRepository, times(0)).findProjectById(projectId);
    }

    @Test
    void updateProject_shouldThrowNotFound_whenProjectMissing() {
        UUID projectId = UUID.randomUUID();
        UpdateProjectDto updateDto = new UpdateProjectDto();
        updateDto.setName("Name");
        when(projectRepository.findProjectById(projectId)).thenReturn(java.util.Optional.empty());

        assertThrows(NotFoundException.class, () -> projectService.updateProject(projectId, updateDto));
        verify(projectRepository, times(1)).findProjectById(projectId);
    }

    @Test
    void updateProject_shouldWrapDatabaseException_whenSaveFails() {
        UUID projectId = UUID.randomUUID();
        UpdateProjectDto updateDto = new UpdateProjectDto();
        updateDto.setName("Name");

        Project existing = Project.builder().id(projectId).build();
        when(projectRepository.findProjectById(projectId)).thenReturn(Optional.of(existing));
        when(projectRepository.save(existing)).thenThrow(new RuntimeException("db down"));

        assertThrows(DatabaseException.class, () -> projectService.updateProject(projectId, updateDto));
        verify(projectRepository, times(1)).save(existing);
    }

    @Test
    void findById_shouldReturnDto_whenProjectExists() throws ValidationException, DatabaseException, NotFoundException {
        UUID projectId = UUID.randomUUID();
        Project project = Project.builder()
                .id(projectId)
                .name("Name")
                .description("Desc")
                .build();
        when(projectRepository.findProjectById(projectId)).thenReturn(java.util.Optional.of(project));

        ProjectDto result = projectService.findById(projectId);

        assertEquals(projectId, result.getId());
        verify(projectRepository, times(1)).findProjectById(projectId);
    }

    @Test
    void findById_shouldThrowValidation_whenIdNull() {
        assertThrows(ValidationException.class, () -> projectService.findById(null));
        verify(projectRepository, times(0)).findProjectById(any());
    }

    @Test
    void findById_shouldThrowNotFound_whenMissing() {
        UUID projectId = UUID.randomUUID();
        when(projectRepository.findProjectById(projectId)).thenReturn(java.util.Optional.empty());

        assertThrows(NotFoundException.class, () -> projectService.findById(projectId));
        verify(projectRepository, times(1)).findProjectById(projectId);
    }

    @Test
    void findById_shouldWrapDatabaseException_whenRepositoryFails() {
        UUID projectId = UUID.randomUUID();
        when(projectRepository.findProjectById(projectId)).thenThrow(new RuntimeException("db"));

        assertThrows(DatabaseException.class, () -> projectService.findById(projectId));
        verify(projectRepository, times(1)).findProjectById(projectId);
    }

    @Test
    void findAllProjects_shouldReturnList() throws DatabaseException {
        Project project = Project.builder()
                .id(UUID.randomUUID())
                .name("Project")
                .description("Desc")
                .build();
        when(projectRepository.findAllProjects()).thenReturn(java.util.List.of(project));

        var result = projectService.findAllProjects();

        assertEquals(1, result.size());
        assertEquals(project.getId(), result.get(0).getId());
        verify(projectRepository, times(1)).findAllProjects();
    }

    @Test
    void findAllProjects_shouldWrapDatabaseException_whenRepositoryFails() {
        when(projectRepository.findAllProjects()).thenThrow(new RuntimeException("db"));

        assertThrows(DatabaseException.class, () -> projectService.findAllProjects());
        verify(projectRepository, times(1)).findAllProjects();
    }

    @Test
    void delete_shouldThrowValidation_whenIdNull() {
        assertThrows(ValidationException.class, () -> projectService.delete(null));
        verify(projectRepository, times(0)).deleteProject(any());
    }

    @Test
    void delete_shouldDelegateToRepository() throws ValidationException, DatabaseException {
        UUID projectId = UUID.randomUUID();

        projectService.delete(projectId);

        verify(projectRepository, times(1)).deleteProject(projectId);
    }

    @Test
    void delete_shouldWrapDatabaseException_whenRepositoryFails() {
        UUID projectId = UUID.randomUUID();
        doThrow(new RuntimeException("db")).when(projectRepository).deleteProject(projectId);

        assertThrows(DatabaseException.class, () -> projectService.delete(projectId));
        verify(projectRepository, times(1)).deleteProject(projectId);
    }

    @Test
    void assignProjectToEmployee_shouldAddUser_whenNotPresent() throws ValidationException, NotFoundException, DatabaseException {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Project project = Project.builder()
                .id(projectId)
                .users(new HashSet<>())
                .build();
        User user = User.builder().id(userId).build();

        when(projectRepository.findProjectById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.save(project)).thenReturn(project);

        ProjectDto result = projectService.assignProjectToEmployee(projectId, userId);

        assertEquals(projectId, result.getId());
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void assignProjectToEmployee_shouldThrowValidation_whenAlreadyAssigned() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = User.builder().id(userId).build();
        Project project = Project.builder()
                .id(projectId)
                .users(new HashSet<>(java.util.List.of(user)))
                .build();

        when(projectRepository.findProjectById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));

        assertThrows(ValidationException.class, () -> projectService.assignProjectToEmployee(projectId, userId));
        verify(projectRepository, times(0)).save(any(Project.class));
    }

    @Test
    void assignProjectToEmployee_shouldThrowValidation_whenIdsNull() {
        assertThrows(ValidationException.class, () -> projectService.assignProjectToEmployee(null, null));
        verify(projectRepository, times(0)).findProjectById(any());
    }

    @Test
    void assignProjectToEmployee_shouldWrapDatabaseException_whenSaveFails() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Project project = Project.builder()
                .id(projectId)
                .users(new HashSet<>())
                .build();
        User user = User.builder().id(userId).build();

        when(projectRepository.findProjectById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.save(project)).thenThrow(new RuntimeException("db"));

        assertThrows(DatabaseException.class, () -> projectService.assignProjectToEmployee(projectId, userId));
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void removeProjectFromEmployee_shouldRemoveUser_whenPresent() throws ValidationException, NotFoundException, DatabaseException {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = User.builder().id(userId).build();
        Project project = Project.builder()
                .id(projectId)
                .users(new HashSet<>(java.util.List.of(user)))
                .build();

        when(projectRepository.findProjectById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.save(project)).thenReturn(project);

        ProjectDto result = projectService.removeProjectFromEmployee(projectId, userId);

        assertEquals(projectId, result.getId());
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void removeProjectFromEmployee_shouldThrowValidation_whenNotAssigned() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Project project = Project.builder()
                .id(projectId)
                .users(new HashSet<>())
                .build();

        when(projectRepository.findProjectById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findUserById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));

        assertThrows(ValidationException.class, () -> projectService.removeProjectFromEmployee(projectId, userId));
        verify(projectRepository, times(0)).save(any(Project.class));
    }

    @Test
    void removeProjectFromEmployee_shouldThrowValidation_whenIdsNull() {
        assertThrows(ValidationException.class, () -> projectService.removeProjectFromEmployee(null, null));
        verify(projectRepository, times(0)).findProjectById(any());
    }

    @Test
    void removeProjectFromEmployee_shouldWrapDatabaseException_whenSaveFails() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = User.builder().id(userId).build();
        Project project = Project.builder()
                .id(projectId)
                .users(new HashSet<>(java.util.List.of(user)))
                .build();

        when(projectRepository.findProjectById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.save(project)).thenThrow(new RuntimeException("db"));

        assertThrows(DatabaseException.class, () -> projectService.removeProjectFromEmployee(projectId, userId));
        verify(projectRepository, times(1)).save(project);
    }



}

