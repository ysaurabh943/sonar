package com.kart.order.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kart.order.dto.CartDTO;
import com.kart.order.dto.OrderDTO;
import com.kart.order.dto.OrderPlacedDTO;
import com.kart.order.dto.ProductDTO;
import com.kart.order.entity.Order;
import com.kart.order.entity.Productsorder;
import com.kart.order.exception.KartException;
import com.kart.order.repository.OrderRepository;
import com.kart.order.repository.ProductsOrderRepo;

@Service
@Transactional
public class OrderService {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	OrderRepository orderrepo;

	@Autowired
	ProductsOrderRepo prodRepo;


	// Get specific order by id
	public OrderDTO getSpecificOrder(String orderid) throws KartException {
		Optional<Order> ord = orderrepo.findById(orderid);
		Order ord1 = ord.orElseThrow(() -> new KartException("Service.ORDERS_NOT_FOUND"));
		logger.info("Order details of Id {}", orderid);
		OrderDTO orderDTO = OrderDTO.valueOf(ord1);
		logger.info("{}", orderDTO);
		return orderDTO;
	}

	// Get all order details
	public List<OrderDTO> getAllOrder() throws KartException {

		Iterable<Order> orders = orderrepo.findAll();
		List<OrderDTO> orderDTOs = new ArrayList<>();

		orders.forEach(order -> {
			OrderDTO orderDTO = OrderDTO.valueOf(order);
			orderDTOs.add(orderDTO);
		});
		if (orderDTOs.isEmpty())
			throw new KartException("Service.ORDERS_NOT_FOUND");
		logger.info("Order Details : {}", orderDTOs);
		return orderDTOs;
	}


	// Place order
	public String saveOrder(OrderDTO orderDTO) throws KartException {

		Order order = orderrepo.getOrderByBuyeridAndAddress(orderDTO.getBuyerid(), orderDTO.getAddress());
		if (order != null) {
			return order.getOrderid();
		} else {
			throw new KartException("Services.ORDER_NOT_PLACED");
		}

	}


	// Delete order
	public void deleteOrder(String orderid) throws KartException {
		Optional<Order> ord = orderrepo.findById(orderid);
		ord.orElseThrow(() -> new KartException("Service.ORDERS_NOT_FOUND"));
		orderrepo.deleteById(orderid);
	}
	

    //Place order
	public OrderPlacedDTO placeOrder(List<ProductDTO> productList, List<CartDTO> cartList, OrderDTO orderDTO)
			throws KartException {
		List<Productsorder> productsOrdered = new ArrayList<>();
		orderDTO.setOrderdate(new Date());
		orderDTO.setAmount(0d);
		for(int i = 0; i< cartList.size();i++) {		
			Double amount = orderDTO.getAmount() + (cartList.get(i).getQuantity() * productList.get(i).getPrice());
			orderDTO.setAmount(amount);
			Productsorder prod = new Productsorder();
			prod.setProdid(productList.get(i).getProdid());
			prod.setBuyerid(orderDTO.getBuyerid());
			prod.setSellerid(productList.get(i).getSellerid());
			prod.setQuantity(cartList.get(i).getQuantity());
			productsOrdered.add(prod);		
		}		
		
		prodRepo.saveAll(productsOrdered);
		Order ord = orderDTO.createEntity();
		Order or = orderrepo.save(ord);
		OrderPlacedDTO orderPlaced = new OrderPlacedDTO();
		orderPlaced.setBuyerId(or.getBuyerid());
		orderPlaced.setOrderId(or.getOrderid());
		Integer rewardPts = (int) (or.getAmount()/100);		
		orderPlaced.setRewardPoints(rewardPts);

		return orderPlaced;

	}
	
	//Reorder
	public String reOrder(String orderId) throws KartException {
		Optional<Order> optional = orderrepo.findById(orderId);
		Order order = optional.orElseThrow(()->new KartException("Order does not exist for the given buyer"));
		Order reorder = new Order();
		reorder.setBuyerid(order.getBuyerid());
		reorder.setAmount(order.getAmount());
		reorder.setAddress(order.getAddress());
		reorder.setOrderdate(new Date());
		reorder.setStatus(order.getStatus());
		orderrepo.save(reorder);		
		return reorder.getOrderid();
	}

	public String changeStatus(String orderid, String status) throws KartException {
		Optional<Order> optional = orderrepo.findById(orderid);
		Order order = optional.orElseThrow(()->new KartException("Order does not exist"));
		order.setStatus(status);
		return "Status Changed";
	}
}