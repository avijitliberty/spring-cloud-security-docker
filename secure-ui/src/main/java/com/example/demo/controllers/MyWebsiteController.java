package com.example.demo.controllers;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.model.Note;
import com.example.demo.model.TollUsage;
import com.example.demo.model.User;

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
	
	@Value("${notes.url}")
	private String notesUrl;
	
	@Autowired
	private OAuth2ClientContext clientContext;

	@Autowired
	private OAuth2RestTemplate oauth2RestTemplate;

	/**
	 * Default index page to verify that our application works.
	 */
	@RequestMapping("/")
	public String loadHome() {
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

		ResponseEntity<ArrayList<TollUsage>> tolls = oauth2RestTemplate.exchange(reportUrl, HttpMethod.GET, null,
				new ParameterizedTypeReference<ArrayList<TollUsage>>() {
				});

		model.addAttribute("tolls", tolls.getBody());

		return "reports";
	}
	
	@RequestMapping(method = RequestMethod.GET, 
	                produces = MediaType.APPLICATION_JSON_VALUE, 
	                path ="/users/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String getUserById(Model model, @PathVariable Integer id) {
		
		OAuth2AccessToken t = clientContext.getAccessToken();
		System.out.println("Token: " + t.getValue());
		String url = usersUrl + "/" + id;

		ResponseEntity<User> user = oauth2RestTemplate.exchange(url, HttpMethod.GET, null,
				new ParameterizedTypeReference<User>() {
				});
		model.addAttribute("user", user.getBody());
		return "user-details";
	}
	
	@RequestMapping("/users")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String loadUsers(Model model) {

		OAuth2AccessToken t = clientContext.getAccessToken();
		System.out.println("Token: " + t.getValue());

		ResponseEntity<ArrayList<User>> users = oauth2RestTemplate.exchange(usersUrl, HttpMethod.GET, null,
				new ParameterizedTypeReference<ArrayList<User>>() {
				});

		model.addAttribute("users", users.getBody());

		return "users";
	}
	
	@GetMapping("/signup")
	//@PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showSignUpForm(User user) {
        return "add-user";
    }
	
	@RequestMapping("/adduser")
	//@PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String addUser(@Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-user";
        }
        return "home";
    }
	
	@RequestMapping("/notes")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String loadNotes(Model model) {

		OAuth2AccessToken t = clientContext.getAccessToken();
		System.out.println("Token: " + t.getValue());

		ResponseEntity<ArrayList<Note>> notes = oauth2RestTemplate.exchange(notesUrl, HttpMethod.GET, null,
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
