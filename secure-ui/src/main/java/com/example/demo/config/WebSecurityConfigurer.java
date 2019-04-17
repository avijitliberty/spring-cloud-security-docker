package com.example.demo.config;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import com.example.demo.exception.CustomUrlAuthenticationFailureHandler;

/**
 * The Web Security configuration for My Website.
 */
@Configuration
@EnableOAuth2Sso
@EnableGlobalMethodSecurity(prePostEnabled = true) // Allow method annotations like @PreAuthorize
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {
	
	private static Logger logger = LoggerFactory.getLogger(WebSecurityConfigurer.class);
	
	@Value("${security.oauth2.client.logoutUri}")
	private String logoutUrl;
	@Value("${security.oauth2.client.accessTokenUri}")
	private String accessTokenUri;
	@Value("${security.oauth2.client.userAuthorizationUri}")
	private String authorizeUrl;
	@Value("${security.oauth2.resource.user-info-uri}")
	private String userInfoUri;
	@Value("${security.oauth2.client.clientId}")
	private String clientId;
	@Value("${security.oauth2.client.clientSecret}")
	private String clientSecret;
	@Autowired
	private OAuth2ClientContext oauth2ClientContext;
	
	@Autowired
	private OAuth2ClientContextFilter oauth2ClientFilter;
    @Autowired	
    @Qualifier("authorizationCodeRestTemplate")
	private OAuth2RestTemplate authorizationCodeRestTemplate;
    @Autowired(required = false)
	private ClientHttpRequestFactory clientHttpRequestFactory;

	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.
        authorizeRequests()
                .antMatchers(
                  "/",
                  "/login**",
                  "/403",
                  "/signup",
                  "/registration",
                  "/js/**",
                  "/css/**",
                  "/img/**",
                  "/webjars/**").permitAll()                       // Allow navigating to index page,
                .anyRequest().authenticated()                      // but secure all the other URLs
          .and()
                .addFilterAfter(oauth2ClientFilter,
                        ExceptionTranslationFilter.class)
                .addFilterBefore(oAuth2AuthenticationProcessingFilter(),
                        FilterSecurityInterceptor.class)
    			.logout()
    			.logoutSuccessUrl(logoutUrl)
    			.invalidateHttpSession(true)
           	    .and().csrf().disable();
    }

   /* @Bean
    public OAuth2RestTemplate oauth2RestTemplate(OAuth2ProtectedResourceDetails resource, OAuth2ClientContext context) {
    	OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resource, context);
    	return restTemplate;
    }*/
    
 /*   @Bean
	public OAuth2RestTemplate oauth2RestTemplate() {
		return new OAuth2RestTemplate(resource(), oauth2ClientContext);
	}*/
    
    @Bean
	public OAuth2ClientAuthenticationProcessingFilter oAuth2AuthenticationProcessingFilter() throws Exception {
		OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(
				"/login");
		
		filter.setRestTemplate(authorizationCodeRestTemplate);
		UserInfoTokenServices tokenServices = new UserInfoTokenServices(userInfoUri, clientId);
		filter.setTokenServices(tokenServices);
		filter.setAuthenticationFailureHandler(customUrlAuthenticationFailureHandler());
		return filter;
	}
    
    @Bean
	public CustomUrlAuthenticationFailureHandler customUrlAuthenticationFailureHandler() throws Exception {
    	CustomUrlAuthenticationFailureHandler handler = new CustomUrlAuthenticationFailureHandler("/403");
		return handler;
	}

	/*
	 * ClientHttpRequestFactory is autowired and checked in case somewhere in
	 * your configuration you provided {@link ClientHttpRequestFactory}
	 * implementation Bean where you defined specifics of your connection, if
	 * not it is instantiated here with {@link SimpleClientHttpRequestFactory}
	 */
	private ClientHttpRequestFactory getClientHttpRequestFactory() {
		if (clientHttpRequestFactory == null) {
			clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
		}
		return clientHttpRequestFactory;
	}
    
    public OAuth2RestTemplate prepareTemplate(OAuth2RestTemplate template, boolean isClient) {
		template.setRequestFactory(getClientHttpRequestFactory());
		if (isClient) {
			template.setAccessTokenProvider(clientAccessTokenProvider());
		} else {
			template.setAccessTokenProvider(authorizationAccessTokenProvider());
		}
		return template;
	}
    
    /**
	 * {@link AccessTokenProviderChain} throws
	 * InsufficientAuthenticationException in
	 * obtainAccessToken(OAuth2ProtectedResourceDetails resource,
	 * AccessTokenRequest request) if user is not authorized, but since we are
	 * setting our own accessTokenProvider() on OAuth2RestTemplate this
	 * condition is not being checked, thus exception is not being thrown and
	 * requirement for user to be logged in is skipped
	 */
	@Bean
	public AccessTokenProvider authorizationAccessTokenProvider() {
		AuthorizationCodeAccessTokenProvider accessTokenProvider = new AuthorizationCodeAccessTokenProvider();
		accessTokenProvider.setRequestFactory(getClientHttpRequestFactory());
		return accessTokenProvider;
	}

	@Bean
	public AccessTokenProvider clientAccessTokenProvider() {
		ClientCredentialsAccessTokenProvider accessTokenProvider = new ClientCredentialsAccessTokenProvider();
		accessTokenProvider.setRequestFactory(getClientHttpRequestFactory());
		return accessTokenProvider;
	}

	@Bean
	@Qualifier("authorizationCodeRestTemplate")
	public OAuth2RestTemplate authorizationCodeRestTemplate() {

		OAuth2RestTemplate template = new OAuth2RestTemplate(authorizationCodeResourceDetails(), oauth2ClientContext);
		return prepareTemplate(template, false);
	}
    
    @Bean
	@Qualifier("clientCredentialsRestTemplate")
	public OAuth2RestTemplate clientCredentialsRestTemplate() {

		OAuth2RestTemplate template = new OAuth2RestTemplate(clientCredentialsResourceDetails(), oauth2ClientContext);
		return prepareTemplate(template, true);
	}

    @Bean
	@Qualifier("authorizationCodeResourceDetails")
	public OAuth2ProtectedResourceDetails authorizationCodeResourceDetails() {
	  
	  AuthorizationCodeResourceDetails resource = new AuthorizationCodeResourceDetails(); 
	  resource.setClientId(clientId);
	  resource.setClientSecret(clientSecret);
	  resource.setAccessTokenUri(accessTokenUri);
	  resource.setUserAuthorizationUri(authorizeUrl); 	  
	  return resource; 
	}
    
    @Bean
	@Qualifier("clientCredentialsResourceDetails")
	public OAuth2ProtectedResourceDetails clientCredentialsResourceDetails() {
	  
      ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails(); 
	  resource.setClientId(clientId);
	  resource.setClientSecret(clientSecret);
	  resource.setAccessTokenUri(accessTokenUri);
	  resource.setScope(Arrays.asList("read","write"));
	  return resource; 
	}
}
