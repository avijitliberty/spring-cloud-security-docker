package com.example.demo.controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.Approval.ApprovalStatus;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SessionAttributes("authorizationRequest")
public class LoginController {

	@Autowired
	private ApprovalStore approvalStore;

	private final Logger log = LoggerFactory.getLogger(getClass());

	@RequestMapping("/login")
	public String login(@RequestParam(value = "error", required = false) String error, Model model) {
		if (error != null) {
			model.addAttribute("error", error);
		}
		return "login";
	}

	@RequestMapping("/oauth/confirm_access")
	public ModelAndView getAccessConfirmation(Map<String, Object> model, Principal principal) {
		AuthorizationRequest clientAuth = (AuthorizationRequest) model.remove("authorizationRequest");
		model.put("auth_request", clientAuth);
		model.put("client", clientAuth.getClientId());
		Map<String, String> scopes = new LinkedHashMap<String, String>();
		for (String scope : clientAuth.getScope()) {
			scopes.put(OAuth2Utils.SCOPE_PREFIX + scope, "false");
		}
		for (Approval approval : approvalStore.getApprovals(principal.getName(), "client")) {
			if (clientAuth.getScope().contains(approval.getScope())) {
				scopes.put(OAuth2Utils.SCOPE_PREFIX + approval.getScope(),
						approval.getStatus() == ApprovalStatus.APPROVED ? "true" : "false");
			}
		}
		model.put("scopes", scopes);
		return new ModelAndView("access_confirmation", model);
	}

	@RequestMapping("/exit")
	public void exit(HttpServletRequest request, HttpServletResponse response) {
		new SecurityContextLogoutHandler().logout(request, null, null);
		new CookieClearingLogoutHandler("SECUREUISESSION",
				AbstractRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY).logout(request, response, null);
		try {
			log.info("Redirecting back to referer: " + request.getHeader("referer"));
			response.sendRedirect(request.getHeader("referer"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
