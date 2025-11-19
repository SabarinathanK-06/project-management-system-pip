package com.i2i.project_management.repository;

import com.i2i.project_management.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    @Query("SELECT p FROM Project p WHERE p.id = :id AND p.isDeleted = false")
    Optional<Project> findProjectById(@Param("id") UUID id);

    @Query("SELECT p FROM Project p WHERE p.name = :name AND p.isDeleted = false")
    Optional<Project> findProjectByName(@Param("name") String name);

    @Query("SELECT p FROM Project p WHERE p.isDeleted = false")
    List<Project> findAllProjects();

    @Modifying
    @Query("UPDATE Project p SET p.isDeleted = true WHERE p.id = :id")
    void deleteProject(@Param("id") UUID id);


}
