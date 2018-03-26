package com.example.demo.exception;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.stereotype.Component;

@Component("CustomUrlAuthenticationFailureHandler")
public class CustomUrlAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler{
	
	private static Logger logger = LoggerFactory.getLogger(CustomUrlAuthenticationFailureHandler.class);

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	private String customFailureUrl;
	
	public CustomUrlAuthenticationFailureHandler(String defaultFailureUrl) {
		super(defaultFailureUrl);
		this.customFailureUrl = defaultFailureUrl;
	}
	
	public CustomUrlAuthenticationFailureHandler() {
		super();
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		logger.error("Redirecting to /403 page");
		logger.error("errorCode: " + exception.getCause());
		logger.error("errorMessage: " + exception.getMessage());
		new SecurityContextLogoutHandler().logout(request, response, null);
		new CookieClearingLogoutHandler("SECUREUISESSION", AbstractRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY).logout(request, response, null);
		redirectStrategy.sendRedirect(request, response, customFailureUrl);
	}
}
