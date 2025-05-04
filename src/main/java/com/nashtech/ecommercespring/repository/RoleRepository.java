package com.nashtech.ecommercespring.repository;

import com.nashtech.ecommercespring.enums.RoleName;
import com.nashtech.ecommercespring.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByRoleName(RoleName roleName);
}
