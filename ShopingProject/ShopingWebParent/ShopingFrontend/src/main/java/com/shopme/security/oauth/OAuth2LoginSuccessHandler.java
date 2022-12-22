package com.shopme.security.oauth;

import java.io.IOException;
import java.time.Instant;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.Ssl.ClientAuth;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerService;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired
	private CustomerService customerService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {

		CustomerOauthUser auth2User = (CustomerOauthUser) authentication.getPrincipal();
		String name = auth2User.getName();
		String email = auth2User.getEmail();
		String countryCode = request.getLocale().getCountry();
		String clientName = auth2User.getClientName();
		System.out.println(clientName);
		AuthenticationType type = getAuthenticationType(clientName);

		Customer customer = customerService.getCustomerByEmail(email);

		if (customer == null) {

			customerService.newCustomerAuthLogin(name, email, countryCode, type);
		} else {

			auth2User.setFullName(customer.getFullName());
			customerService.updateAuthentication(customer, type);

		}

		System.out.println("client information" + name + " " + email);

		super.onAuthenticationSuccess(request, response, authentication);
	}

	private AuthenticationType getAuthenticationType(String clientName) {

		return clientName.equals("Google") ? AuthenticationType.GOOGLE
				: clientName.equals("Facebook") ? AuthenticationType.FACEBOOK : AuthenticationType.DATABASE;
	}

}
