package com.shopme.security.oauth;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

public class CustomerOauthUser implements OAuth2User {

	private OAuth2User oAuth2User;
	private String fullName;
	private String clientName;

	public CustomerOauthUser(OAuth2User oAuth2User, String clientName) {

		this.oAuth2User = oAuth2User;
		this.clientName = clientName;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return oAuth2User.getAttributes();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return oAuth2User.getAuthorities();
	}

	@Override
	public String getName() {
		return oAuth2User.getAttribute("name");
	}

	public String getClientName() {
		return this.clientName;
	}

	public String getFullName() {
		return this.fullName != null ? fullName : oAuth2User.getAttribute("name");
	}

	public String getEmail() {

		return oAuth2User.getAttribute("email");
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

}
