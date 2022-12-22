package com.shopme.admin.categories.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Categories;

@Repository
public interface CategoriesRepository extends PagingAndSortingRepository<Categories, Integer> {

	@Query("Select c from Categories c where c.parent is null")
	public List<Categories> findRootCategories(Sort sort);

	@Query("Select c from Categories c where c.parent is null")
	public Page<Categories> findRootCategories(Pageable pageable);

	@Query("Select c from Categories c where c.name=:name")
	public Categories getCategoriesByName(@Param("name") String name);

	@Query(

			value = "SELECT u.name FROM categories u WHERE u.name LIKE BINARY ?1",

			nativeQuery = true)
	public Page<Categories> search(String keyword, Pageable pageable);

	@Query("Update Categories c SET c.enabled=?2 WHERE c.id=?1")
	@Modifying
	@Transactional
	public void updateEnabledStatus(Integer id, boolean status);

}
