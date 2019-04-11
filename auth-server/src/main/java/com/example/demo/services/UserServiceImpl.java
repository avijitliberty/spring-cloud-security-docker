package com.example.demo.services;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import com.example.demo.exception.ApiRuntimeException;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.model.UserRegistrationDto;
import com.example.demo.repositories.RoleRepository;
import com.example.demo.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	@Qualifier("authenticationManagerBean")
	protected AuthenticationManager authenticationManager;

	@Override
	public User create(UserRegistrationDto user) throws Exception {

		try {

			User existing = userRepository.findByusername(user.getUserName());

			if (existing != null) {
				log.info("user already exists: " + existing.getUsername());
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

				for (Role role : rolesToAdd) {
					Role existingRole = roleRepository.findByRole(role.getRole());
					if (existingRole != null) {
						rolesAdded.add(existingRole);
					} else {
						throw new ApiRuntimeException(HttpStatus.BAD_REQUEST, "Role does not exist");
					}
				}

				newUser.setRoles(rolesAdded);

				log.info("new user has been created: {}", newUser.toString());
				return userRepository.save(newUser);
			}

		} catch (HttpServerErrorException ex) {
			log.error("Unexpected Exception getting the User Information");
			throw new Exception(ex);
		}
	}

	public UsernamePasswordAuthenticationToken authenticateUserAndSetSession(String userName, String password, HttpServletRequest request) {

		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userName, password);
		token.setDetails(new WebAuthenticationDetails(request));
		
		try {
			token = (UsernamePasswordAuthenticationToken) authenticationManager.authenticate(token);
		    SecurityContext sc = SecurityContextHolder.getContext();
		    sc.setAuthentication(token);
				    
		 // generate session if one doesn't exist
		 //	HttpSession session = request.getSession(true);
		 //	session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,sc);
					
			 //session.setAttribute("name", user.getUsername());
			 //session.setAttribute("authorities", token.getAuthorities());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return token;
	}

}
