package com.shopme.category;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.shopme.common.entity.Categories;

public interface CategoryRepository extends JpaRepository<Categories, Integer> {

	@Query("SELECT c FROM Categories c where c.enabled=true ORDER BY c.name ASC")
	List<Categories> findEnabled();

	@Query("SELECT c FROM Categories c where c.enabled=true AND c.alias=?1")
	Categories FindByAlias(String alias);
}
