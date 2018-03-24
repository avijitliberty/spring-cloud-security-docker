package com.example.demo.controllers;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.User;
import com.example.demo.services.UserService;

/**
 * Because this application is also a User Info Resource Server, we expose info about the logged in user at:
 *
 *     http://localhost:9090/auth/user
 */
@RestController
public class UserController {
	
	@Autowired
	private UserService userService;

    /**
	/* Controller method invoked by the oauth2 client to verify the user 
	**/
	@RequestMapping(value = "/user", 
			        method = RequestMethod.GET, 
			        produces = MediaType.APPLICATION_JSON_VALUE)
	public Principal getUser(Principal principal) {
		return principal;
	}
	
	/* Create User */
	//@PreAuthorize("#oauth2.hasScope('toll_report')")
	@RequestMapping(method = RequestMethod.POST, 
			        consumes = MediaType.APPLICATION_JSON_VALUE, 
			        produces = MediaType.APPLICATION_JSON_VALUE, 
			        path = "/users")
	public User createUser(@Valid @RequestBody User user) throws Exception {
		return userService.create(user);
	}

	/* Read Users */
	//@PreAuthorize("#oauth2.hasScope('toll_read')")
	@RequestMapping(method = RequestMethod.GET, 
			        produces = MediaType.APPLICATION_JSON_VALUE, 
			        path = "/users")
	public List<User> getUser(@RequestParam(value = "name", required = false) String name) throws Exception {
		if (name != null && !name.trim().isEmpty()) {
			return userService.findUsers(name, null);
		} else {
			return userService.findUsers(null, null);
		}
	}
	
	/* Read Users by Id */
	//@PreAuthorize("#oauth2.hasScope('toll_read')")
	@RequestMapping(method = RequestMethod.GET, 
			        produces = MediaType.APPLICATION_JSON_VALUE, 
			        path = "/users/{id}")
	public User getUserById(@PathVariable Integer id) throws Exception {
		return userService.findUsers(null, id).get(0);
	}
	
	/* Update */
	//@PreAuthorize("#oauth2.hasScope('toll_report')")
	@RequestMapping(method = RequestMethod.PUT, 
			        consumes = MediaType.APPLICATION_JSON_VALUE, 
			        produces = MediaType.APPLICATION_JSON_VALUE, 
			        path = "/users")
	public User updateUser(@Valid @RequestBody User user) throws Exception {
		return userService.update(user);
	}

	/* Delete */
	//@PreAuthorize("#oauth2.hasScope('toll_report')")
	@RequestMapping(method = RequestMethod.DELETE, 
			        path = "/users/{id}")
	public String deleteUser(@PathVariable Integer id) throws Exception {
		return userService.delete(id);
	}
}
