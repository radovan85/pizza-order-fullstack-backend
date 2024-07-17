package com.radovan.spring.service;

import com.radovan.spring.dto.ShippingAddressDto;

public interface ShippingAddressService {

	ShippingAddressDto updateShippingAddress(Integer id, ShippingAddressDto shippingAddress);

	ShippingAddressDto getShippingAddress(Integer addressId);
	
}
