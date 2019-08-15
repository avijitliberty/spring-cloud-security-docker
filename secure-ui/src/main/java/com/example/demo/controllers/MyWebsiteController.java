package com.example.demo.controllers;

import java.security.Principal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.model.Note;
import com.example.demo.model.Role;
import com.example.demo.model.TollUsage;
import com.example.demo.model.User;
import com.example.demo.model.UserRegistrationDto;

/**
 * Web MVC Controller serving two pages:
 *
 * - http://localhost:8080/ -> Shows 'Hello world!' - http://localhost:8080/time
 * -> Shows a page with the current time
 */
@Controller
public class MyWebsiteController {

	@Value("${report.url}")
	private String reportUrl;

	@Value("${users.url}")
	private String usersUrl;

	@Value("${roles.url}")
	private String rolesUrl;

	@Value("${notes.url}")
	private String notesUrl;

	@Value("${auth.url}")
	private String authUrl;

	/*
	 * @ModelAttribute("user") public UserRegistrationDto userRegistrationDto() {
	 * return new UserRegistrationDto(); }
	 */

	@Autowired
	private OAuth2ClientContext clientContext;

	@Autowired
	@Qualifier("authorizationCodeRestTemplate")
	private OAuth2RestOperations authorizationCodeRestTemplate;

	@Autowired
	@Qualifier("clientCredentialsRestTemplate")
	private OAuth2RestOperations clientCredentialsRestTemplate;

	/**
	 * Default index page to verify that our application works.
	 */
	@RequestMapping("/")
	public String loadHome(@RequestParam(value = "success", required = false) String success, Model model) {
		if (success != null) {
			model.addAttribute("success", true);
		}
		return "home";
	}

	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@RequestMapping("/hello")
	public String getGreeting() {
		return "hello";
	}

	@PreAuthorize("hasAuthority('ROLE_USER')")
	@RequestMapping("/time")
	public ModelAndView time() {
		ModelAndView mav = new ModelAndView("time");
		mav.addObject("currentTime", getCurrentTime());
		return mav;
	}

