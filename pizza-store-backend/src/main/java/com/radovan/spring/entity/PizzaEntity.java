package com.radovan.spring.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "pizzas")
public class PizzaEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "pizza_id")
	private Integer pizzaId;

	@Column(nullable = false, length = 40)
	private String name;

	@Column(nullable = false, length = 90)
	private String description;

	@Transient
	@OneToMany(mappedBy = "pizza", orphanRemoval = true, fetch = FetchType.EAGER)
	private List<PizzaSizeEntity> pizzaSizes;

	@Column(name = "image_url", nullable = false, length = 255)
	private String imageUrl;

	public Integer getPizzaId() {
		return pizzaId;
	}

	public void setPizzaId(Integer pizzaId) {
		this.pizzaId = pizzaId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<PizzaSizeEntity> getPizzaSizes() {
		return pizzaSizes;
	}

	public void setPizzaSizes(List<PizzaSizeEntity> pizzaSizes) {
		this.pizzaSizes = pizzaSizes;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

}
