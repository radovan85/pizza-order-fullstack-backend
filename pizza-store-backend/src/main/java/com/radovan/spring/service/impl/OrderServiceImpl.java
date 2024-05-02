package com.radovan.spring.service.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.CartDto;
import com.radovan.spring.dto.CartItemDto;
import com.radovan.spring.dto.CustomerDto;
import com.radovan.spring.dto.OrderAddressDto;
import com.radovan.spring.dto.OrderDto;
import com.radovan.spring.dto.OrderItemDto;
import com.radovan.spring.dto.ShippingAddressDto;
import com.radovan.spring.entity.OrderAddressEntity;
import com.radovan.spring.entity.OrderEntity;
import com.radovan.spring.entity.OrderItemEntity;
import com.radovan.spring.exceptions.InstanceUndefinedException;
import com.radovan.spring.repository.OrderAddressRepository;
import com.radovan.spring.repository.OrderItemRepository;
import com.radovan.spring.repository.OrderRepository;
import com.radovan.spring.service.CartItemService;
import com.radovan.spring.service.CartService;
import com.radovan.spring.service.CustomerService;
import com.radovan.spring.service.OrderService;
import com.radovan.spring.service.ShippingAddressService;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderAddressRepository orderAddressRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private ShippingAddressService shippingAddressService;

	@Autowired
	private CartItemService cartItemService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CartService cartService;

	@Autowired
	private TempConverter tempConverter;

	private ZoneId zoneId = ZoneId.of("UTC");

	@Override
	@Transactional
	public OrderDto addOrder() {
		// TODO Auto-generated method stub
		OrderDto returnValue = new OrderDto();
		CustomerDto customer = customerService.getCurrentCustomer();
		CartDto cart = cartService.getCartByCartId(customer.getCartId());
		cartService.validateCart(cart.getCartId());
		Float grandTotal = cartService.calculateGrandTotal(cart.getCartId());
		returnValue.setCartId(cart.getCartId());
		returnValue.setOrderPrice(grandTotal);
		ShippingAddressDto shippingAddress = shippingAddressService.getShippingAddress(customer.getShippingAddressId());
		OrderAddressDto orderAddress = tempConverter.shippingAddressToOrderAddress(shippingAddress);
		OrderAddressEntity orderAddressEntity = tempConverter.orderAddressDtoToEntity(orderAddress);
		OrderAddressEntity storedAddress = orderAddressRepository.save(orderAddressEntity);
		OrderEntity orderEntity = tempConverter.orderDtoToEntity(returnValue);
		orderEntity.setAddress(storedAddress);
		ZonedDateTime currentTime = Instant.now().atZone(zoneId);
		orderEntity.setCreatedAt(Timestamp.valueOf(currentTime.toLocalDateTime()));
		OrderEntity storedOrder = orderRepository.save(orderEntity);

		List<OrderItemDto> orderedItems = new ArrayList<>();

		List<CartItemDto> cartItems = cartItemService.listAllByCartId(cart.getCartId());

		cartItems.forEach((cartItemDto) -> {
			OrderItemDto orderItem = tempConverter.cartItemToOrderItemDto(cartItemDto);
			orderedItems.add(orderItem);
		});

		List<OrderItemEntity> allOrderedItems = new ArrayList<>();

		for (OrderItemDto orderItem : orderedItems) {
			orderItem.setOrderId(storedOrder.getOrderId());
			OrderItemEntity orderItemEntity = tempConverter.orderItemDtoToEntity(orderItem);
			OrderItemEntity storedItem = orderItemRepository.save(orderItemEntity);
			allOrderedItems.add(storedItem);
		}

		storedOrder.getOrderedItems().clear();
		storedOrder.getOrderedItems().addAll(allOrderedItems);
		storedOrder = orderRepository.saveAndFlush(storedOrder);
		returnValue = tempConverter.orderEntityToDto(storedOrder);
		cartItemService.eraseAllCartItems(cart.getCartId());
		cartService.refreshCartState(cart.getCartId());

		return returnValue;
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderDto> listAll() {
		// TODO Auto-generated method stub
		List<OrderDto> returnValue = new ArrayList<>();
		List<OrderEntity> allOrders = orderRepository.findAll();
		if (!allOrders.isEmpty()) {
			allOrders.forEach((orderEntity) -> {
				OrderDto orderDto = tempConverter.orderEntityToDto(orderEntity);
				returnValue.add(orderDto);
			});
		}
		return returnValue;
	}

	@Override
	@Transactional(readOnly = true)
	public Float calculateOrderTotal(Integer orderId) {
		// TODO Auto-generated method stub
		OrderDto order = getOrderById(orderId);
		Float returnValue = 0f;
		Optional<Float> grandTotalOptional = Optional
				.ofNullable(orderItemRepository.calculateGrandTotal(order.getOrderId()));
		if (grandTotalOptional.isPresent()) {
			returnValue = grandTotalOptional.get();
		}
		return returnValue;
	}

	@Override
	@Transactional(readOnly = true)
	public OrderDto getOrderById(Integer orderId) {
		// TODO Auto-generated method stub
		OrderDto returnValue = null;
		Optional<OrderEntity> orderOptional = orderRepository.findById(orderId);
		if (orderOptional.isPresent()) {
			returnValue = tempConverter.orderEntityToDto(orderOptional.get());
		} else {
			Error error = new Error("The order has not been found");
			throw new InstanceUndefinedException(error);
		}
		return returnValue;
	}

	@Override
	@Transactional
	public void deleteOrder(Integer orderId) {
		// TODO Auto-generated method stub
		OrderDto order = getOrderById(orderId);
		orderRepository.deleteById(order.getOrderId());
		orderRepository.flush();
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderDto> listAllByCardId(Integer cartId) {
		// TODO Auto-generated method stub
		List<OrderDto> returnValue = new ArrayList<>();
		List<OrderEntity> allOrders = orderRepository.findAllByCartId(cartId);
		if (!allOrders.isEmpty()) {
			allOrders.forEach((orderEntity) -> {
				OrderDto orderDto = tempConverter.orderEntityToDto(orderEntity);
				returnValue.add(orderDto);
			});
		}
		return returnValue;
	}

}
