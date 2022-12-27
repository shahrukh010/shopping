package com.shopme.shopingcart;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ShopingCartRepo {

	@Autowired
	private ShopingCartRepository cartRepo;

	@Autowired
	private EntityManager entityManager;

	@Test
	public void addProduct() {

		Customer customerId = entityManager.find(Customer.class, 2);
		Product productId = entityManager.find(Product.class, 130);

		CartItem cartItem = new CartItem();

		cartItem.setCustomer(customerId);
		cartItem.setProduct(productId);
		cartItem.setQuantity(2);

		CartItem cartItem2 = new CartItem();
		cartItem2.setCustomer(customerId);
		cartItem2.setProduct(productId);
		cartItem2.setQuantity(1);

		cartRepo.save(cartItem2);

	}

	@Test
	public void findByCustomer() {

		Customer customerId = entityManager.find(Customer.class, 2);

		List<CartItem> cartItem = cartRepo.findByCustomer(customerId);

		cartItem.forEach(System.out::println);
	}

	@Test
	public void findByCustomerAndProduct() {

		Customer customerId = entityManager.find(Customer.class, 2);
		Product productId = entityManager.find(Product.class, 130);

		CartItem cartItem = this.cartRepo.findByCustomerAndProduct(customerId, productId);

		System.err.println(cartItem);
	}

	@Test
	public void updateQuantity() {

		Integer quantity = 4;
		Customer customerId = entityManager.find(Customer.class, 2);
		Product productId = entityManager.find(Product.class, 130);

		cartRepo.updateQuantity(quantity, customerId.getId(), productId.getId());
	}

	@Test
	public void testDeleteCustomerAndProduct() {

		Integer customerId = 2;
		Integer productId = 130;

		cartRepo.deleteByCustomerAndProduct(customerId, productId);
	}
}

