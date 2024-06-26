package com.radovan.spring.dto;

import java.io.Serializable;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class CartDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer cartId;

	@NotNull
	private Integer customerId;

	private List<Integer> cartItemsIds;

	@NotNull
	@DecimalMin(value = "0.00")
	private Float cartPrice;

	public Integer getCartId() {
		return cartId;
	}

	public void setCartId(Integer cartId) {
		this.cartId = cartId;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public List<Integer> getCartItemsIds() {
		return cartItemsIds;
	}

	public void setCartItemsIds(List<Integer> cartItemsIds) {
		this.cartItemsIds = cartItemsIds;
	}

	public Float getCartPrice() {
		return cartPrice;
	}

	public void setCartPrice(Float cartPrice) {
		this.cartPrice = cartPrice;
	}

}
