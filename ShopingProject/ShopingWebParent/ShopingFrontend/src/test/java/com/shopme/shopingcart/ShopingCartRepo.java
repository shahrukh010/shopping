package com.shopme.shopingcart;

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
		
		Customer customerId = entityManager.find(Customer.class, 1);
		Product productId = entityManager.find(Product.class, 1);
		
		CartItem cartItem = new CartItem();
		
		cartItem.setCustomer(customerId);
		cartItem.setProduct(productId);
		cartItem.setQuantity(3);
		
		cartRepo.save(cartItem);
	}
}
