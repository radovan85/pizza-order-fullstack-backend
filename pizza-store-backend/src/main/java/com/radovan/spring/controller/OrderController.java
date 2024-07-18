package com.radovan.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.radovan.spring.dto.CustomerDto;
import com.radovan.spring.service.CartService;
import com.radovan.spring.service.CustomerService;
import com.radovan.spring.service.OrderService;

@RestController
@RequestMapping(value = "/api/order")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CartService cartService;

	@PostMapping(value = "/createOrder")
	public ResponseEntity<String> createOrder() {
		orderService.addOrder();
		return ResponseEntity.ok().body("Order completed!");
	}

	@GetMapping(value = "/checkout")
	public ResponseEntity<String> checkout() {
		CustomerDto currentCustomer = customerService.getCurrentCustomer();
		cartService.validateCart(currentCustomer.getCartId());
		return ResponseEntity.ok().body("Ckeckout is processing...");
	}
}
