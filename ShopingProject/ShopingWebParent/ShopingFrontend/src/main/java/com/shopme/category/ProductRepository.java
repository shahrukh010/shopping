package com.shopme.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.shopme.common.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	@Query("SELECT p From Product p where p.enabled=true AND p.categories.id = ?1 OR p.categories.allParentIds LIKE %?2% ORDER BY p.name ASC")
	public Page<Product> ListByCategory(Integer categoryId, String categoryMatch, Pageable pageable);

	public Product findByAlias(String alias);
}
