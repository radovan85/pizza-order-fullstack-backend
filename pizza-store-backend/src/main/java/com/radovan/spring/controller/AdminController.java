package com.radovan.spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.radovan.spring.dto.CustomerDto;
import com.radovan.spring.dto.OrderAddressDto;
import com.radovan.spring.dto.OrderDto;
import com.radovan.spring.dto.OrderItemDto;
import com.radovan.spring.dto.PizzaDto;
import com.radovan.spring.dto.PizzaSizeDto;
import com.radovan.spring.dto.UserDto;
import com.radovan.spring.exceptions.DataNotValidatedException;
import com.radovan.spring.service.CustomerService;
import com.radovan.spring.service.OrderAddressService;
import com.radovan.spring.service.OrderItemService;
import com.radovan.spring.service.OrderService;
import com.radovan.spring.service.PizzaService;
import com.radovan.spring.service.PizzaSizeService;
import com.radovan.spring.service.UserService;

@RestController
@RequestMapping(value = "/api/admin")
public class AdminController {

	@Autowired
	private PizzaService pizzaService;

	@Autowired
	private PizzaSizeService pizzaSizeService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private UserService userService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private OrderItemService orderItemService;

	@Autowired
	private OrderAddressService orderAddressService;

	@PostMapping(value = "/storePizza")
	public ResponseEntity<String> storePizza(@Validated @RequestBody PizzaDto pizza, Errors errors) {

		if (errors.hasErrors()) {
			Error error = new Error("The data is not validated");
			throw new DataNotValidatedException(error);
		}

		PizzaDto storedPizza = pizzaService.addPizza(pizza);
		return ResponseEntity.ok().body("The pizza with id " + storedPizza.getPizzaId() + " has been stored!");
	}

	@PutMapping(value = "/updatePizza/{pizzaId}")
	public ResponseEntity<String> updatePizza(@Validated @RequestBody PizzaDto pizza, Errors errors,
			@PathVariable("pizzaId") Integer pizzaId) {

		if (errors.hasErrors()) {
			Error error = new Error("The data is not validated");
			throw new DataNotValidatedException(error);
		}

		PizzaDto updatedPizza = pizzaService.updatePizza(pizzaId, pizza);
		return ResponseEntity.ok()
				.body("The product with id " + updatedPizza.getPizzaId() + " has been updated without any issues");
	}

	@DeleteMapping(value = "/deletePizza/{pizzaId}")
	public ResponseEntity<String> deletePizza(@PathVariable("pizzaId") Integer pizzaId) {

		pizzaService.deletePizza(pizzaId);
		return ResponseEntity.ok().body("The pizza with id " + pizzaId + " has been permanently deleted!");
	}

	@PostMapping(value = "/storePizzaSize")
	public ResponseEntity<String> storePizzaSize(@Validated @RequestBody PizzaSizeDto pizzaSize, Errors errors) {

		if (errors.hasErrors()) {
			Error error = new Error("The data is not validated!");
			throw new DataNotValidatedException(error);
		}

		PizzaSizeDto storedSize = pizzaSizeService.addPizzaSize(pizzaSize);
		return ResponseEntity.ok().body("Pizza size with id " + storedSize.getPizzaSizeId() + " has been stored!");
	}

	@PutMapping(value = "/updatePizzaSize/{sizeId}")
	public ResponseEntity<String> updatePizzaSize(@Validated @RequestBody PizzaSizeDto pizzaSize, Errors errors,
			@PathVariable("sizeId") Integer sizeId) {

		if (errors.hasErrors()) {
			Error error = new Error("The data is not validated!");
			throw new DataNotValidatedException(error);
		}

		PizzaSizeDto updatedSize = pizzaSizeService.updatePizzaSize(sizeId, pizzaSize);
		return ResponseEntity.ok()
				.body("Pizza size with id " + updatedSize.getPizzaSizeId() + " has been updated without any issues!");
	}

	@DeleteMapping(value = "/deletePizzaSize/{sizeId}")
	public ResponseEntity<String> deletePizzaSize(@PathVariable("sizeId") Integer sizeId) {

		pizzaSizeService.deletePizzaSize(sizeId);
		return ResponseEntity.ok().body("Pizza size with id " + sizeId + " has been permanently deleted!");
	}

	@DeleteMapping(value = "/deleteOrder/{orderId}")
	public ResponseEntity<String> deleteOrder(@PathVariable("orderId") Integer orderId) {
		orderService.deleteOrder(orderId);
		return ResponseEntity.ok().body("Order with id " + orderId + " has been permanently deleted!");
	}

	@GetMapping(value = "/allOrders")
	public ResponseEntity<List<OrderDto>> getAllOrders() {
		List<OrderDto> allOrders = orderService.listAll();
		return ResponseEntity.ok().body(allOrders);
	}

	@GetMapping(value = "/allUsers")
	public ResponseEntity<List<UserDto>> getAllUsers() {
		List<UserDto> allUsers = userService.listAllUsers();
		return ResponseEntity.ok().body(allUsers);
	}

	@GetMapping(value = "/suspendUser/{userId}")
	public ResponseEntity<String> suspendUser(@PathVariable("userId") Integer userId) {

		userService.suspendUser(userId);
		return ResponseEntity.ok().body("The user with id " + userId + " has been suspended!");
	}

	@GetMapping(value = "/reactivateUser/{userId}")
	public ResponseEntity<String> reactivateUser(@PathVariable("userId") Integer userId) {

		userService.clearSuspension(userId);
		return ResponseEntity.ok().body("The user with id " + userId + " has been reactivated!");
	}

	@GetMapping(value = "/allCustomers")
	public ResponseEntity<List<CustomerDto>> getAllCustomers() {
		List<CustomerDto> allCustomers = customerService.getAllCustomers();
		return ResponseEntity.ok().body(allCustomers);
	}

	@DeleteMapping(value = "/deleteCustomer/{customerId}")
	public ResponseEntity<String> deleteCustomer(@PathVariable("customerId") Integer customerId) {
		customerService.deleteCustomer(customerId);
		return ResponseEntity.ok("The customer with id " + customerId + "has been permanently deleted!");
	}

	@GetMapping(value = "/allItems/{orderId}")
	public ResponseEntity<List<OrderItemDto>> listAllByOrderId(@PathVariable("orderId") Integer orderId) {
		List<OrderItemDto> allItems = orderItemService.listAllByOrderId(orderId);
		return ResponseEntity.ok().body(allItems);
	}

	@GetMapping(value = "/orderDetails/{orderId}")
	public ResponseEntity<OrderDto> getOrderDetails(@PathVariable("orderId") Integer orderId) {
		OrderDto order = orderService.getOrderById(orderId);
		return ResponseEntity.ok().body(order);
	}

	@GetMapping(value = "/orderAddress/{orderId}")
	public ResponseEntity<OrderAddressDto> getOrderAddress(@PathVariable("orderId") Integer orderId) {
		OrderDto order = orderService.getOrderById(orderId);
		OrderAddressDto address = orderAddressService.getAddressById(order.getAddressId());
		return ResponseEntity.ok().body(address);
	}

	@GetMapping(value = "/customerDetails/{customerId}")
	public ResponseEntity<CustomerDto> getCustomerDetails(@PathVariable("customerId") Integer customerId) {
		CustomerDto customer = customerService.getCustomer(customerId);
		return ResponseEntity.ok().body(customer);
	}

}
