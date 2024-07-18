package com.radovan.spring.service.impl;

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
import com.radovan.spring.dto.PizzaSizeDto;
import com.radovan.spring.entity.CartItemEntity;
import com.radovan.spring.exceptions.InstanceUndefinedException;
import com.radovan.spring.exceptions.InvalidCartException;
import com.radovan.spring.exceptions.OperationNotAllowedException;
import com.radovan.spring.repository.CartItemRepository;
import com.radovan.spring.service.CartItemService;
import com.radovan.spring.service.CartService;
import com.radovan.spring.service.CustomerService;
import com.radovan.spring.service.PizzaSizeService;
import com.radovan.spring.service.UserService;

@Service
public class CartItemServiceImpl implements CartItemService {

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private CartService cartService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private PizzaSizeService pizzaSizeService;

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private UserService userService;

	@Override
	@Transactional
	public CartItemDto addCartItem(CartItemDto cartItem) {
		// TODO Auto-generated method stub

		pizzaSizeService.getPizzaSizeById(cartItem.getPizzaSizeId());

		CartItemDto returnValue = null;
		CartItemEntity cartItemEntity = null;
		CustomerDto customer = customerService.getCurrentCustomer();
		cartItem.setCartId(customer.getCartId());
		Optional<List<CartItemEntity>> cartItemsOptional = Optional
				.ofNullable(cartItemRepository.findAllByCartId(customer.getCartId()));
		if (!cartItemsOptional.isEmpty()) {
			List<CartItemEntity> cartItems = cartItemsOptional.get();
			Integer quantity = cartItem.getQuantity();
			Integer cartQuantiy = 0;
			for (CartItemEntity itemEntity : cartItems) {
				cartQuantiy = cartQuantiy + itemEntity.getQuantity();
			}

			if (cartQuantiy + quantity > 20) {
				Error error = new Error("Maximum 20 pizzas allowed in the cart");
				throw new InvalidCartException(error);
			}
			for (CartItemEntity itemEntity : cartItems) {
				if (itemEntity.getPizzaSize().getPizzaSizeId() == cartItem.getPizzaSizeId()) {
					quantity = quantity + itemEntity.getQuantity();
					cartItem.setCartItemId(itemEntity.getCartItemId());
				}
			}

			cartItem.setQuantity(quantity);
			cartItemEntity = tempConverter.cartItemDtoToEntity(cartItem);
			CartItemEntity updatedItem = cartItemRepository.saveAndFlush(cartItemEntity);
			cartService.refreshCartState(customer.getCartId());
			returnValue = tempConverter.cartItemEntityToDto(updatedItem);
		} else {
			cartItemEntity = tempConverter.cartItemDtoToEntity(cartItem);
			CartItemEntity storedItem = cartItemRepository.saveAndFlush(cartItemEntity);
			cartService.refreshCartState(customer.getCartId());
			returnValue = tempConverter.cartItemEntityToDto(storedItem);

		}

		return returnValue;
	}

	@Override
	@Transactional
	public void removeCartItem(Integer itemId) {
		// TODO Auto-generated method stub
		CustomerDto currentCustomer = customerService.getCurrentCustomer();
		CartItemDto cartItem = getCartItem(itemId);
		Integer cartId = cartItem.getCartId();
		if (cartId == currentCustomer.getCartId()) {
			cartItemRepository.removeCartItem(itemId);
			cartItemRepository.flush();
			cartService.refreshCartState(cartId);
		} else {
			Error error = new Error("Operation not allowed!");
			throw new OperationNotAllowedException(error);
		}

	}

	@Override
	@Transactional
	public void eraseAllCartItems(Integer cartId) {
		// TODO Auto-generated method stub
		CartDto cart = cartService.getCartByCartId(cartId);
		CustomerDto currentCustomer = customerService.getCurrentCustomer();
		if (userService.isAdmin() || cartId == currentCustomer.getCartId()) {
			cartItemRepository.removeAllByCartId(cart.getCartId());
			cartItemRepository.flush();
			cartService.refreshCartState(cartId);
		} else {
			Error error = new Error("Operation not allowed!");
			throw new OperationNotAllowedException(error);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CartItemDto> listAllByCartId(Integer cartId) {
		// TODO Auto-generated method stub
		CartDto cart = cartService.getCartByCartId(cartId);
		List<CartItemDto> returnValue = new ArrayList<>();
		List<CartItemEntity> allCartItems = cartItemRepository.findAllByCartId(cart.getCartId());
		if (!allCartItems.isEmpty()) {
			allCartItems.forEach((itemEntity) -> {
				CartItemDto itemDto = tempConverter.cartItemEntityToDto(itemEntity);
				returnValue.add(itemDto);
			});
		}

		return returnValue;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CartItemDto> listAllByPizzaSizeId(Integer pizzaSizeId) {
		// TODO Auto-generated method stub

		pizzaSizeService.getPizzaSizeById(pizzaSizeId);

		List<CartItemDto> returnValue = new ArrayList<>();
		List<CartItemEntity> allCartItems = cartItemRepository.findAllByPizzaSizeId(pizzaSizeId);
		if (!allCartItems.isEmpty()) {
			allCartItems.forEach((itemEntity) -> {
				CartItemDto itemDto = tempConverter.cartItemEntityToDto(itemEntity);
				returnValue.add(itemDto);
			});
		}

		return returnValue;
	}

	@Override
	@Transactional(readOnly = true)
	public CartItemDto getCartItem(Integer id) {
		// TODO Auto-generated method stub
		CartItemDto returnValue = null;
		Optional<CartItemEntity> itemOptional = cartItemRepository.findById(id);
		if (itemOptional.isPresent()) {
			returnValue = tempConverter.cartItemEntityToDto(itemOptional.get());
		} else {
			Error error = new Error("Cart item has not been found!");
			throw new InstanceUndefinedException(error);
		}

		return returnValue;
	}

	@Override
	@Transactional
	public void eraseAllByPizzaSizeId(Integer pizzaSizeId) {
		// TODO Auto-generated method stub

		PizzaSizeDto pizzaSize = pizzaSizeService.getPizzaSizeById(pizzaSizeId);
		cartItemRepository.removeAllByPizzaSizeId(pizzaSize.getPizzaSizeId());
		cartItemRepository.flush();

	}

}
