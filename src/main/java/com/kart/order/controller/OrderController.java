package com.kart.order.controller;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kart.order.dto.CartDTO;
import com.kart.order.dto.OrderDTO;
import com.kart.order.dto.OrderPlacedDTO;
import com.kart.order.dto.ProductDTO;
import com.kart.order.dto.ProductsorderedDTO;
import com.kart.order.exception.KartException;
import com.kart.order.service.OrderService;
import com.kart.order.service.ProductOrderService;


@RestController
public class OrderController {


	@Value("${user.uri}") String userUri;

	@Value("${product.uri}") String productUri;


	/*
	 * @Autowired DiscoveryClient client;
	 */

	Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	Environment environment;
	@Autowired
	private OrderService orderservice;
	@Autowired
	ProductOrderService proser;

	// Get orders by ID
	@RequestMapping(value = "/api/orders/{orderid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OrderDTO> getSpecificOrder(@PathVariable String orderid) throws KartException {
		try {
			logger.info("Order details {}", orderid);
			OrderDTO order = orderservice.getSpecificOrder(orderid);
			return new ResponseEntity<>(order, HttpStatus.OK);
		} catch (Exception exception) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, environment.getProperty(exception.getMessage()),
					exception);
		}

	}

	// Get all orders
	@GetMapping(value = "/api/orders", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<OrderDTO>> getAllOrder() throws KartException {
		try {
			logger.info("Fetching all products");
			List<OrderDTO> orderdto = orderservice.getAllOrder();
			return new ResponseEntity<>(orderdto, HttpStatus.OK);
		} catch (Exception exception) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, environment.getProperty(exception.getMessage()),
					exception);
		}

	}

	//Place orders->POST
	@PostMapping(value = "/api/orders/placeorders/{buyerid}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> Order(@PathVariable String buyerid, @RequestBody OrderDTO order) throws KartException {
		try {
			ObjectMapper mapper = new ObjectMapper();


			List<ProductDTO> productList = new ArrayList<>(); 

			//List<ServiceInstance> Uinstances= client.getInstances("userMS"); 
			//ServiceInstance Uinstance =  Uinstances.get(0); 
			//URI userUri = Uinstance.getUri();

			//List<ServiceInstance> Pinstances = client.getInstances("ProductMS");
			//ServiceInstance Pinstance= Pinstances.get(0); 
			//URI productUri =Pinstance.getUri();


			List<CartDTO> cartList = mapper.convertValue(new RestTemplate().getForObject(userUri + "api/cart/" + buyerid, List.class),new TypeReference<List<CartDTO>>(){});

			cartList.forEach(item ->{
				ProductDTO prod = new RestTemplate().getForObject(productUri +"api/productid/" + item.getProdid(), ProductDTO.class);
				productList.add(prod);
			});

			OrderPlacedDTO orderPlaced = orderservice.placeOrder(productList, cartList, order);
			cartList.forEach(item->{ 
				new RestTemplate().put(productUri + "api/product/" + item.getProdid() + "/" + (~(item.getQuantity() -1)), boolean.class);
				new RestTemplate().postForObject(userUri + "cart/remove/"+ item.getBuyerid() +"/" + item.getProdid(), null, String.class);
			});	

			new RestTemplate().put(userUri + "api/user/updateRewardPoints/" + buyerid + "/"+ orderPlaced.getRewardPoints() , String.class);

			return new ResponseEntity<>(orderPlaced.getOrderId(),HttpStatus.ACCEPTED);

		} catch (Exception exception) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, environment.getProperty(exception.getMessage()),
					exception);
		}
	}

	// Reorder
	@PostMapping(value = "/api/orders/reorder/{orderid}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> reOrder(@PathVariable String orderid) throws KartException {

		String id = orderservice.reOrder(orderid);
		return new ResponseEntity<>("Order ID: "+id,HttpStatus.ACCEPTED);

	}

	// Get products ordered by ProductId
	@GetMapping(value = "/api/productsorders/{prodid}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ProductsorderedDTO>> getProductById(@PathVariable String buyerid)
			throws KartException {
		try {
			logger.info("product details request for ordered product {}", buyerid);
			List<ProductsorderedDTO> orders = proser.getProductById(buyerid);
			return new ResponseEntity<>(orders, HttpStatus.CREATED);
		} catch (Exception exception) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, environment.getProperty(exception.getMessage()),
					exception);
		}
	}

	// Change Status
	@PostMapping(value = "/api/orders/{orderid}/{status}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> reOrder(@PathVariable String orderid,@PathVariable String status) throws KartException {

		String stat = orderservice.changeStatus(orderid,status);
		return new ResponseEntity<>(stat ,HttpStatus.ACCEPTED);

	}

	// Delete order-->DELETE
	@DeleteMapping(value = "/api/order/{orderid}")
	public ResponseEntity<String> deleteOrder(@PathVariable String orderid) throws KartException {
		try {
			orderservice.deleteOrder(orderid);
			String successMessage = environment.getProperty("API.DELETE_SUCCESS");
			return new ResponseEntity<>(successMessage, HttpStatus.OK);
		} catch (Exception exception) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, environment.getProperty(exception.getMessage()),
					exception);

		}

	}
}
