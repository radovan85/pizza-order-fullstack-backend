package com.radovan.spring.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
public class CustomerEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer customerId;

	@Column(name = "phone", nullable = false, length = 15)
	private String customerPhone;

	@OneToOne(fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "shipping_address_id", nullable = false)
	private ShippingAddressEntity shippingAddress;

	@OneToOne(fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;

	@OneToOne(fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "cart_id", nullable = false)
	private CartEntity cart;

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

	public ShippingAddressEntity getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(ShippingAddressEntity shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public CartEntity getCart() {
		return cart;
	}

	public void setCart(CartEntity cart) {
		this.cart = cart;
	}

}
