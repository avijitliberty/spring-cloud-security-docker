package com.example.demo.controllers;

import java.security.Principal;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Because this application is also a User Info Resource Server, we expose info about the logged in user at:
 *
 *     http://localhost:9090/auth/user
 */
@RestController
public class UserController {
	
    /**
	/* Controller method invoked by the oauth2 client to verify the user 
	**/
	@RequestMapping(value = "/user", 
			        method = RequestMethod.GET, 
			        produces = MediaType.APPLICATION_JSON_VALUE)
	public Principal getUser(Principal principal) {
		return principal;
	}
}
