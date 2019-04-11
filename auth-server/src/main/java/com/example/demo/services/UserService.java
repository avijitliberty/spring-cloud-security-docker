package com.example.demo.services;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.example.demo.model.User;
import com.example.demo.model.UserRegistrationDto;

public interface UserService {

	User create(UserRegistrationDto user) throws Exception;

	UsernamePasswordAuthenticationToken authenticateUserAndSetSession(String userName, String password, HttpServletRequest request);

}
