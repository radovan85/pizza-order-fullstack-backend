package com.radovan.spring.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.CustomerDto;
import com.radovan.spring.dto.ShippingAddressDto;
import com.radovan.spring.entity.ShippingAddressEntity;
import com.radovan.spring.exceptions.InstanceUndefinedException;
import com.radovan.spring.exceptions.OperationNotAllowedException;
import com.radovan.spring.repository.ShippingAddressRepository;
import com.radovan.spring.service.CustomerService;
import com.radovan.spring.service.ShippingAddressService;

@Service
public class ShippingAddressServiceImpl implements ShippingAddressService {

	@Autowired
	private ShippingAddressRepository addressRepository;

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private CustomerService customerService;

	@Override
	@Transactional
	public ShippingAddressDto updateShippingAddress(Integer id, ShippingAddressDto shippingAddress) {
		// TODO Auto-generated method stub
		ShippingAddressDto returnValue = null;
		CustomerDto currentCustomer = customerService.getCurrentCustomer();
		if (id != currentCustomer.getShippingAddressId()) {
			throw new OperationNotAllowedException(new Error("This operation is not allowed!"));
		}
		ShippingAddressDto currentAddress = getShippingAddress(id);
		shippingAddress.setShippingAddressId(currentAddress.getShippingAddressId());
		shippingAddress.setCustomerId(currentAddress.getCustomerId());
		ShippingAddressEntity addressEntity = tempConverter.shippingAddressDtoToEntity(shippingAddress);
		ShippingAddressEntity updatedAddress = addressRepository.saveAndFlush(addressEntity);
		returnValue = tempConverter.shippingAddressEntityToDto(updatedAddress);

		return returnValue;
	}

	@Override
	@Transactional(readOnly = true)
	public ShippingAddressDto getShippingAddress(Integer addressId) {
		// TODO Auto-generated method stub
		ShippingAddressDto returnValue = null;
		Optional<ShippingAddressEntity> addressOptional = addressRepository.findById(addressId);
		if (addressOptional.isPresent()) {
			returnValue = tempConverter.shippingAddressEntityToDto(addressOptional.get());
		} else {
			Error error = new Error("The address has not been found!");
			throw new InstanceUndefinedException(error);
		}
		return returnValue;
	}

}
