package com.shopme.admin.product.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.admin.product.repository.ProductRepository;
import com.shopme.common.entity.Product;
import com.shopme.common.exception.ProductNotFoundException;

@Service
public class ProductService {

	public static final int PRODUCT_PER_PAGE = 10;
	@Autowired
	private ProductRepository productRepository;

	public Page<Product> listByPage(Integer pageNum, String sortField, String sortDir, String keyword,
			Integer categoryId) {

		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		// make sure Pageable package importing correct you spend 30min for fix 😋
		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCT_PER_PAGE, sort);
		System.out.println("The categoryId: " + categoryId);
		
		if (keyword != null && !keyword.isEmpty()) {

			if (categoryId != null && categoryId > 0) {

				String categoryMatch = "-" + String.valueOf(categoryId) + "-";
				Page<Product> page = productRepository.searchInCategory(categoryId, categoryMatch, keyword, pageable);
				return page;
			}
		}

		if (categoryId != null && categoryId > 0) {

			// for product top parent id matching.
			String categoryMatch = "-" + String.valueOf(categoryId) + "-";
			Page<Product> page = productRepository.findAllInCategories(categoryId, categoryMatch, pageable);
			return page;
			// debuging
//			page.getContent().stream().forEach(name -> System.out.print(name));
		}
		if (keyword != null) {

			return productRepository.searchAll(keyword, pageable);
		}

		Page<Product> page = productRepository.findAll(pageable);
		return page;

	}

	public Product listById(Integer id) throws ProductNotFoundException {

		Product product = productRepository.findById(id).get();

		if (product == null)
			throw new ProductNotFoundException("THe product is not found");

		return productRepository.findById(id).get();
	}

	public void countById(Integer id) throws ProductNotFoundException {

		Long countId = productRepository.countBYId(id);
		if (countId == null || countId == 0)
			throw new ProductNotFoundException("The product is not found");

		productRepository.deleteById(id);
	}

	public Product save(Product product) {

		if (product.getId() == null) {
			product.setCreatedTime(new Date());
			product.setUpdatedTime(new Date());
		}

		return productRepository.save(product);
	}

	public List<Product> listAllProduct() {

		return (List<Product>) productRepository.findAll();
	}

	public void updateStatusEnabled(Integer id, boolean status) {

		productRepository.updateEnableStatus(id, status);
	}
}
