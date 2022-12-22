package com.shopme.admin.categories.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.categories.exception.CategoriesNotFoundException;
import com.shopme.admin.categories.service.CategoriesPageInfo;
import com.shopme.admin.categories.service.CategoriesService;
import com.shopme.common.entity.Categories;

@Controller
public class CategoriesController {

	@Autowired
	private CategoriesService service;

	@GetMapping("/categories")
	public String listFirstPage(@Param("sortField") String sortField, @Param("sortDir") String sortDir,
			@Param("keyboard") String keyword, Model model, RedirectAttributes redirectAttribute) {

//		List<Categories> listCategories = service.listAll();
//		model.addAttribute("listCategories", listCategories);
//		Page<Categories> page = service.listByPage(1);
//		model.addAttribute("listCategories", page.getContent());
//
//		return "categories/categories";

		// refactor
		return listByPage(1, "name", sortDir, keyword, model, redirectAttribute);
	}

	@GetMapping("/categories/page/{pageNum}")
	private String listByPage(@PathVariable("pageNum") Integer pageNum, @Param("sortField") String sortField,
			@Param("sortDir") String sortDir, String keyword, Model model, RedirectAttributes redirectAttribute) {

		if (sortDir == null || sortDir.isEmpty())
			sortDir = "asc";
//		else
//			sortDir = "dsc";

		CategoriesPageInfo pageInfo = new CategoriesPageInfo();
		List<Categories> listCategories = service.listByPage(pageInfo, pageNum, sortDir, keyword);
		//debug
		System.out.println(sortField + "----");

		int startCount = (pageNum - 1) * service.CATEGORY_PER_PAGE + 1;
		int endCount = (startCount) + service.CATEGORY_PER_PAGE - 1;

		if (endCount > pageInfo.getNoOfElement())
			endCount = (int) pageInfo.getNoOfElement();

		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

		model.addAttribute("currentPage", pageNum);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", pageInfo.getNoOfElement());
		model.addAttribute("totalPages", pageInfo.getTotalNumberoFPage());
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("listCategories", listCategories);
		return "categories/categories";
	}

	@GetMapping("/categories/new")
	public String newCategories(Model model) {

		List<Categories> listCategories = service.listCategoriesUsedInForm();
		model.addAttribute("category", new Categories());
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("pageTitle", "Create New Category");

		return "/categories/category_form";
	}

	@PostMapping("/categories/save")
	public String saveCategories(Categories categories, @RequestParam("fileImage") MultipartFile multipartFile,
			RedirectAttributes redirectAttributes) throws IOException {

		if (!multipartFile.isEmpty()) {

			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			categories.setImages(fileName);

			Categories saveCategories = service.save(categories);
			String uploadDir = "../category-images/" + saveCategories.getId();
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

		} else {
			service.save(categories);
		}

		redirectAttributes.addFlashAttribute("message", "The categories has been save successfully.");

		return "redirect:/categories";
	}

	@GetMapping("/categories/edit/{id}")
	public String editCategory(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {

		try {

			Categories categories = service.getById(id);
			List<Categories> listCategories = service.listCategoriesUsedInForm();
			model.addAttribute("category", categories);
			model.addAttribute("listCategories", listCategories);
			model.addAttribute("pageTitle", "Edit User ID:" + id);
			return "/categories/category_form";
		} catch (CategoriesNotFoundException ex) {

			redirectAttributes.addFlashAttribute("message", ex.getMessage());

			return "redirect:/categories";
		}

	}

	@GetMapping("/categories/{id}/enabled/{enabled}")
	public String updateUserEnabledStatus(@PathVariable("id") Integer id, @PathVariable("enabled") boolean enabled,
			RedirectAttributes redirectAttributes) {

		service.updateStatusEnable(id, enabled);
		String status = enabled ? "enabled" : "disable";
		redirectAttributes.addFlashAttribute("message", "The Categories id " + id + " has been " + status);

		return "redirect:/categories";

	}

}
