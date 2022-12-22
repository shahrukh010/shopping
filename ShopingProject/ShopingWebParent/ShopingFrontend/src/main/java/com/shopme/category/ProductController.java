package com.shopme.category;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.common.entity.Categories;
import com.shopme.common.entity.Product;
import com.shopme.common.exception.ProductNotFoundException;

@Controller
public class ProductController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@GetMapping("/c/{category_alias}")
	public String viewCategoryFirstPage(@PathVariable("category_alias") String alias, Model model) {

		return viewCategoryByPage(alias, 1, "name", "asc", model);
	}

	@GetMapping("/c/{category_alias}/page/{pageNum}")
	public String viewCategoryByPage(@PathVariable("category_alias") String alias, @PathVariable("pageNum") int pageNum,
			@Param("sortField") String sortField, @Param("sortDir") String sortDir, Model model) {

		Categories categories = categoryService.getCategory(alias);
		if (categories == null)
			return "error/404";

		List<Categories> listCategoryParents = categoryService.getCategoryParent(categories);

		Page<Product> pageProducts = productService.listByCategory(pageNum, categories.getId());
		List<Product> listProduct = pageProducts.getContent();

		int startCount = (pageNum - 1) * productService.PRODUCT_PER_PAGE + 1;
		int endCount = (startCount) + productService.PRODUCT_PER_PAGE - 1;

		System.out.println("totalpage" + pageProducts.getTotalPages());

		if (endCount > pageProducts.getTotalElements()) {

			endCount = (int) pageProducts.getTotalElements();
		}

		model.addAttribute("currentPage", pageNum);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", pageProducts.getTotalElements());
		model.addAttribute("totalPages", pageProducts.getTotalPages());
		model.addAttribute("listProducts", listProduct);
		model.addAttribute("listCategoryParents", listCategoryParents);
		model.addAttribute("category", categories);
		return "/product/products_by_category";
	}

	@GetMapping("/p/{product_alias}")
	public String viewProductDetail(@PathVariable("product_alias") String alias, RedirectAttributes redirectAttribute,
			Model model) {

		try {
			Product product = productService.getProduct(alias);

			List<Categories> listCategories = categoryService.getCategoryParent(product.getCategories());
			model.addAttribute("product", product);
			model.addAttribute("listCategories", listCategories);
			return "/product/product_detail";

		} catch (ProductNotFoundException ex) {

			return "error/404";
		}
	}

}
