package com.shopme.admin.security;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableAutoConfiguration
@EnableWebSecurity
public class WebSecurityConfigure extends WebSecurityConfigurerAdapter {

	@Bean
	public UserDetailsService userDetailsService() {
		return new ShopmeUserDetailsService();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {

		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth.authenticationProvider(authenticationProvider());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();
	}

//remember works when your userDetailsService() name must be same not userDetailSerice()?
//please do care you spend 1hr to fix this issue😛
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests().antMatchers("/users/**", "/categories/**", "/brands/", "/products/")
				.hasAnyAuthority("Admin", "Coder").anyRequest().authenticated().and().formLogin().loginPage("/login")
				.usernameParameter("email").permitAll().and().logout().permitAll().and().rememberMe()
				.key("d44ee0ba-b0ff-40c7-833a-4541b7e33290").tokenValiditySeconds(2 * 24 * 60 * 60);// 😇/ by default 1
																									// weak
//																									// maintain remember

		// token
		// you can see
		/**
		 * why .key contain random has vlaue bacause of if restart applicatoin then also
		 * session should be maintain or cookies maintain. if you not specifies .key
		 * then after restart application your session should be expires and enter again
		 * credentials. 😇
		 */
		// in cookies remember identifier close browser then
		// again open browser and login shopmeAdmin you will see
		// not asked for credentials,because of remember me
		// maintain cookies token for some times. 😅

		/**
		 * remember me features:suppose use session end or browser exit so for user
		 * browser should remember user credentials for users. Remember login
		 * implementation in spring security. Hash based token(cookies)fast but less
		 * seccure Persistent Token(cookies and databases)slow but secure
		 */

	}

	@Override
	public void configure(WebSecurity web) throws Exception {

		web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
	}

	// just disable login prompt on browser.
//	@Override
//	public void configure(WebSecurity web) {
//
//		web.ignoring().antMatchers("/**");
//
//	}

}
