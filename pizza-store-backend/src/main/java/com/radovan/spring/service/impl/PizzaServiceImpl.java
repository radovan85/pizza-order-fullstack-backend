package com.radovan.spring.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.CartDto;
import com.radovan.spring.dto.PizzaDto;
import com.radovan.spring.dto.PizzaSizeDto;
import com.radovan.spring.entity.PizzaEntity;
import com.radovan.spring.exceptions.InstanceUndefinedException;
import com.radovan.spring.repository.PizzaRepository;
import com.radovan.spring.service.CartService;
import com.radovan.spring.service.PizzaService;
import com.radovan.spring.service.PizzaSizeService;

@Service
public class PizzaServiceImpl implements PizzaService {

	@Autowired
	private PizzaRepository pizzaRepository;

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private PizzaSizeService pizzaSizeService;

	@Autowired
	private CartService cartService;

	@Override
	@Transactional(readOnly = true)
	public List<PizzaDto> listAll() {
		// TODO Auto-generated method stub
		List<PizzaDto> returnValue = new ArrayList<>();
		List<PizzaEntity> allPizzas = pizzaRepository.findAll();
		if (!allPizzas.isEmpty()) {
			allPizzas.forEach((pizzaEntity) -> {
				PizzaDto pizzaDto = tempConverter.pizzaEntityToDto(pizzaEntity);
				returnValue.add(pizzaDto);
			});
		}

		return returnValue;
	}

	@Override
	@Transactional(readOnly = true)
	public PizzaDto getPizzaById(Integer pizzaId) {
		// TODO Auto-generated method stub
		PizzaDto returnValue = null;
		Optional<PizzaEntity> pizzaOptional = pizzaRepository.findById(pizzaId);
		if (pizzaOptional.isPresent()) {
			returnValue = tempConverter.pizzaEntityToDto(pizzaOptional.get());
		} else {
			Error error = new Error("The pizza has not been found!");
			throw new InstanceUndefinedException(error);
		}

		return returnValue;
	}

	@Override
	@Transactional
	public void deletePizza(Integer pizzaId) {
		// TODO Auto-generated method stub
		PizzaDto pizza = getPizzaById(pizzaId);
		List<PizzaSizeDto> allSizes = pizzaSizeService.listAllByPizzaId(pizzaId);
		allSizes.forEach((pizzaSize) -> {
			pizzaSizeService.deletePizzaSize(pizzaSize.getPizzaSizeId());
		});
		pizzaRepository.deleteById(pizza.getPizzaId());
		pizzaRepository.flush();

		List<CartDto> allCarts = cartService.listAll();
		if (!allCarts.isEmpty()) {
			allCarts.forEach((cart) -> {
				cartService.refreshCartState(cart.getCartId());
			});
		}
	}

	@Override
	@Transactional
	public PizzaDto addPizza(PizzaDto pizza) {
		// TODO Auto-generated method stub
		PizzaEntity pizzaEntity = tempConverter.pizzaDtoToEntity(pizza);
		PizzaEntity storedPizza = pizzaRepository.save(pizzaEntity);
		PizzaDto returnValue = tempConverter.pizzaEntityToDto(storedPizza);
		return returnValue;
	}

	@Override
	@Transactional
	public PizzaDto updatePizza(Integer pizzaId, PizzaDto pizza) {
		// TODO Auto-generated method stub
		PizzaDto returnValue = null;
		Optional<PizzaEntity> pizzaOptional = pizzaRepository.findById(pizzaId);
		if (pizzaOptional.isPresent()) {
			PizzaEntity pizzaEntity = tempConverter.pizzaDtoToEntity(pizza);
			pizzaEntity.setPizzaId(pizzaId);
			pizzaEntity.setPizzaSizes(pizzaOptional.get().getPizzaSizes());
			PizzaEntity updatedPizza = pizzaRepository.saveAndFlush(pizzaEntity);
			returnValue = tempConverter.pizzaEntityToDto(updatedPizza);
		} else {
			Error error = new Error("The pizza has not been found");
			throw new InstanceUndefinedException(error);
		}

		return returnValue;
	}

}
