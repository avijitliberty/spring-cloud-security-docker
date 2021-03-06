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
import com.example.demo.model.UserRegistrationDto;
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

			if (name != null && !name.trim().isEmpty()) {
				User userByName = userRepository.findByusername(name);
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
		return users;
	}

	@Override
	public User create(UserRegistrationDto user) throws Exception {


			User existing = userRepository.findByusername(user.getUserName());

			if (existing != null) {
				log.info("user already exists: " + user.getUserName());
				throw new ApiRuntimeException(HttpStatus.CONFLICT, "User already exists with same name.");
			} else {

				User newUser = new User();
				newUser.setFirstName(user.getFirstName());
				newUser.setLastName(user.getLastName());
				newUser.setActive(1);
				newUser.setEmail(user.getEmail());
				newUser.setUsername(user.getUserName());
							
				String hash = encoder.encode(user.getPassword());
				newUser.setPassword(hash);
				
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
				
				newUser.setRoles(rolesAdded);
				
				log.info("new user has been created: {}", newUser.toString());
				return userRepository.save(newUser);
			}
	}

	@Override
	public User update(User user) throws Exception {

			User existing = userRepository.findByusername(user.getUsername());

			if (existing == null) {
				log.info("user does not exist: " + user.getUsername());
				throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "user does not exist");
			} else {

				// !Here the password gets updated if the user changes it in the request
				String hash = encoder.encode(user.getPassword());
				existing.setPassword(hash);
				
				existing.setFirstName(user.getFirstName());
				existing.setLastName(user.getLastName());
				existing.setEmail(user.getEmail());
				existing.setUsername(user.getUsername());
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
								
				log.info("user updated: {}", existing.toString());
				return userRepository.save(existing);
			} 
	}

	@Override
	public String delete(Integer id) throws Exception {

		    User existing = userRepository.findOne(id);

			if (existing == null) {
				log.info("user does not exist: " + id);
				throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "user does not exist");
			} else {
				    
				userRepository.delete(id);
				log.info("deleted user: {}", id);
				return "deleted user: " + id;
			}
	}
}
