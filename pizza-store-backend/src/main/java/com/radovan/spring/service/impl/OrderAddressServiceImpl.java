package com.radovan.spring.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.OrderAddressDto;
import com.radovan.spring.entity.OrderAddressEntity;
import com.radovan.spring.exceptions.InstanceUndefinedException;
import com.radovan.spring.repository.OrderAddressRepository;
import com.radovan.spring.service.OrderAddressService;

@Service
public class OrderAddressServiceImpl implements OrderAddressService {

	@Autowired
	private OrderAddressRepository addressRepository;

	@Autowired
	private TempConverter tempConverter;

	@Override
	@Transactional(readOnly = true)
	public OrderAddressDto getAddressById(Integer addressId) {
		// TODO Auto-generated method stub
		OrderAddressDto returnValue = null;
		Optional<OrderAddressEntity> addressOptional = addressRepository.findById(addressId);
		if (addressOptional.isPresent()) {
			returnValue = tempConverter.orderAddressEntityToDto(addressOptional.get());
		} else {
			Error error = new Error("The address has not been found!");
			throw new InstanceUndefinedException(error);
		}
		return returnValue;
	}

	@Override
	@Transactional
	public void deleteAddress(Integer addressId) {
		// TODO Auto-generated method stub
		OrderAddressDto address = getAddressById(addressId);
		addressRepository.deleteById(address.getOrderAddressId());
		addressRepository.flush();
	}

}
