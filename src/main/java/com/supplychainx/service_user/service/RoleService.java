package com.supplychainx.service_user.service;

import com.supplychainx.exception.ResourceNotFoundException;
import com.supplychainx.service_user.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.supplychainx.service_user.repository.RoleRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role create(Role role) {
        return roleRepository.save(role);
    }

    public Role getById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id " + id));
    }

    public Role update(Role role) {
        return roleRepository.save(role);
    }

    public void delete(Long id) {
        Role role = getById(id);
        roleRepository.delete(role);
    }

    public List<Role> getAll() {
        return roleRepository.findAll();
    }
}
