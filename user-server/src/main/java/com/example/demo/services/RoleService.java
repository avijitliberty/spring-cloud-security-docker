package com.example.demo.services;

import java.util.Set;

import com.example.demo.model.Role;

public interface RoleService {
	
	Set<Role> findRoles() throws Exception;

}
