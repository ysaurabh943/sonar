package com.kart.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kart.order.entity.Productsorder;

public interface  ProductsOrderRepo extends JpaRepository<Productsorder, String>{
	
	List<Productsorder> findByProdid(String prodid);

	Iterable<Productsorder> findByBuyerid(String buyerid);
	
	}

