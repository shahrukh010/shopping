package com.shopme.admin.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.exception.UserNotFoundException;
import com.shopme.admin.export.UserCsvExporter;
import com.shopme.admin.export.UserExcelExporter;
import com.shopme.admin.export.UserPdfExporter;
import com.shopme.admin.service.UserService;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/users")
	public String listFirstPage(Model model, RedirectAttributes redirectAttributes, String keyword) {

		return listByPage(1, model, redirectAttributes, "firstName", "desc", null);
	}

	@GetMapping("/users/page/{pageNum}")
	public String listByPage(@PathVariable(value = "pageNum") int pageNum, Model model,
			RedirectAttributes redirectAttributes, @Param("sortField") String sortField,
			@Param("sortDir") String sortDir, String keyword) {
		/**
		 * sortField:column sortDir:asc/desc
		 */

		System.out.println("sortField" + sortField);
		System.out.println("sortDir" + sortDir);
		Page<User> page = userService.listByPage(pageNum, sortField, sortDir, keyword);
		List<User> listUsers = page.getContent();

		List<User> users = new ArrayList<>();

		listUsers.forEach(usr -> {

			users.add(usr);
		});

		// reverse list debuging
//		List<?> shallow = users.subList(0, users.size());
//		System.out.println("shallow" + shallow);
//
//		Collections.reverse(shallow);
//		System.out.println(shallow);

//		System.out.println("----" + users);
//**********************************************************************

		int startCount = (pageNum - 1) * userService.USER_PER_PAGE + 1;
		int endCount = (startCount) + userService.USER_PER_PAGE - 1;

		if (endCount > page.getTotalElements())
			endCount = (int) page.getTotalElements();

		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("listUsers", listUsers);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);

		// Debugging
//		System.out.println("page" + page);
//		System.out.println("total Element: " + page.getTotalElements());
//		System.out.println("total page: " + page.getTotalPages());

//**********************************************************************

		return "users";
	}

	@GetMapping("/users/new")
	public String newUser(Model model) {

		List<Role> roles = userService.listRoles();
		User user = new User();
		user.setEnabled(true);
		model.addAttribute("user", user);
		model.addAttribute("listRoles", roles);
		model.addAttribute("pageTitle", "Create New User");

		return "user_form";
	}

	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {

		System.out.println(multipartFile.getOriginalFilename());
		System.out.println(user);
		if (!multipartFile.isEmpty()) {
			System.out.println(multipartFile.getOriginalFilename());
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

			user.setPhotos(fileName);
			User saveUsr = userService.saveUser(user);
			String uploadDir = "user-photo/" + user.getId();
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			user.setPhotos(null);
			userService.saveUser(user);
		}

		redirectAttributes.addFlashAttribute("message", "The user has been saved successfully.");

		return "redirect:/users";
	}

	@GetMapping("/users/edit/{id}")
	public String editorUser(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {

		try {
			List<Role> role = userService.listRoles();
			User user = userService.getById(id);
			model.addAttribute("user", user);
			model.addAttribute("listRoles", role);
			model.addAttribute("pageTitle", "Edit User ID:" + id);
			return "user_form";
		} catch (UserNotFoundException e) { // e.getMessage()getting error message from UserNotFoundException
											// constructor value
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}

		return "redirect:/users";
	}

	@GetMapping("/users/delete/{id}")
	public String delete(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {

		try {
			userService.deleteUser(Long.valueOf(id));
			redirectAttributes.addFlashAttribute("message", "The user id " + id + " has been deleted successfully");
		} catch (UserNotFoundException e) {

			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/users";
	}

	@GetMapping("/users/{id}/enabled/{enabled}")
	public String updateUserEnabledStatus(@PathVariable("id") Long id, @PathVariable("enabled") boolean enabled,
			RedirectAttributes redirectAttributes) {

		userService.updateStatusEnable(id, enabled);
		String status = enabled ? "enabled" : "disable";
		redirectAttributes.addFlashAttribute("message", "The User id " + id + " has been " + status);

		return "redirect:/users";

	}

	@GetMapping("/users/export/csv")
	public void exportToCsv(HttpServletResponse httpServletResponse) throws IOException {

		List<User> listUsers = userService.listAllUsers();
		UserCsvExporter userCsvExporter = new UserCsvExporter();
		userCsvExporter.export(listUsers, httpServletResponse);
	}

	@GetMapping("/users/export/excel")
	public void exportToExcel(HttpServletResponse httpServletResponse) throws IOException {

		List<User> listUsers = userService.listAllUsers();
		UserExcelExporter userExcelExporter = new UserExcelExporter();
		userExcelExporter.export(listUsers, httpServletResponse);
	}

	@GetMapping("/users/export/pdf")
	public void exportToPdf(HttpServletResponse httpServletResponse) throws IOException {

		List<User> listUsers = userService.listAllUsers();

		UserPdfExporter userPdfExporter = new UserPdfExporter();
		userPdfExporter.export(listUsers, httpServletResponse);
	}
	
	

}
