package com.example.demo.services;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.demo.model.User;

public interface UserService extends UserDetailsService{

	User create(User user) throws Exception;

	User update(User user) throws Exception;

	String delete(Integer id) throws Exception;

	List<User> findUsers(String name, Integer id) throws Exception;

}
