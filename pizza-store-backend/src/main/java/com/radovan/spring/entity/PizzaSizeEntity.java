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
@Table(name = "pizza_sizes")
public class PizzaSizeEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "pizza_size_id")
	private Integer pizzaSizeId;

	@Column(nullable = false, length = 40)
	private String name;

	@Column(nullable = false)
	private Float price;

	@ManyToOne
	@JoinColumn(name = "pizza_id",nullable = false)
	private PizzaEntity pizza;

	public Integer getPizzaSizeId() {
		return pizzaSizeId;
	}

	public void setPizzaSizeId(Integer pizzaSizeId) {
		this.pizzaSizeId = pizzaSizeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public PizzaEntity getPizza() {
		return pizza;
	}

	public void setPizza(PizzaEntity pizza) {
		this.pizza = pizza;
	}

}
