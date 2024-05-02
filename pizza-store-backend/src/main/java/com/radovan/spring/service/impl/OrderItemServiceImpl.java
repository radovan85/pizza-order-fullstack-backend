package com.radovan.spring.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.OrderDto;
import com.radovan.spring.dto.OrderItemDto;
import com.radovan.spring.entity.OrderItemEntity;
import com.radovan.spring.repository.OrderItemRepository;
import com.radovan.spring.service.OrderItemService;
import com.radovan.spring.service.OrderService;

@Service
public class OrderItemServiceImpl implements OrderItemService {

	@Autowired
	private OrderItemRepository itemRepository;

	@Autowired
	private OrderService orderService;

	@Autowired
	private TempConverter tempConverter;

	@Override
	@Transactional(readOnly = true)
	public List<OrderItemDto> listAllByOrderId(Integer orderId) {
		// TODO Auto-generated method stub
		OrderDto order = orderService.getOrderById(orderId);
		List<OrderItemDto> returnValue = new ArrayList<>();
		List<OrderItemEntity> allItems = itemRepository.findAllByOrderId(order.getOrderId());
		if (!allItems.isEmpty()) {
			allItems.forEach((itemEntity) -> {
				OrderItemDto itemDto = tempConverter.orderItemEntityToDto(itemEntity);
				returnValue.add(itemDto);
			});
		}
		return returnValue;
	}

	@Override
	@Transactional
	public void eraseAllByOrderId(Integer orderId) {
		OrderDto order = orderService.getOrderById(orderId);
		itemRepository.deleteAllByOrderId(order.getOrderId());
		itemRepository.flush();

	}

}
