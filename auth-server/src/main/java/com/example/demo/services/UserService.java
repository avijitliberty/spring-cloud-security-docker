package com.example.demo.services;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

public interface UserService {

	Principal authenticateUserAndSetSession(String userName, String password, HttpServletRequest request);

}
