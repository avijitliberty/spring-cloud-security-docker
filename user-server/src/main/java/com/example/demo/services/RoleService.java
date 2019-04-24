package com.example.demo.services;

import java.util.List;

import com.example.demo.model.Role;

public interface RoleService {
	
	List<Role> findRoles() throws Exception;

}
