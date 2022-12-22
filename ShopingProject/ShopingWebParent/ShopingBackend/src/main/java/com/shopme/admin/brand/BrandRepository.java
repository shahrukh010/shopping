package com.shopme.admin.brand;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Brand;

public interface BrandRepository extends PagingAndSortingRepository<Brand, Integer> {

	@Query("SELECT count(*) FROM Brand WHERE id = :id")
	public long coutById(Integer id);

	@Query("SELECT new Brand(b.id,b.name) FROM Brand b ORDER BY b.name ASC")
	public List<Brand> findAll();

}
