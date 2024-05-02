package com.radovan.spring.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.CartDto;
import com.radovan.spring.entity.CartEntity;
import com.radovan.spring.entity.CartItemEntity;
import com.radovan.spring.exceptions.InvalidCartException;
import com.radovan.spring.repository.CartItemRepository;
import com.radovan.spring.repository.CartRepository;
import com.radovan.spring.service.CartService;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private TempConverter tempConverter;

	@Override
	@Transactional(readOnly = true)
	public CartDto getCartByCartId(Integer cartId) {
		// TODO Auto-generated method stub
		CartDto returnValue = null;
		Optional<CartEntity> cartOptional = cartRepository.findById(cartId);
		if (cartOptional.isPresent()) {
			returnValue = tempConverter.cartEntityToDto(cartOptional.get());
		} else {
			Error error = new Error("Invalid cart!");
			throw new InvalidCartException(error);
		}
		return returnValue;
	}

	@Override
	@Transactional
	public void refreshCartState(Integer cartId) {
		// TODO Auto-generated method stub
		CartDto cart = getCartByCartId(cartId);
		Float grandTotal = cartItemRepository.calculateGrandTotal(cartId);
		cart.setCartPrice(grandTotal);
		CartEntity cartEntity = tempConverter.cartDtoToEntity(cart);
		cartRepository.saveAndFlush(cartEntity);

	}

	@Override
	@Transactional(readOnly = true)
	public Float calculateGrandTotal(Integer cartId) {
		// TODO Auto-generated method stub
		getCartByCartId(cartId);
		Float returnValue = cartItemRepository.calculateGrandTotal(cartId);
		return returnValue;
	}

	@Override
	@Transactional(readOnly = true)
	public CartDto validateCart(Integer cartId) {
		// TODO Auto-generated method stub
		CartDto returnValue = null;
		Optional<CartEntity> cartOptional = cartRepository.findById(cartId);
		if (cartOptional.isPresent()) {
			List<CartItemEntity> allCartItems = cartItemRepository.findAllByCartId(cartId);
			if (allCartItems == null || allCartItems.size() == 0) {
				Error error = new Error("Invalid Cart");
				throw new InvalidCartException(error);
			} else {
				returnValue = tempConverter.cartEntityToDto(cartOptional.get());
			}

		} else {
			Error error = new Error("Invalid cart!");
			throw new InvalidCartException(error);
		}
		return returnValue;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CartDto> listAll() {
		// TODO Auto-generated method stub
		List<CartDto> returnValue = new ArrayList<>();
		List<CartEntity> allCarts = cartRepository.findAll();
		if (!allCarts.isEmpty()) {
			allCarts.forEach((cartEntity) -> {
				CartDto cartDto = tempConverter.cartEntityToDto(cartEntity);
				returnValue.add(cartDto);
			});
		}
		return returnValue;
	}

	@Override
	@Transactional
	public void deleteCart(Integer cartId) {
		// TODO Auto-generated method stub
		CartDto cart = getCartByCartId(cartId);
		cartRepository.deleteById(cart.getCartId());
		cartRepository.flush();
	}

}
