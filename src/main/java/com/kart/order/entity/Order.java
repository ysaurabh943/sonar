package com.kart.order.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "orders")
public class Order{

	@Id
	@GenericGenerator(name = "order_id", strategy = "com.kart.order.generator.OrderIdGenerator")
    @GeneratedValue(generator = "order_id")  
	String orderid;
	
	@Column(nullable = false)
	String buyerid;
	@Column(nullable = false)
	Double amount;
	@Column(nullable = false)
	Date orderdate;
	@Column(nullable = false)
	String address;
	@Column(nullable = false)
	String status;


	public String getOrderid() {
		return orderid;
	}

	public void setOrderId(String orderid) {
		this.orderid = orderid;
	}

	public String getBuyerid() {
		return buyerid;
	}

	public void setBuyerid(String buyerid) {
		this.buyerid = buyerid;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Date getOrderdate() {
		return orderdate;
	}

	public void setOrderdate(Date orderdate) {
		this.orderdate = orderdate;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
