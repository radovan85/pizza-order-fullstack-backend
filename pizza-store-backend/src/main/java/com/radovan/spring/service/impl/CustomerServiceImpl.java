package com.radovan.spring.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.CustomerDto;
import com.radovan.spring.dto.OrderDto;
import com.radovan.spring.dto.ShippingAddressDto;
import com.radovan.spring.dto.UserDto;
import com.radovan.spring.entity.CartEntity;
import com.radovan.spring.entity.CustomerEntity;
import com.radovan.spring.entity.RoleEntity;
import com.radovan.spring.entity.ShippingAddressEntity;
import com.radovan.spring.entity.UserEntity;
import com.radovan.spring.exceptions.ExistingInstanceException;
import com.radovan.spring.exceptions.InstanceUndefinedException;
import com.radovan.spring.repository.CartRepository;
import com.radovan.spring.repository.CustomerRepository;
import com.radovan.spring.repository.RoleRepository;
import com.radovan.spring.repository.ShippingAddressRepository;
import com.radovan.spring.repository.UserRepository;
import com.radovan.spring.service.CustomerService;
import com.radovan.spring.service.OrderService;
import com.radovan.spring.service.UserService;
import com.radovan.spring.utils.RegistrationForm;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private ShippingAddressRepository shippingAddressRepository;

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserService userService;

	@Autowired
	private OrderService orderService;

	@Override
	@Transactional
	public CustomerDto storeCustomer(RegistrationForm form) {
		// TODO Auto-generated method stub
		Optional<UserEntity> userOptional = userRepository.findByEmail(form.getUser().getEmail());
		if (userOptional.isPresent()) {
			Error error = new Error("This email already exists.");
			throw new ExistingInstanceException(error);
		}

		CustomerDto returnValue = form.getCustomer();
		UserDto user = form.getUser();
		user.setEnabled((byte) 1);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		RoleEntity roleEntity = roleRepository.findByRole("ROLE_USER");
		List<Integer> rolesIds = new ArrayList<>();
		rolesIds.add(roleEntity.getId());
		user.setRolesIds(rolesIds);
		UserEntity userEntity = tempConverter.userDtoToEntity(user);
		UserEntity storedUser = userRepository.save(userEntity);

		List<UserEntity> usersList = roleEntity.getUsers();
		if (usersList == null) {
			usersList = new ArrayList<>();
		}

		usersList.add(storedUser);
		roleEntity.setUsers(usersList);
		roleEntity = roleRepository.saveAndFlush(roleEntity);

		ShippingAddressDto shippingAddress = form.getShippingAddress();
		ShippingAddressEntity shippingAddressEntity = tempConverter.shippingAddressDtoToEntity(shippingAddress);
		ShippingAddressEntity storedAddress = shippingAddressRepository.save(shippingAddressEntity);

		CartEntity cartEntity = new CartEntity();
		cartEntity.setCartPrice(0f);
		CartEntity storedCart = cartRepository.save(cartEntity);

		returnValue.setUserId(storedUser.getId());
		returnValue.setCartId(storedCart.getCartId());
		returnValue.setShippingAddressId(storedAddress.getShippingAddressId());
		CustomerEntity customerEntity = tempConverter.customerDtoToEntity(returnValue);
		CustomerEntity storedCustomer = customerRepository.save(customerEntity);

		storedCart.setCustomer(storedCustomer);
		cartRepository.saveAndFlush(storedCart);

		storedAddress.setCustomer(storedCustomer);
		shippingAddressRepository.saveAndFlush(storedAddress);

		returnValue = tempConverter.customerEntityToDto(storedCustomer);
		return returnValue;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CustomerDto> getAllCustomers() {
		// TODO Auto-generated method stub
		List<CustomerDto> returnValue = new ArrayList<>();
		List<CustomerEntity> allCustomers = customerRepository.findAll();
		if (!allCustomers.isEmpty()) {
			allCustomers.forEach((customerEntity) -> {
				CustomerDto customer = tempConverter.customerEntityToDto(customerEntity);
				returnValue.add(customer);
			});
		}

		return returnValue;
	}

	@Override
	@Transactional(readOnly = true)
	public CustomerDto getCustomer(Integer id) {
		// TODO Auto-generated method stub
		CustomerDto returnValue = null;
		Optional<CustomerEntity> customerOptional = customerRepository.findById(id);
		if (customerOptional.isPresent()) {
			returnValue = tempConverter.customerEntityToDto(customerOptional.get());
		} else {
			Error error = new Error("The customer has not been found");
			throw new InstanceUndefinedException(error);
		}
		return returnValue;
	}

	@Override
	@Transactional(readOnly = true)
	public CustomerDto getCustomerByUserId(Integer userId) {
		// TODO Auto-generated method stub
		CustomerDto returnValue = null;
		Optional<CustomerEntity> customerOptional = Optional.ofNullable(customerRepository.findByUserId(userId));
		if (customerOptional.isPresent()) {
			returnValue = tempConverter.customerEntityToDto(customerOptional.get());
		} else {
			Error error = new Error("The customer has not been found");
			throw new InstanceUndefinedException(error);
		}
		return returnValue;
	}

	@Override
	@Transactional(readOnly = true)
	public CustomerDto getCustomerByCartId(Integer cartId) {
		// TODO Auto-generated method stub
		CustomerDto returnValue = null;
		Optional<CustomerEntity> customerOptional = Optional.ofNullable(customerRepository.findByCartId(cartId));
		if (customerOptional.isPresent()) {
			returnValue = tempConverter.customerEntityToDto(customerOptional.get());
		} else {
			Error error = new Error("The customer has not been found");
			throw new InstanceUndefinedException(error);
		}
		return returnValue;
	}

	@Override
	@Transactional
	public CustomerDto updateCustomer(Integer customerId, CustomerDto customer) {
		// TODO Auto-generated method stub
		CustomerDto returnValue = null;
		Optional<CustomerEntity> customerOptional = customerRepository.findById(customerId);
		if (customerOptional.isPresent()) {
			CustomerEntity customerEntity = customerOptional.get();
			customerEntity.setCustomerPhone(customer.getCustomerPhone());
			CustomerEntity updatedCustomer = customerRepository.saveAndFlush(customerEntity);
			returnValue = tempConverter.customerEntityToDto(updatedCustomer);
		}
		return returnValue;
	}

	@Override
	@Transactional
	public void deleteCustomer(Integer customerId) {
		// TODO Auto-generated method stub
		CustomerDto customer = getCustomer(customerId);
		List<OrderDto> orders = orderService.listAllByCardId(customer.getCartId());
		orders.forEach((order) -> {
			orderService.deleteOrder(order.getOrderId());
		});
		customerRepository.deleteById(customer.getCustomerId());
		userRepository.flush();
	}

	@Override
	@Transactional(readOnly = true)
	public CustomerDto getCurrentCustomer() {
		// TODO Auto-generated method stub
		CustomerDto returnValue = null;
		UserDto authUser = userService.getCurrentUser();
		returnValue = getCustomerByUserId(authUser.getId());
		return returnValue;
	}

}
