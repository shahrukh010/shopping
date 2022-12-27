package com.shopme.shopingcart;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;

@Repository
public interface ShopingCartRepository extends CrudRepository<CartItem, Integer> {

	/*
	 * spring data jpa automatically build sql query not required to write join for
	 * this kind operation.
	 */
	public List<CartItem> findByCustomer(Customer customer);

	/*
	 * spring data jpa automatically build sql query not required to write join or
	 * other query for this kind operation.
	 */
	public CartItem findByCustomerAndProduct(Customer customer, Product product);

	/*
	 * @Modifying annotation because of it is update query
	 * 
	 * @Query annotation used if we write a custom query c->c is alias of
	 * CartItem(class) quantity belong to CartItem,id->Customer,id->Product ?1?2?3
	 * all these are position of argument which is present in updateQuantity
	 */
	@Modifying
	@Query("UPDATE CartItem c SET c.quantity = ?1 WHERE c.customer.id=?2 AND c.product.id=?3")
	public void updateQuantity(Integer quantity, Integer customerId, Integer productId);

	@Modifying
	@Query("DELETE FROM CartItem c WHERE c.customer.id = ?1 AND c.product.id = ?2")
	public void deleteByCustomerAndProduct(Integer customerId, Integer productId);

}
