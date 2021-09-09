package com.kart.order.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kart.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, String> {
	 Order findByOrderid(String orderid);
	 
	@Query("FROM Order as order " + " WHERE order.buyerid = :buyerid and order.address = :address")   
	public Order getOrderByBuyeridAndAddress(@Param("buyerid") String buyerId,@Param("address") String address);

	
}
