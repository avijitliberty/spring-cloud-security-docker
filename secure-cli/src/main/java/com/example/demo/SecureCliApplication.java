package com.example.demo;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;

@SpringBootApplication
public class SecureCliApplication implements CommandLineRunner {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${security.oauth2.client.accessTokenUri}")
	private String accessTokenUri;
	@Value("${security.oauth2.client.userAuthorizationUri}")
	private String authorizeUrl;
	@Value("${security.oauth2.resource.userInfoUri}")
	private String userInfoUri;
	@Value("${security.oauth2.client.clientId}")
	private String clientId;
	@Value("${security.oauth2.client.clientSecret}")
	private String clientSecret;
	@Value("${security.oauth2.client.scope}")
	private String scope;
	@Value("${report.url}")
	private String reportUrl;

	public static void main(String[] args) {
		SpringApplication.run(SecureCliApplication.class, args);
	}

	private OAuth2ProtectedResourceDetails resource() {

		ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
		resource.setUsername("jim");
		resource.setPassword("demo");
		resource.setClientId(clientId);
		resource.setClientSecret(clientSecret);
		resource.setAccessTokenUri(accessTokenUri);
		resource.setClientAuthenticationScheme(AuthenticationScheme.header);
		resource.setScope(Arrays.asList(scope));
		return resource;
	}

	@Override
	public void run(String... args) throws Exception {
		logger.info("cron job started");

		OAuth2RestTemplate template = new OAuth2RestTemplate(resource());
		// could also get scopes: template.getAccessToken().getScope()
		String token = template.getAccessToken().toString();// .getValue();

		logger.info("Token: " + token);

		String s = template.getForObject(reportUrl, String.class);

		logger.info("Result: " + s);

	}

}
