package com.project_management.test.repository;

import com.project_management.test.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(String name);

    @Query("SELECT r FROM Role r WHERE r.isDeleted = false")
    List<Role> findAllRoles();

    @Query("SELECT r FROM Role r WHERE r.id = :id AND r.isDeleted = false")
    Optional<Role> findRoleById(@Param("id") UUID id);

    @Query("SELECT r FROM Role r WHERE r.name = :name AND r.isDeleted = false")
    Optional<Role> findRoleByName(@Param("name") String name);

    @Modifying
    @Query("UPDATE Role r SET r.isDeleted = true WHERE r.id = :id")
    void deleteRole(@Param("id") UUID id, @Param("updatedAt") LocalDateTime updatedAt);
}
