package com.radovan.spring.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cart_items")
public class CartItemEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id")
	private Integer cartItemId;

	@Column(nullable = false)
	private Integer quantity;

	@Column(nullable = false)
	private Float price;

	@ManyToOne
	@JoinColumn(name = "size_id", nullable = false)
	private PizzaSizeEntity pizzaSize;

	@ManyToOne
	@JoinColumn(name = "cart_id", nullable = false)
	private CartEntity cart;

	public Integer getCartItemId() {
		return cartItemId;
	}

	public void setCartItemId(Integer cartItemId) {
		this.cartItemId = cartItemId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public CartEntity getCart() {
		return cart;
	}

	public void setCart(CartEntity cart) {
		this.cart = cart;
	}

	public PizzaSizeEntity getPizzaSize() {
		return pizzaSize;
	}

	public void setPizzaSize(PizzaSizeEntity pizzaSize) {
		this.pizzaSize = pizzaSize;
	}

}
