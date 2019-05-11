package com.example.demo.services;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

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

	public Principal authenticateUserAndSetSession(String userName, String password, HttpServletRequest request) {

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
		return (Principal) token.getPrincipal();
	}

}
