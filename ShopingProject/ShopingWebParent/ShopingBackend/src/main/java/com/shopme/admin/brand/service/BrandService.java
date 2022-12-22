package com.shopme.admin.brand.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shopme.admin.brand.BrandRepository;
import com.shopme.admin.categories.repository.CategoriesRepository;
import com.shopme.admin.exception.BrandNotFoundException;
import com.shopme.common.entity.Brand;

@Service
public class BrandService {

	@Autowired
	private BrandRepository brandBepository;
	@Autowired
	private CategoriesRepository categoriesRepository;

	public static final int BRAND_PER_PAGE = 10;

	public Page<Brand> listByPage(int pageNum, String sortField, String sortDir) {

		Pageable pageable = PageRequest.of(pageNum, BRAND_PER_PAGE);
		Page page = brandBepository.findAll(pageable);
		return page;
	}

	public List<Brand> listAll() {

		return (List<Brand>) brandBepository.findAll();
	}
//
//	public List<Categories> listAllCategories() {
//
//		return (List<Categories>) categoriesRepository.findAll();
//	}

	public Brand getById(Integer id) throws BrandNotFoundException {

		Long countId = brandBepository.coutById(id);
		if (countId == null || countId == 0)
			throw new BrandNotFoundException("Brand not found:");

		return brandBepository.findById(id).get();
	}

	public Brand save(Brand brands) {

		return brandBepository.save(brands);

//		List<String> categoriesName = new ArrayList<>();
//		brands.getCategories().stream().forEach(cat -> categoriesName.add(cat.getName()));
//		// debuging
//		System.out.println(categoriesName);
//
//		Set<Categories> catId = new HashSet<>();
//
//		boolean flag = false;
//		Categories categories = null;
//		Categories catObj = null;
//		for (String name : categoriesName) {
//
//			catObj = categoriesRepository.getCategoriesByName(name);
//			categories = new Categories(catObj.getId());
//			catId.add(categories);
//			// debugin
//			// System.out.println(categories.getName()+":::"+categories.getId());
//		}
//		Brand brand = new Brand(brands.getName());
//		// System.out.println(brands.getLogo());
//		brand.setLogo(brands.getLogo());
//		brand.getCategories().addAll(catId);

	}

	public void deleteBrand(Integer id) throws BrandNotFoundException {

		Long countId = brandBepository.coutById(id);
		if (countId == null || countId == 0)

			throw new BrandNotFoundException("brand id not found");

		brandBepository.deleteById(id);
	}

}
