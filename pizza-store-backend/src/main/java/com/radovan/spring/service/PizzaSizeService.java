package com.radovan.spring.service;

import java.util.List;

import com.radovan.spring.dto.PizzaSizeDto;

public interface PizzaSizeService {

	PizzaSizeDto addPizzaSize(PizzaSizeDto pizzaSize);

	PizzaSizeDto getPizzaSizeById(Integer pizzaSizeId);

	void deletePizzaSize(Integer pizzaSizeId);

	List<PizzaSizeDto> listAll();

	List<PizzaSizeDto> listAllByPizzaId(Integer pizzaId);

	PizzaSizeDto updatePizzaSize(Integer sizeId, PizzaSizeDto pizzaSize);

}
