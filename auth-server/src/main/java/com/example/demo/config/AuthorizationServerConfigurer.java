package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

/**
 * Our configuration for the OAuth2 Authorization Server.
 */
@Configuration
@EnableAuthorizationServer
// The Resource Server configuration creates a security filter with '@Order(3)' by default,
// so by moving the authorization server to '@Order(6)' we ensure that the rule for '/user' takes precedence.
@Order(6)
public class AuthorizationServerConfigurer extends AuthorizationServerConfigurerAdapter {

	@Value("${security.oauth2.client.registered-redirect-uri}")
	private String redirectUri;
	@Autowired
	private UserDetailsService userService;
	/*
	 * To use password grant you need to provide an authentication manager to the
	 * authorization server, so it can authenticate users. If it's a Spring Boot
	 * application there is always an AuthenticationManager available to
	 * be @Autowired.
	 */
	@Autowired
	@Qualifier("authenticationManagerBean")
	private AuthenticationManager authenticationManager;
	
	@Autowired
    private TokenStore tokenStore;

	@Bean
	public TokenStore tokenStore() {
		return new InMemoryTokenStore();
	}

	// password encrypted
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.tokenStore(tokenStore).authenticationManager(authenticationManager).userDetailsService(userService);
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
		// .passwordEncoder(passwordEncoder()); To send encrypted password from caller
		// enable this.
	}

	@Bean
	public ApprovalStore approvalStore() {
		TokenApprovalStore store = new TokenApprovalStore();
		store.setTokenStore(tokenStore);
		return store;
	}

	/**
	 * We configure the client details in-memory. Alternatively, we could fetch this
	 * information from a database.
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
				// Credentials for the client: secureui / secureui-secret
				.withClient("secureui").secret("secureui-secret")
				// Only allow redirecting to 'http://localhost:8080/**' when logging in
				.redirectUris(redirectUri)
				// We only use the authorization_code grant type, with support for refresh
				// tokens
				.authorizedGrantTypes("authorization_code", "refresh_token","password", "client_credentials")
				// We can define our own scopes here
				.scopes("read", "write")
				// If we do not auto approve, the user is asked upon login if (s)he approves
				.and()
				.withClient("securecli").secret("securecli-secret")
				.authorizedGrantTypes("authorization_code", "refresh_token", "password", "client_credentials")
				.scopes("read", "write")
				.autoApprove(true);
	}

}
