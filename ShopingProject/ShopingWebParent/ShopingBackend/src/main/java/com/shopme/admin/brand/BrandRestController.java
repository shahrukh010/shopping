package com.shopme.admin.brand;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.admin.brand.service.BrandService;
import com.shopme.admin.exception.BrandNotFoundException;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Categories;

@RestController // restController direct calling from ui/user request in my case using agex call
public class BrandRestController {

	@Autowired
	private BrandService service;

	@GetMapping("/brands/{id}/categories")
	public List<CategoriesDTO> listCategoriesByBrand(@PathVariable("id") Integer id) throws BrandNotFoundException {

		List<CategoriesDTO> listCategoriesDTO = new ArrayList<>();
		try {
			Brand brand = service.getById(id);
			Set<Categories> categories = brand.getCategories();
			for (Categories category : categories) {

				CategoriesDTO dto = new CategoriesDTO(category.getId(), category.getName());
				listCategoriesDTO.add(dto);
			}
			System.out.println(listCategoriesDTO.get(0).getName());
			return listCategoriesDTO;

		} catch (BrandNotFoundException ex) {

			throw new BrandNotFoundException(ex.getMessage());

		}
	}
}
