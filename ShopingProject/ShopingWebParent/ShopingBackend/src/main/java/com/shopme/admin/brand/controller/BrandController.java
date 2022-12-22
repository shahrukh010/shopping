package com.shopme.admin.brand.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.shopme.admin.brand.service.BrandService;
import com.shopme.admin.categories.service.CategoriesService;
import com.shopme.admin.exception.BrandNotFoundException;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Categories;

@Controller
public class BrandController {

	@Autowired
	private BrandService service;
	@Autowired
	private CategoriesService categoriesService;

	@GetMapping("/brands")
	public String listByFirstPage(@Param("sortField") String sortField, @Param("sortDir") String sortDir, Model model,
			RedirectAttributes redirectAttributes) {

		return listByPage(1, model);
	}

	@GetMapping("/brands/page/{pageNum}")
	public String listByPage(@PathVariable("pageNum") Integer pageNum, Model model) {

//		if (sortDir == null || sortDir.isEmpty())
//			sortDir = "asc";
//		else
//			sortDir = "dsc";

		System.out.println(pageNum);

		Page page = service.listByPage(pageNum, null, null);
		List<Brand> listBrands = page.getContent();

//		System.out.println(sortField + "----");

		int startCount = (pageNum - 1) * service.BRAND_PER_PAGE + 1;
		int endCount = (startCount) + service.BRAND_PER_PAGE - 1;

//		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

		model.addAttribute("currentPage", pageNum);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
//		model.addAttribute("sortField", sortField);
//		model.addAttribute("sortDir", sortDir);
//		model.addAttribute("keyword", keyword);
//		model.addAttribute("reverseSortDir", reverseSortDir);
//		model.addAttribute("listCategories", listCategories);
		model.addAttribute("listBrands", listBrands);
		return "brands/brands";

	}

	@GetMapping("/brands/new")
	public String createNewBrand(Model model, RedirectAttributes redirectAttribute) {

		List<Brand> brand = service.listAll();
		List<Categories> listCategories = categoriesService.listCategoriesUsedInForm();
		model.addAttribute("brand", new Brand());
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("pageTitle", "Create Brand");
		return "brands/brand_form";
	}

	@GetMapping("/brands/edit/{id}")
	public String editBrand(Model model, @PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {

		try {

			Brand brand = service.getById(id);
			List<Categories> listCategories = categoriesService.listCategoriesUsedInForm();
			model.addAttribute("brand", brand);
			model.addAttribute("listCategories", listCategories);
			model.addAttribute("pageTitle", "Edit User ID:" + id);
			return "brands/brand_form";

		} catch (Exception ex) {

			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:brands";
		}
	}

	@GetMapping("/brands/delete/{id}")
	public String deleteBrand(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {

		try {
			service.deleteBrand(id);
			redirectAttributes.addFlashAttribute("message", "The brand id " + id + " has been deleted successfully");
		} catch (BrandNotFoundException ex) {

			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/brands";
	}

	@PostMapping("/brands/save")
	public String saveBrand(Brand brand, @RequestParam("fileImage") MultipartFile multipartFile,
			RedirectAttributes redirectAttribute) throws IOException {

		if (!multipartFile.isEmpty()) {

			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename().toLowerCase());
			brand.setLogo(fileName);
			Brand saveBrand = service.save(brand);

			String uploadDir = "../product-images/brand-image/" + saveBrand.getId();

			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			service.save(brand);
		}

//		System.out.println(brand.getName()); debuging
//		service.save(brand);
		redirectAttribute.addFlashAttribute("message", "The brand has been save successfully.");
		return "redirect:/brands";
	}
}
