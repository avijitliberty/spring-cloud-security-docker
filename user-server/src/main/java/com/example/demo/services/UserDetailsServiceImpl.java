package com.example.demo.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import com.example.demo.exception.ApiRuntimeException;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repositories.RoleRepository;
import com.example.demo.repositories.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;

	@Override
	public List<User> findUsers(String name, Integer id) throws Exception {

		List<User> users = new ArrayList<User>();

		try {

			if (name != null && !name.trim().isEmpty()) {
				User userByName = userRepository.findByName(name);
				if (userByName == null) {
					throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "user does not exist");
				} else {
					users.add(userByName);
				}
			} else if (id != null) {
				User userById = userRepository.findOne(id);
				if (userById == null) {
					throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "user does not exist");
				} else {
					users.add(userById);
				}
			} else {
				for(User user : userRepository.findAll()) {
					users.add(user);
		        }
			}
		} catch (HttpClientErrorException ex) {
			log.error("User not found, throwing USER_NOT_FOUND exception");
			throw new Exception(ex);

		} catch (HttpServerErrorException ex) {
			log.error("Unexpected Exception getting the User Information");
			throw new Exception(ex);
		} 

		return users;
	}

	@Override
	public User create(User user) throws Exception {

		try {

			User existing = userRepository.findByName(user.getUsername());

			if (existing != null) {
				log.info("user already exists: " + user.getUsername());
				throw new ApiRuntimeException(HttpStatus.CONFLICT, "User already exists with same name.");
			} else {

				String hash = encoder.encode(user.getPassword());
				user.setPassword(hash);
				
				Set<Role> rolesToAdd = user.getRoles();
				Set<Role> rolesAdded = new HashSet<Role>();
				
				for(Role role : rolesToAdd) {
					Role existingRole = roleRepository.findByRole(role.getRole());
					if(existingRole != null) {
						rolesAdded.add(existingRole);
					}
					else {
						throw new ApiRuntimeException(HttpStatus.BAD_REQUEST , "Role does not exist"); 
					}
				}
				
				user.setRoles(rolesAdded);
				
				log.info("new user has been created: {}", user.toString());
				return userRepository.save(user);
			}

		} catch (HttpClientErrorException ex) {
			log.error("User not found, throwing USER_NOT_FOUND exception");
			throw new Exception(ex);

		} catch (HttpServerErrorException ex) {
			log.error("Unexpected Exception getting the User Information");
			throw new Exception(ex);
		} 
	}

	@Override
	public User update(User user) throws Exception {
		try {

			User existing = userRepository.findByName(user.getUsername());

			if (existing == null) {
				log.info("user does not exist: " + user.getUsername());
				throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "user does not exist");
			} else {

				// !Here the password gets updated if the user changes it in the request
				String hash = encoder.encode(user.getPassword());
				existing.setPassword(hash);
				
				existing.setName(user.getName());
				existing.setLastName(user.getLastName());
				existing.setEmail(user.getEmail());
				existing.setActive(user.getActive());
				
				
				Set<Role> rolesToAdd = user.getRoles();
				Set<Role> rolesAdded = new HashSet<Role>();
				
				for(Role role : rolesToAdd) {
					Role existingRole = roleRepository.findByRole(role.getRole());
					if(existingRole != null) {
						rolesAdded.add(existingRole);
					}
					else {
						throw new ApiRuntimeException(HttpStatus.BAD_REQUEST , "Role does not exist"); 
					}
				}
				
				existing.setRoles(rolesAdded);
								
				userRepository.save(existing);
				log.info("user updated: {}", existing.toString());
				return userRepository.save(existing);
			}
		} catch (HttpClientErrorException ex) {
			log.error("User not found, throwing USER_NOT_FOUND exception");
			throw new Exception(ex);

		} catch (HttpServerErrorException ex) {
			log.error("Unexpected Exception getting the User Information");
			throw new Exception(ex);
		} 
	}

	@Override
	public String delete(Integer id) throws Exception {

		try {

			User existing = userRepository.findOne(id);

			if (existing == null) {
				log.info("user does not exist: " + id);
				throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "user does not exist");
			} else {
				    
				userRepository.delete(id);
				log.info("deleted user: {}", id);
				return "deleted user: " + id;
			}
		} catch (HttpClientErrorException ex) {
			log.error("User not found, throwing USER_NOT_FOUND exception");
			throw new Exception(ex);

		} catch (HttpServerErrorException ex) {
			log.error("Unexpected Exception getting the User Information");
			throw new Exception(ex);
		} 
	}
}
