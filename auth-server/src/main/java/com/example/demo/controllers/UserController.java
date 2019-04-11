package com.example.demo.controllers;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.User;
import com.example.demo.model.UserRegistrationDto;
import com.example.demo.services.UserService;

/**
 * Because this application is also a User Info Resource Server, we expose info
 * about the logged in user at:
 *
 * http://localhost:9090/auth/user
 */
@RestController
public class UserController {

	@Value("${security.oauth2.client.registered-redirect-uri}")
	private String redirectUri;
	
	@Autowired
	private UserService userService;

	/**
	 * /* Controller method invoked by the oauth2 client to verify the user
	 **/
	@RequestMapping(value = "/user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Principal getUser(Principal principal) {
		return principal;
	}

	/* Create User */
	// @PreAuthorize("#oauth2.hasScope('write')")
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/users")
	public User createUser(@Valid @RequestBody UserRegistrationDto user, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User createdUser = userService.create(user);
		// userService.authenticateUserAndSetSession(createdUser, user.getUserName(),
		// user.getPassword(), request);
		//request.login(user.getUserName(), user.getPassword());
		return createdUser;
	}

	@RequestMapping(method = RequestMethod.POST, 
			consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE, 
			path = "/authenticate")
	public UsernamePasswordAuthenticationToken authenticate(@Valid @RequestBody UserRegistrationDto user, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		UsernamePasswordAuthenticationToken token =  userService.authenticateUserAndSetSession(user.getUserName(),user.getPassword(), request);
		return token;
		//response.sendRedirect(redirectUri);
	}
}
