package com.shopme.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.shopme.admin.repository.UserRepository;
import com.shopme.common.entity.User;

@Service
public class ShopmeUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepo;//return Bean new ShopmeUserDetailsService() from WebSecurityConfigure  otherwise UserRepository not autowired you already spend 30 minute 

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		User user = userRepo.getUserByEmail(email);

		if (user != null) {

			return new ShopmeUserDetails(user);
		}

		throw new UsernameNotFoundException("user not found:" + email);
	}

}
