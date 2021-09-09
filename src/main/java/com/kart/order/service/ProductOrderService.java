package com.kart.order.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kart.order.dto.ProductsorderedDTO;
import com.kart.order.entity.Productsorder;
import com.kart.order.exception.KartException;
import com.kart.order.repository.ProductsOrderRepo;

@Service
@Transactional
public class ProductOrderService {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ProductsOrderRepo orderproRepo;

	public List<ProductsorderedDTO> getProductById(String buyerid) throws KartException{
		logger.info("Productname request for product {}", buyerid);
		Iterable<Productsorder> proord = orderproRepo.findByBuyerid(buyerid);
		List<ProductsorderedDTO> proorderDTO = new ArrayList<ProductsorderedDTO>();
		proord.forEach(pord -> {
			proorderDTO.add(ProductsorderedDTO.valueOf(pord));
		});
		
		logger.info("Productname for product : {}", proord);
		return proorderDTO;
	}
}
