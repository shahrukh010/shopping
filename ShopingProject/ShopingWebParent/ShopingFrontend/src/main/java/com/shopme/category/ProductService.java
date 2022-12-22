package com.shopme.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Product;
import com.shopme.common.exception.ProductNotFoundException;

@Service
public class ProductService {

	public static final int PRODUCT_PER_PAGE = 10;

	@Autowired
	private ProductRepository productRepository;

	public Page<Product> listByCategory(int pageNum, Integer categoryId) {

		String categoryMatch = "-" + String.valueOf(categoryId) + "-";

		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCT_PER_PAGE);

		return productRepository.ListByCategory(categoryId, categoryMatch, pageable);
	}

	public Product getProduct(String alias) throws ProductNotFoundException {

		Product product = productRepository.findByAlias(alias);

		if (product == null)
			throw new ProductNotFoundException("could not found product by alias:" + alias);

		return product;
	}

}
