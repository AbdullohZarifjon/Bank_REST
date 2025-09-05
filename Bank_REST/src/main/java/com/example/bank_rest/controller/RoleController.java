package com.example.bank_rest.controller;


import com.example.bank_rest.payload.common.ApiResponse;
import com.example.bank_rest.payload.common.ApiResponseFactory;
import com.example.bank_rest.payload.dto.response.RoleDTO;
import com.example.bank_rest.service.RoleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ApiResponse<List<RoleDTO>> getAllRoles() {
        return ApiResponseFactory.success("List of all roles", roleService.getAllRoles());
    }
}
