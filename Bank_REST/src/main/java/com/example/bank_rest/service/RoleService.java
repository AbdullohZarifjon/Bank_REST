package com.example.bank_rest.service;


import com.example.bank_rest.payload.dto.response.RoleDTO;

import java.util.List;

public interface RoleService {
    List<RoleDTO> getAllRoles();
}
