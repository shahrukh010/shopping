package com.shopme.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.admin.service.UserService;
import com.shopme.common.entity.User;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//
//import com.shopme.admin.security.ShopmeUserDetails;
//import com.shopme.admin.service.UserService;
//import com.shopme.common.entity.User;

@Controller
public class AccountController {
//
//	// please implements controller also.
	@Autowired
	private UserService service;

	@GetMapping("/account")
	public String viewDetails(@AuthenticationPrincipal ShopmeUserDetails logedUser, Model model) {

		String email = logedUser.getUsername();
		User user = service.getByEmail(email);
		model.addAttribute("user", user);

		return "/users/account_form";

	}

}
