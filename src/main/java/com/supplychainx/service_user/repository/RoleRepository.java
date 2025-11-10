package com.supplychainx.service_user.repository;

import com.supplychainx.service_user.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role , Long> {
    Boolean existsRolesByName(String name);
}
