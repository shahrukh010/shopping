package com.shopme.admin.customer;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.shopme.common.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

	@Query("SELECT c FROM Customer c WHERE CONCAT(c.email,' ',c.firstName,' ',c.lastName,' ',c.addressLine1,' ',c.addressLine2,' ',c.state,' ',c.postalCode,' ',c.country.name) LIKE %?1%")
	public Page<Customer> findAll(String keyword, Pageable pageable);

	@Query("UPDATE  Customer c SET c.enabled=?2 WHERE c.id=?1")
	@Modifying
	@Transactional
	public void updateEnableStatus(Integer id, boolean status);

	@Query("SELECT c FROM Customer c where c.email=?1")
	public Customer findByEmail(String email);

	public Integer countById(Integer id);
}
