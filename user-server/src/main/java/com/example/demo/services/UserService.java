package com.example.demo.services;

import java.util.List;

import com.example.demo.model.User;
import com.example.demo.model.UserRegistrationDto;

public interface UserService {

	User create(UserRegistrationDto user) throws Exception;

	User update(User user) throws Exception;

	String delete(Integer id) throws Exception;

	List<User> findUsers(String name, Integer id) throws Exception;

}
