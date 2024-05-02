package com.radovan.spring.utils;

import java.io.Serializable;

import com.radovan.spring.dto.CustomerDto;
import com.radovan.spring.dto.ShippingAddressDto;
import com.radovan.spring.dto.UserDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class RegistrationForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Valid
	@NotNull
	private UserDto user;

	@Valid
	@NotNull
	private CustomerDto customer;

	@Valid
	@NotNull
	private ShippingAddressDto shippingAddress;

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}

	public CustomerDto getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerDto customer) {
		this.customer = customer;
	}

	public ShippingAddressDto getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(ShippingAddressDto shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

}
