package com.example.demo.repositories;

import java.util.Set;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Role;

@Repository
public interface RoleRepository extends PagingAndSortingRepository<Role, Integer>{

	public Role findByRole(String role);
	
	public Set<Role> findAll();
}
