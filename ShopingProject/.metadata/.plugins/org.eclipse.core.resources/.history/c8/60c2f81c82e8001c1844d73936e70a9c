package com.shopme.admin;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.repository.UserRepository;
import com.shopme.common.entity.User;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Test
	public void testPageing() {

		int noOfPage = 2;// page
		int pageSize = 4;// element

		Pageable pageable = PageRequest.of(noOfPage, pageSize);
		Page<User> page = userRepository.findAll(pageable);
		List<User> listUsers = page.getContent();

		listUsers.forEach(usr -> System.out.println(usr));
		assertThat(listUsers.size()).isEqualTo(pageSize);

	}
	
	@Test
	public void testSearch() {
		
		String keyword = "S";
		int noOfPage = 0;
		int pageSize = 4;
		Pageable pageable = PageRequest.of(noOfPage, pageSize);
		
		Page<User>users = userRepository.searchAll(keyword, pageable);
		List<User>listUsers = users.getContent();
		
		listUsers.forEach(user->System.out.println(user.getFirstName()));
		assertThat(listUsers.size()).isGreaterThan(0);
		
	}

}
