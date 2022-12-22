package com.shopme.admin.service;

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

import com.shopme.admin.exception.UserNotFoundException;
import com.shopme.admin.repository.RoleRepository;
import com.shopme.admin.repository.UserRepository;
import com.shopme.admin.security.WebSecurityConfigure;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Service
@Transactional
public class UserService {

	public final int USER_PER_PAGE = 4;
	@Autowired
	private UserRepository repository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public User getByEmail(String email) {

		return repository.getUserByEmail(email);
	}

	public List<User> listAllUsers() {

		List<User> users = (List<User>) repository.findAll(Sort.by("firstName").ascending());
		return users;
	}

	public List<Role> listRoles() {

		return (List<Role>) roleRepository.findAll();
	}

	public Page<User> listByPage(int pageNum, String sortField, String sortDir,String keyword) {

		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		// make sure Pageable package importing correct you spend 30min for fix  😋
		Pageable pageable = PageRequest.of(pageNum - 1, USER_PER_PAGE, sort);

		if (keyword != null) {

			return repository.searchAll(keyword, pageable);
		}
		Page<User> page = repository.findAll(pageable);
		return page;
	}

	public User saveUser(User user) {

		boolean isUpdateUser = (user.getId() != null);
		if (isUpdateUser) {

			User existingUsers = repository.findById(user.getId()).get();
			System.out.println(existingUsers);
			if (user.getPassword().isEmpty()) {
//				System.out.println("password empty"); 🤣 
				user.setPassword(existingUsers.getPassword());
			} else {
//				System.out.println("password not empty");
				passwordEncoder(user);
			}

		} else
			passwordEncoder(user);

		return repository.save(user);

	}

	public User updateAccount(User userInForm) {

		User userInDb = repository.findById(userInForm.getId()).get();

		if (!userInForm.getPassword().isEmpty()) {

			userInDb.setPassword(userInForm.getPassword());
			passwordEncoder(userInDb);
		}

		if (userInForm.getPhotos() != null) {

			userInDb.setPhotos(userInForm.getPhotos());
		}

		userInDb.setFirstName(userInForm.getFirstName());
		userInDb.setLastName(userInForm.getLastName());

		return repository.save(userInDb);
	}

	private void passwordEncoder(User user) {

		String encoded = passwordEncoder.encode(user.getPassword());
		user.setPassword(encoded);
	}

	public boolean isEmailUnique(Integer id, String email) {

		User user = repository.getUserByEmail(email);

//		if (user == null)
//			return true;
//		boolean isCreateNewUser = (id == null);
//		System.out.println(isCreateNewUser);
//
//		if (isCreateNewUser) {
//
//			if (user.getEmail() != null)
//				return false;
//		} else {
//			if (user.getId() != null)
//				return true;
//		}
//		return true;


//		 😇 😇 😇 😇 😇 😇 😇 😇 😇 😇 😇 😇 😇 😇 😇 😇 😇 😇
		return user == null ? true : id == null && user.getEmail() != null ? false : user.getId() != null ? true : true;

	}

	public User getById(long id) throws UserNotFoundException {

		try {

			return repository.findById(id).get();
		} catch (NoSuchElementException ex) {

			throw new UserNotFoundException("Could not find any user with id:" + id);
		}

	}

	public void deleteUser(Long id) throws UserNotFoundException {

		Long countId = repository.countById(id);
		if (countId == null || countId == 0) {
			throw new UserNotFoundException("Count not find any user with id" + id);
		}
		repository.deleteById(id);
	}

	public void updateStatusEnable(Long id, boolean status) {

		repository.updateEnabledStatus(id, status);
	}

}
