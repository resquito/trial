package org.example.repository;

import org.example.entities.RoleEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RoleRepository extends CrudRepository<RoleEntity, Long> {

    List<RoleEntity> findAllByRoleIn(List<String> role);

    @Query(nativeQuery = true, value = "select role"
            + " from role r, user u, user_roles ur "
            + " where u.username = ?1 "
            + " and u.user_id = ur.user_id"
            + " and r.role_id = ur.role_id")
    List<String> getRolesForUser(String username);
}
