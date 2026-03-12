package com.juliandonati.api.security.service;

import com.juliandonati.api.domain.Role;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RoleService {
    List<Role> findAll();
    Role findById(Long id);
    Role save(Role role);
    void deleteById(Long id);
}
