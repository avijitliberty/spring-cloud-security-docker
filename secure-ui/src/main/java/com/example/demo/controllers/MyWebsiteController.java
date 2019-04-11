package com.example.demo.controllers;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	
	@Value("${security.oauth2.client.accessTokenUri}")
	private String accessTokenUri;
	@Value("${security.oauth2.client.userAuthorizationUri}")
	private String authorizeUrl;
	@Value("${security.oauth2.client.clientId}")
	private String clientId;
	@Value("${security.oauth2.client.clientSecret}")
	private String clientSecret;

	@Value("${report.url}")
	private String reportUrl;
	
	@Value("${users.url}")
	private String usersUrl;
	
	@Value("${notes.url}")
	private String notesUrl;
	
	@Value("${auth.url}")
	private String authUrl;
	
	@ModelAttribute("user")
    public UserRegistrationDto userRegistrationDto() {
        return new UserRegistrationDto();
    }
	
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
	
	@RequestMapping("/signup")
	//@PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showSignUpForm(Model model) {
        return "registration";
    }
	
	@RequestMapping("/registration")
	//@PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String addUser(@ModelAttribute("user") @Valid UserRegistrationDto userDto, 
            BindingResult result, HttpServletRequest request, Model model) {
        if (result.hasErrors()) {
            return "registration";
        }
        
        Set<Role> rolesToAdd = new HashSet<Role>();
        Role role = new Role();
        role.setRole("USER");
        rolesToAdd.add(role);
        userDto.setRoles(rolesToAdd);
        
        ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails();
		resource.setClientId(clientId);
		resource.setClientSecret(clientSecret);
	    resource.setAccessTokenUri(accessTokenUri);
	    resource.setScope(Arrays.asList("read","write"));
	   		
        OAuth2RestTemplate template = new OAuth2RestTemplate(resource, new DefaultOAuth2ClientContext(new DefaultAccessTokenRequest()));
        
        ClientCredentialsAccessTokenProvider accessTokenProvider = new ClientCredentialsAccessTokenProvider();
		accessTokenProvider.setRequestFactory(new SimpleClientHttpRequestFactory());
        
        
        template.setAccessTokenProvider(accessTokenProvider);
        //request entity is created with request body and headers
        HttpEntity<UserRegistrationDto> requestEntity = new HttpEntity<>(userDto);

    	ResponseEntity<User> responseEntity = template.exchange(usersUrl, HttpMethod.POST, requestEntity,
				new ParameterizedTypeReference<User>() {
				});
    	if(responseEntity.getStatusCode() == HttpStatus.OK){
            User createdUser = responseEntity.getBody();
            System.out.println("user response retrieved " + responseEntity.getBody());
            //return "redirect:/registration?success";
            
            /*ResponseEntity<UsernamePasswordAuthenticationToken> tokenEntity = template.exchange(authUrl, HttpMethod.POST, requestEntity,
    				new ParameterizedTypeReference<UsernamePasswordAuthenticationToken>() {
    				});
            
              //if(tokenEntity.getStatusCode() == HttpStatus.OK){
                UsernamePasswordAuthenticationToken auth = tokenEntity.getBody();
            	HttpSession session = request.getSession(true);
                SecurityContext sc = SecurityContextHolder.getContext();
                sc.setAuthentication(auth);
                session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,sc);
                session.setAttribute("principal.name", auth.getName());
   			    session.setAttribute("authorities", auth.getAuthorities());
            //}		
*/        }
    	return "redirect:/registration?success";
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
