package com.shopme.admin.customer;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.admin.exception.CustomerNotFoundException;
import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;

@Service
@Transactional
public class CustomerService {

	public static final int CUSTOMER_PER_PAGE = 100;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private CountryRepository countryRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	public Page<Customer> listByPage(int pageNum, String sortField, String sortDir, String keyword) {

		Sort sort = Sort.by(sortField);

		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(pageNum - 1, CUSTOMER_PER_PAGE, sort);

		if (keyword != null) {

			return customerRepository.findAll(keyword, pageable);
		}

		return customerRepository.findAll(pageable);
	}

	public void updateCustomerEnableStatus(Integer id, boolean status) {

		customerRepository.updateEnableStatus(id, status);
	}

	public Customer get(Integer id) throws CustomerNotFoundException {

		try {

			return customerRepository.findById(id).get();
		} catch (NoSuchElementException ex) {

			throw new CustomerNotFoundException("Could not find any customer with ID:" + id);
		}

	}

	public List<Country> listCountry() {

		return countryRepository.findAllByOrderByNameAsc();
	}

	public boolean isEmailUnique(Integer id, String email) {

		Customer existCustomer = customerRepository.findByEmail(email);

		if (existCustomer != null || existCustomer.getId() != id) {

			// found another customer having mail
			return false;
		}

		return true;

	}

	public void save(Customer customerInForm) {

		if (!customerInForm.getPassword().isEmpty()) {

			String encode = passwordEncoder.encode(customerInForm.getPassword());

			customerInForm.setPassword(encode);

		} else {

			Customer customerInDB = customerRepository.findById(customerInForm.getId()).get();
			customerInForm.setPassword(customerInDB.getPassword());
		}
		customerRepository.save(customerInForm);
	}

	public void delete(Integer id) throws CustomerNotFoundException {

		Integer count = customerRepository.countById(id);
		if (count == null || count == 0) {

			throw new CustomerNotFoundException("Could not found customer ID:" + id);
		}
		customerRepository.deleteById(id);

	}

}
