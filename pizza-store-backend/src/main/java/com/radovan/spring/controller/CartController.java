package com.radovan.spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.radovan.spring.dto.CartDto;
import com.radovan.spring.dto.CartItemDto;
import com.radovan.spring.dto.CustomerDto;
import com.radovan.spring.exceptions.DataNotValidatedException;
import com.radovan.spring.service.CartItemService;
import com.radovan.spring.service.CartService;
import com.radovan.spring.service.CustomerService;

@RestController
@RequestMapping(value = "/api/cart")
public class CartController {

	@Autowired
	private CartItemService cartItemService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CartService cartService;

	@PostMapping(value = "/addCartItem")
	public ResponseEntity<String> addCartItem(@Validated @RequestBody CartItemDto cartItem, Errors errors) {

		if (errors.hasErrors()) {
			Error error = new Error("The data is not validated!");
			throw new DataNotValidatedException(error);
		}

		cartItemService.addCartItem(cartItem);
		return ResponseEntity.ok("The item has been placed in the cart");
	}

	@GetMapping(value = "/getMyCart")
	public ResponseEntity<List<CartItemDto>> getMyItems() {
		CustomerDto currentCustomer = customerService.getCurrentCustomer();
		List<CartItemDto> allCartItems = cartItemService.listAllByCartId(currentCustomer.getCartId());
		return ResponseEntity.ok().body(allCartItems);
	}

	@DeleteMapping(value = "/clearCart")
	public ResponseEntity<String> clearCart() {
		CustomerDto customer = customerService.getCurrentCustomer();
		cartItemService.eraseAllCartItems(customer.getCartId());
		return ResponseEntity.ok().body("All cart items have been removed!");
	}

	@DeleteMapping(value = "/removeCartItem/{itemId}")
	public ResponseEntity<String> deleteItem(@PathVariable("itemId") Integer itemId) {
		cartItemService.removeCartItem(itemId);
		return ResponseEntity.ok().body("The cart item has been removed!");
	}

	@GetMapping(value = "/getCartById/{cartId}")
	public ResponseEntity<CartDto> getCartById(@PathVariable("cartId") Integer cartId) {
		CartDto cart = cartService.getCartByCartId(cartId);
		return ResponseEntity.ok().body(cart);
	}
	
	

}
