package com.shopme.shopingcart;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;

public interface ShopingCartRepository extends CrudRepository<CartItem, Integer>{

	
	
	public List<CartItem>findByCustomer(Customer customer);
}
