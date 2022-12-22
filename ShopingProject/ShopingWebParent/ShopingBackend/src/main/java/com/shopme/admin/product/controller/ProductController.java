package com.shopme.admin.product.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.shopme.admin.product.service.ProductService;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Categories;
import com.shopme.common.entity.Product;
import com.shopme.common.exception.ProductNotFoundException;

@Controller
public class ProductController {

	@Autowired
	private ProductService service;
	@Autowired
	private BrandService brandService;
	@Autowired
	private CategoriesService categoriesService;

	@GetMapping("/products")
	public String listByFirstPage(Model model, RedirectAttributes redirectAttributes, String keyword) {

		return listByPage(1, "name", "asc", 0, model, null);
	}

	@GetMapping("/products/page/{pageNum}")
	public String listByPage(@PathVariable("pageNum") Integer pageNum, @Param("sortField") String sortField,
			@Param("sortDir") String sortDir, @Param("categoryId") Integer categoryId, Model model, String keyword) {

		// for debug
//		System.out.println(sortField + "," + sortDir);
		System.out.println("The cateogory id is:" + categoryId);

		Page page = service.listByPage(pageNum, sortField, sortDir, keyword, categoryId);
		List<Product> listProducts = page.getContent();
		List<Categories> listCategories = categoriesService.listCategoriesUsedInForm();

		int startCount = (pageNum - 1) * service.PRODUCT_PER_PAGE + 1;
		int endCount = (startCount) + service.PRODUCT_PER_PAGE - 1;

		System.out.println("totalpage" + page.getTotalPages());

		if (categoryId != null && categoryId > 0) {
			model.addAttribute("categoryId", categoryId);
		}
		System.out.println(listCategories);

		model.addAttribute("currentPage", pageNum);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("moduleURL", "/products");
//		String url = "https://cdn.thewirecutter.com/wp-content/uploads/2020/04/laptops-lowres-2x1-.jpg?auto=webp&quality=75&crop=2:1&width=980&dpr=2%22";
//		model.addAttribute("URL", url);//render absolute url
		return "products/products";
	}

	@GetMapping("/products/{id}/enabled/{enabled}")
	public String updateStatusEnabled(@PathVariable("id") Integer id, @PathVariable("enabled") boolean enabled,
			RedirectAttributes redirectAttributes) {

		service.updateStatusEnabled(id, enabled);
		String status = enabled ? "enabled" : "disable";

		redirectAttributes.addFlashAttribute("message", "The Product id " + id + " has been " + status);

		return "redirect:/products";
	}

	@PostMapping("/products/save")
	public String saveProudct(Product product, Model model, @RequestParam("fileImage") MultipartFile multipartFile,
			@RequestParam("extraImage") MultipartFile[] extraImageMultiparts, RedirectAttributes redirectAttribute,
			@RequestParam(name = "detailNames", required = false) String[] detailNames,
			@RequestParam(name = "detailValues", required = false) String[] detailValues) throws IOException {

		setMainImageName(multipartFile, product);
		setExtraImageName(extraImageMultiparts, product);
		setProductDetail(detailNames, detailValues, product);

		Product saveProduct = service.save(product);

		savedUploadImages(multipartFile, extraImageMultiparts, saveProduct);
		redirectAttribute.addFlashAttribute("message", "The Product has been save successfully.");
		return "redirect:/products";

	}

	private void setProductDetail(String[] detailNames, String[] detailValues, Product product) {

		if (detailNames == null || detailValues.length == 0)
			return;

		for (int index = 0; index < detailNames.length; index++) {

			String name = detailNames[index];
			String value = detailValues[index];
			product.addDetail(name, value);
		}

	}

	private void savedUploadImages(MultipartFile mainImageMultipart, MultipartFile[] extraImageMultipart,
			Product savedProduct) throws IOException {

		if (!mainImageMultipart.isEmpty()) {

			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			String uploadDir = "../product-images/" + savedProduct.getId();
			FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
		}

		if (extraImageMultipart.length > 0) {
			String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";

			for (MultipartFile multipartFile : extraImageMultipart) {

				if (multipartFile.isEmpty())
					continue;

				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
				FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

			}
		}
	}

	private void setExtraImageName(MultipartFile[] extraImageMultipart, Product product) {

		if (extraImageMultipart.length > 0) {

			for (MultipartFile multipart : extraImageMultipart) {

				if (!multipart.isEmpty()) {

					String fileName = StringUtils.cleanPath(multipart.getOriginalFilename());
					product.addExtraImage(fileName);
				}

			}
		}
	}

	private void setMainImageName(MultipartFile multipartFile, Product product) {

		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			product.setMainImage(fileName);
		}
	}

	@GetMapping("/products/new")
	public String addProduct(Model model, RedirectAttributes redirectAttributes) {

		List<Brand> listBrands = brandService.listAll();
		model.addAttribute("product", new Product());
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("numberOfExistingExtraImages", 0);
		model.addAttribute("pageTitle", "Create New Product ");
		return "products/product_form";
	}

	@GetMapping("/products/edit/{id}")
	public String editProduct(@PathVariable("id") Integer id, Model model, RedirectAttributes ra,
			@AuthenticationPrincipal ShopmeUserDetails loggedUser) throws BrandNotFoundException {

		try {
			Product product = service.listById(id);
			List<Brand> listBrands = brandService.listAll();
			Integer numberOfExistingExtraImages = product.getImages().size();

			boolean isReadOnlyForSalesperson = false;

			model.addAttribute("product", product);
			model.addAttribute("listBrands", listBrands);
			model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
			model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);

			return "products/product_form";

		} catch (ProductNotFoundException e) {
			ra.addFlashAttribute("message", e.getMessage());
		}

		return "products/product_form";
//		}

	}

	@GetMapping("/products/detail/{id}")
	public String viewProductDetails(@PathVariable("id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {

		try {
			Product product = service.listById(id);
			model.addAttribute("product", product);

			return "products/product_detail_modal.html";
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("The product id does not exists");
			return "redirect:/products";
		}

	}

	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {

		try {

			service.countById(id);
			redirectAttributes.addFlashAttribute("message", "The Product Id " + id + " is deleted successfully");
			return "redirect:/products";
		} catch (ProductNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/products";
		}
	}

}