	private String getCurrentTime() {
		return LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME);
	}

	@RequestMapping("/reports")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String loadReports(Model model) {

		OAuth2AccessToken t = clientContext.getAccessToken();
		System.out.println("Token: " + t.getValue());

		ResponseEntity<ArrayList<TollUsage>> tolls = authorizationCodeRestTemplate.exchange(reportUrl, HttpMethod.GET,
				null, new ParameterizedTypeReference<ArrayList<TollUsage>>() {
				});

		model.addAttribute("tolls", tolls.getBody());

		return "reports";
	}

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/get-user/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String getUserById(Model model, @PathVariable Integer id) {

		ResponseEntity<User> userResponse = authorizationCodeRestTemplate.exchange(usersUrl + "/" + id, HttpMethod.GET,
				null, new ParameterizedTypeReference<User>() {
				});

		ResponseEntity<HashSet<Role>> roleResponse = authorizationCodeRestTemplate.exchange(rolesUrl, HttpMethod.GET,
				null, new ParameterizedTypeReference<HashSet<Role>>() {
				});

		model.addAttribute("user", userResponse.getBody());
		model.addAttribute("allRoles", roleResponse.getBody());

		return "edit-user";
	}
	
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/get-note/{id}")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String getNoteById(Model model, @PathVariable Integer id) {

		ResponseEntity<Note> notesResponse = authorizationCodeRestTemplate.exchange(notesUrl + "/" + id, HttpMethod.GET,
				null, new ParameterizedTypeReference<Note>() {
				});

		model.addAttribute("note", notesResponse.getBody());
		return "edit-note";
	}

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/delete-user/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String deleteUser(Model model, @PathVariable Integer id) {

		ResponseEntity<String> deleteResponse = authorizationCodeRestTemplate.exchange(usersUrl + "/" + id,
				HttpMethod.DELETE, null, new ParameterizedTypeReference<String>() {
				});

		if (deleteResponse.getStatusCode() == HttpStatus.OK) {
			System.out.println("user deleted " + deleteResponse.getBody());
		}

		ResponseEntity<ArrayList<User>> users = authorizationCodeRestTemplate.exchange(usersUrl, HttpMethod.GET, null,
				new ParameterizedTypeReference<ArrayList<User>>() {
				});

		model.addAttribute("users", users.getBody());
		return "users";
	}
	
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/delete-note/{id}")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String deleteNote(Model model, @PathVariable Integer id, HttpServletRequest request) {
		
		String currentLoggedInUser = request.getUserPrincipal().getName();

		ResponseEntity<String> deleteResponse = authorizationCodeRestTemplate.exchange(notesUrl + "/" + id,
				HttpMethod.DELETE, null, new ParameterizedTypeReference<String>() {
				});

		if (deleteResponse.getStatusCode() == HttpStatus.OK) {
			System.out.println("note deleted " + deleteResponse.getBody());
		}

		ResponseEntity<ArrayList<Note>> responseEntity = authorizationCodeRestTemplate.exchange(notesUrl + "?createdBy=" + currentLoggedInUser, HttpMethod.GET, null,
				new ParameterizedTypeReference<ArrayList<Note>>() {
				});

		model.addAttribute("notes", responseEntity.getBody());
		return "notes";
	}
	
	
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, path = "/edit-user")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String editUser(@Valid final User user, BindingResult result, Model model) {

		if (result.hasErrors()) {
			return "edit-user";
		}

		// request entity is created with request body and headers
		HttpEntity<User> requestEntity = new HttpEntity<>(user);
		ResponseEntity<User> responseEntity = authorizationCodeRestTemplate.exchange(usersUrl, HttpMethod.PUT,
				requestEntity, new ParameterizedTypeReference<User>() {
				});

		if (responseEntity.getStatusCode() == HttpStatus.OK) {
			User createdUser = responseEntity.getBody();
			System.out.println("user response retrieved " + responseEntity.getBody());
		}

		ResponseEntity<ArrayList<User>> users = authorizationCodeRestTemplate.exchange(usersUrl, HttpMethod.GET, null,
				new ParameterizedTypeReference<ArrayList<User>>() {
				});

		model.addAttribute("users", users.getBody());
		return "users";
	}
	
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, path = "/edit-note")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String editNote(@Valid final Note note, BindingResult result, Model model, HttpServletRequest request) {

		if (result.hasErrors()) {
			return "edit-note";
		}
		
		String currentLoggedInUser = request.getUserPrincipal().getName();
		// request entity is created with request body and headers
		HttpEntity<Note> requestEntity = new HttpEntity<>(note);
		ResponseEntity<Note> responseEntity = authorizationCodeRestTemplate.exchange(notesUrl, HttpMethod.PUT,
				requestEntity, new ParameterizedTypeReference<Note>() {
				});

		if (responseEntity.getStatusCode() == HttpStatus.OK) {
			Note editedNote = responseEntity.getBody();
			System.out.println("note response retrieved " + editedNote);
		}

		ResponseEntity<ArrayList<Note>> notes = authorizationCodeRestTemplate.exchange(notesUrl + "?createdBy=" + currentLoggedInUser, HttpMethod.GET, null,
				new ParameterizedTypeReference<ArrayList<Note>>() {
				});

		model.addAttribute("notes", notes.getBody());
		return "notes";
	}

	@RequestMapping("/users")
	// @PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String loadUsers(Model model) {

		OAuth2AccessToken t = clientContext.getAccessToken();
		System.out.println("Token: " + t.getValue());

		ResponseEntity<ArrayList<User>> users = authorizationCodeRestTemplate.exchange(usersUrl, HttpMethod.GET, null,
				new ParameterizedTypeReference<ArrayList<User>>() {
				});

		model.addAttribute("users", users.getBody());

		return "users";
	}

	@RequestMapping("/signup")
	// @PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String showSignUpForm(Model model) {
		model.addAttribute("user", new UserRegistrationDto());
		//model.addAttribute("success", false);
		return "registration";
	}
	
	@RequestMapping("/stickynote")
	// @PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String showAddNoteForm(Model model) {
		model.addAttribute("note", new Note());
		return "add-note";
	}

	@RequestMapping("/registration")
	// @PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String addUser(@Valid final UserRegistrationDto userDto, BindingResult result, HttpServletRequest request,
			Model model) {
		if (result.hasErrors()) {
			return "registration";
		}

		Set<Role> rolesToAdd = new HashSet<Role>();
		Role role = new Role("USER");
		rolesToAdd.add(role);
		userDto.setRoles(rolesToAdd);

		// request entity is created with request body and headers
		HttpEntity<UserRegistrationDto> requestEntity = new HttpEntity<>(userDto);

		ResponseEntity<User> responseEntity = clientCredentialsRestTemplate.exchange(usersUrl, HttpMethod.POST,
				requestEntity, new ParameterizedTypeReference<User>() {
				});
		if (responseEntity.getStatusCode() == HttpStatus.OK) {
			User createdUser = responseEntity.getBody();
			System.out.println("user response retrieved " + responseEntity.getBody());
			//model.addAttribute("success", true);
			//model.addAttribute("user", userDto);
			new SecurityContextLogoutHandler().logout(request, null, null);
		}
		return "redirect:/?success";
	}

	@RequestMapping("/notes")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String loadNotes(Model model, HttpServletRequest request) {

		OAuth2AccessToken t = clientContext.getAccessToken();
		String currentLoggedInUser = request.getUserPrincipal().getName();
		System.out.println("Token: " + t.getValue());

		ResponseEntity<ArrayList<Note>> notes = authorizationCodeRestTemplate.exchange(notesUrl + "?createdBy=" + currentLoggedInUser, HttpMethod.GET, null,
				new ParameterizedTypeReference<ArrayList<Note>>() {
				});

		model.addAttribute("notes", notes.getBody());

		return "notes";
	}
	
	@RequestMapping("/add-note")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String addNotes(@Valid final Note note, BindingResult result, Model model, HttpServletRequest request) {
		
		if (result.hasErrors()) {
			return "add-note";
		}


		// request entity is created with request body and headers
		HttpEntity<Note> requestEntity = new HttpEntity<>(note);

		ResponseEntity<Note> responseEntity = authorizationCodeRestTemplate.exchange(notesUrl, HttpMethod.POST, requestEntity,
				new ParameterizedTypeReference<Note>() {
				});
		
		if (responseEntity.getStatusCode() == HttpStatus.OK) {
			Note createdNote = responseEntity.getBody();
			System.out.println("note response retrieved " + createdNote);
		}
		
		String currentLoggedInUser = request.getUserPrincipal().getName();

		ResponseEntity<ArrayList<Note>> notes = authorizationCodeRestTemplate.exchange(notesUrl + "?createdBy=" + currentLoggedInUser, HttpMethod.GET, null,
				new ParameterizedTypeReference<ArrayList<Note>>() {
				});

		model.addAttribute("notes", notes.getBody());

		return "notes";
	}

	/** Forbidden page. */
	@RequestMapping("/403")
	public String forbidden() {
		return "403";
	}

	/** Simulation of an exception. */
	@RequestMapping("/simulateError")
	public void simulateError() {
		throw new RuntimeException("This is a simulated error message");
	}
}
