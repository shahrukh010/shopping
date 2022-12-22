package com.shopme.customer;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.State;
import com.shopme.setting.CountryRepository;
import com.shopme.setting.StateRepository;

import net.bytebuddy.utility.RandomString;

@Service
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private StateRepository stateRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<Country> listAllCountries() {

		return countryRepository.findAllByOrderByNameAsc();
	}

	public List<State> listStates() {

		return stateRepository.findAllByOrderByNameAsc();
	}

	public void saveCustomer(Customer customer) {

		customerRepository.save(customer);
	}

	public boolean isEmailUnique(String email) {

		Customer customer = customerRepository.findByEmail(email);

		return customer == null ? true : false;
	}

	public void registerCustomer(Customer customer) {

		encodePassword(customer);
		customer.setEnabled(false);
		customer.setCreatedTime(new Date());
		String randomCode = RandomString.make(64);
		customer.setVerificationCode(randomCode);
		System.out.println("Verification code: " + randomCode);
	}

	private void encodePassword(Customer customer) {

		String encodedPassword = passwordEncoder.encode(customer.getPassword());

		customer.setPassword(encodedPassword);

	}

	public void updateAuthentication(Customer customer, AuthenticationType type) {

		if (customer.getAuthenticationType().equals(type)) {

			customerRepository.updateAuthenticationType(customer.getId(), type);
		}
	}

	public Customer getCustomerByEmail(String email) {

		System.out.println(customerRepository);
		return customerRepository.findByEmail(email);
	}

	public boolean verify(String verificationCode) {

		Customer customer = customerRepository.findByVerificationCode(verificationCode);

		if (customer == null || customer.isEnabled())
			return false;
		else {

			customerRepository.enable(customer.getId());
			return true;
		}
	}

	public void newCustomerAuthLogin(String name, String email, String countryCode, AuthenticationType type) {

		Customer customer = new Customer();
		String[] splitName = name.split(" ");
		String firstName = splitName[0];
		String lastName = splitName[1];
		customer.setFirstName(firstName);
		customer.setLastName(lastName);
		customer.setEmail(email);
		customer.setCreatedTime(new Date());
		customer.setEnabled(true);
		customer.setCountry(countryRepository.findByCode(countryCode));
		customer.setAuthenticationType(type);
		customer.setPassword("");
		customer.setVerificationCode("");
		customer.setState("");
		customer.setPhoneNumber("");
		customer.setAddressLine1("");
		customer.setAddressLine2("");
		customer.setCity("");
		customer.setPostalCode("");
		
		customerRepository.save(customer);
	}
}
