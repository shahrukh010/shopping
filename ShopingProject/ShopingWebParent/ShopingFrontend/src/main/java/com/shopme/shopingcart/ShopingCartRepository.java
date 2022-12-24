package com.shopme.shopingcart;

import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.CartItem;

public interface ShopingCartRepository extends CrudRepository<CartItem, Integer>{

}
