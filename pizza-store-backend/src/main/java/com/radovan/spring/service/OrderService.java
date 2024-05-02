package com.radovan.spring.service;

import java.util.List;

import com.radovan.spring.dto.OrderDto;

public interface OrderService {

	OrderDto addOrder();

	List<OrderDto> listAll();

	List<OrderDto> listAllByCardId(Integer cartId);

	Float calculateOrderTotal(Integer orderId);

	OrderDto getOrderById(Integer orderId);

	void deleteOrder(Integer orderId);
}
