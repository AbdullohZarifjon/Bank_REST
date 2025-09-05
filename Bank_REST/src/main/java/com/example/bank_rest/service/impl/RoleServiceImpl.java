package com.example.bank_rest.service.impl;

import com.example.bank_rest.payload.dto.response.RoleDTO;
import com.example.bank_rest.repository.RoleRepository;
import com.example.bank_rest.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {


    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> new RoleDTO(role.getId(), role.getRole().toString()))
                .toList();
    }
}
