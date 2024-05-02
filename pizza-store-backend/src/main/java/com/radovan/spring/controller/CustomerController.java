package com.radovan.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.radovan.spring.dto.CustomerDto;
import com.radovan.spring.dto.ShippingAddressDto;
import com.radovan.spring.exceptions.DataNotValidatedException;
import com.radovan.spring.service.CustomerService;
import com.radovan.spring.service.ShippingAddressService;

@RestController
@RequestMapping(value = "/api/customers")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private ShippingAddressService shippingAddressService;

	@GetMapping(value = "/getMyAddress")
	public ResponseEntity<ShippingAddressDto> getMyAddress() {
		CustomerDto customer = customerService.getCurrentCustomer();
		ShippingAddressDto address = shippingAddressService.getShippingAddress(customer.getShippingAddressId());
		return ResponseEntity.ok().body(address);
	}

	@GetMapping(value = "/currentCustomer")
	public ResponseEntity<CustomerDto> getCurrentCustomer() {
		CustomerDto customer = customerService.getCurrentCustomer();
		return ResponseEntity.ok().body(customer);
	}

	@PutMapping(value = "/updateCustomer")
	public ResponseEntity<String> updateCustomer(@Validated @RequestBody CustomerDto customer, Errors errors) {

		CustomerDto currentCustomer = customerService.getCurrentCustomer();
		customer.setCustomerId(currentCustomer.getCustomerId());

		if (errors.hasErrors()) {
			Error error = new Error("The data has not been validated!");
			throw new DataNotValidatedException(error);
		}

		CustomerDto updatedCustomer = customerService.updateCustomer(customer.getCustomerId(), customer);
		return ResponseEntity.ok().body(
				"The customer with id: " + updatedCustomer.getCustomerId() + " has been updated without any issues!");
	}

	@PutMapping(value = "/updateShippingAddress/{addressId}")
	public ResponseEntity<String> updateShippingAddress(@PathVariable("addressId") Integer addressId,
			@RequestBody ShippingAddressDto address, Errors errors) {

		if (errors.hasErrors()) {
			Error error = new Error("The data has not been validated!");
			throw new DataNotValidatedException(error);
		}

		shippingAddressService.updateShippingAddress(addressId, address);
		return ResponseEntity.ok().body("The address has been updated without any issues");

	}
}
