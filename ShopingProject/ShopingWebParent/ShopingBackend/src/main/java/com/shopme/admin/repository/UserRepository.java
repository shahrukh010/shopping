package com.shopme.admin.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

	@Query("SELECT u FROM User u Where u.email=:email")
	public User getUserByEmail(@Param("email") String email);

	@Query("SELECT count(*) FROM User WHERE id = :id")
	Long countById(Long id);

	@Query("update User u SET u.enabled=?2 WHERE u.id=?1")
	@Modifying
	public void updateEnabledStatus(Long id, boolean enabled);

	@Query("SELECT u FROM User u WHERE u.firstName LIKE %?1% OR u.lastName LIKE  %?1%")
	public Page<User> searchAll(String keyword, Pageable pageable);


}
