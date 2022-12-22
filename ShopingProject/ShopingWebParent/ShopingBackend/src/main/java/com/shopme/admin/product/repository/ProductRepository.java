package com.shopme.admin.product.repository;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Product;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {

	@Query("Update  Product p SET p.enabled=?2 WHERE p.id=?1")
	@Modifying
	@Transactional
	public void updateEnableStatus(Integer id, boolean status);

	@Query("SELECT count(*) FROM Product p WHERE p.id=:id")
	public Long countBYId(Integer id);

	@Query("SELECT p FROM Product p WHERE p.name LIKE %?1% OR p.shortName " + "LIKE  %?1%" + "OR p.name LIKE %?1%"
			+ "OR p.fullDescription like %?1%" + 
			"OR p.brand.name LIKE %?1%" + "OR p.categories.name LIKE %?1%")
	public Page<Product> searchAll(String keyword, Pageable pageable);

	@Query("SELECT p FROM Product p where p.categories.id=?1" + " OR  p.categories.allParentIds LIKE %?2%")
	public Page<Product> findAllInCategories(Integer categoriesID, String all_parentID, Pageable pageable);

	@Query("SELECT p FROM Product p where p.categories.id=?1" 
			+ " OR  p.categories.allParentIds LIKE %?2%"
			+ " OR  p.name LIKE %?2%"
			+ " OR p.fullDescription like %?3%" 
			+ "	OR p.brand.name LIKE %?3%" 
			+ "OR p.categories.name LIKE %?3%")
	public Page<Product> searchInCategory(Integer categoryId, String all_parentID, String keyword, Pageable pageable);
}
