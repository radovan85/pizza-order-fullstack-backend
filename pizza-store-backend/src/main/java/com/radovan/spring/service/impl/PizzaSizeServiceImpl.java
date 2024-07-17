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
import com.radovan.spring.entity.CartItemEntity;
import com.radovan.spring.entity.PizzaSizeEntity;
import com.radovan.spring.exceptions.ExistingInstanceException;
import com.radovan.spring.exceptions.InstanceUndefinedException;
import com.radovan.spring.repository.CartItemRepository;
import com.radovan.spring.repository.PizzaSizeRepository;
import com.radovan.spring.service.CartService;
import com.radovan.spring.service.PizzaService;
import com.radovan.spring.service.PizzaSizeService;

@Service
public class PizzaSizeServiceImpl implements PizzaSizeService {

	@Autowired
	private PizzaSizeRepository pizzaSizeRepository;

	@Autowired
	private PizzaService pizzaService;

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private CartService cartService;

	@Autowired
	private TempConverter tempConverter;

	@Override
	@Transactional
	public PizzaSizeDto addPizzaSize(PizzaSizeDto pizzaSize) {
		// TODO Auto-generated method stub

		pizzaService.getPizzaById(pizzaSize.getPizzaId());

		Optional<PizzaSizeEntity> pizzaSizeOptional = pizzaSizeRepository.findByNameAndPizzaId(pizzaSize.getName(),
				pizzaSize.getPizzaId());
		if (pizzaSizeOptional.isPresent()) {
			Error error = new Error("Pizza size already exists!");
			throw new ExistingInstanceException(error);
		}

		PizzaSizeEntity sizeEntity = tempConverter.pizzaSizeDtoToEntity(pizzaSize);
		PizzaSizeEntity storedSize = pizzaSizeRepository.save(sizeEntity);
		PizzaSizeDto returnValue = tempConverter.pizzaSizeEntityToDto(storedSize);
		return returnValue;
	}

	@Override
	@Transactional(readOnly = true)
	public PizzaSizeDto getPizzaSizeById(Integer pizzaSizeId) {
		// TODO Auto-generated method stub
		PizzaSizeDto returnValue = null;
		Optional<PizzaSizeEntity> pizzaSizeOptional = pizzaSizeRepository.findById(pizzaSizeId);
		if (pizzaSizeOptional.isPresent()) {
			returnValue = tempConverter.pizzaSizeEntityToDto(pizzaSizeOptional.get());
		} else {
			Error error = new Error("Pizza size has not been found!");
			throw new InstanceUndefinedException(error);
		}

		return returnValue;
	}

	@Override
	@Transactional
	public void deletePizzaSize(Integer pizzaSizeId) {
		// TODO Auto-generated method stub
		PizzaSizeDto pizzaSize = getPizzaSizeById(pizzaSizeId);
		cartItemRepository.removeAllByPizzaSizeId(pizzaSizeId);
		pizzaSizeRepository.deleteById(pizzaSize.getPizzaSizeId());
		pizzaSizeRepository.flush();

		List<CartDto> allCarts = cartService.listAll();
		if (!allCarts.isEmpty()) {
			allCarts.forEach((cart) -> {
				cartService.refreshCartState(cart.getCartId());
			});
		}

	}

	@Override
	@Transactional(readOnly = true)
	public List<PizzaSizeDto> listAll() {
		// TODO Auto-generated method stub
		List<PizzaSizeDto> returnValue = new ArrayList<>();
		List<PizzaSizeEntity> allPizzaSizes = pizzaSizeRepository.findAll();
		if (!allPizzaSizes.isEmpty()) {
			allPizzaSizes.forEach((pizzaSizeEntity) -> {
				PizzaSizeDto pizzaSize = tempConverter.pizzaSizeEntityToDto(pizzaSizeEntity);
				returnValue.add(pizzaSize);
			});
		}

		return returnValue;
	}

	@Override
	@Transactional(readOnly = true)
	public List<PizzaSizeDto> listAllByPizzaId(Integer pizzaId) {
		// TODO Auto-generated method stub
		List<PizzaSizeDto> returnValue = new ArrayList<>();
		List<PizzaSizeEntity> allPizzaSizes = pizzaSizeRepository.findAllByPizzaId(pizzaId);
		if (!allPizzaSizes.isEmpty()) {
			allPizzaSizes.forEach((pizzaSizeEntity) -> {
				PizzaSizeDto pizzaSize = tempConverter.pizzaSizeEntityToDto(pizzaSizeEntity);
				returnValue.add(pizzaSize);
			});
		}

		return returnValue;
	}

	@Override
	@Transactional
	public PizzaSizeDto updatePizzaSize(Integer sizeId, PizzaSizeDto pizzaSize) {
		// TODO Auto-generated method stub
		PizzaSizeDto returnValue = null;

		Optional<PizzaSizeEntity> pizzaSizeOptional = pizzaSizeRepository.findById(sizeId);
		if (pizzaSizeOptional.isPresent()) {

			PizzaDto pizza = pizzaService.getPizzaById(pizzaSize.getPizzaId());

			Optional<PizzaSizeEntity> pizzaSizeOptional2 = pizzaSizeRepository.findByNameAndPizzaId(pizzaSize.getName(),
					pizza.getPizzaId());
			if (pizzaSizeOptional2.isPresent()) {
				if (pizzaSizeOptional2.get().getPizzaSizeId() != sizeId) {
					Error error = new Error("Pizza size already exists!");
					throw new ExistingInstanceException(error);
				}
			}

			PizzaSizeEntity pizzaSizeEntity = tempConverter.pizzaSizeDtoToEntity(pizzaSize);
			pizzaSizeEntity.setPizzaSizeId(sizeId);
			PizzaSizeEntity updatedPizzaSize = pizzaSizeRepository.saveAndFlush(pizzaSizeEntity);
			returnValue = tempConverter.pizzaSizeEntityToDto(updatedPizzaSize);

			Optional<List<CartItemEntity>> cartItemsOptional = Optional
					.ofNullable(cartItemRepository.findAllByPizzaSizeId(sizeId));
			if (!cartItemsOptional.isEmpty()) {
				cartItemsOptional.get().forEach((itemEntity) -> {
					itemEntity.setPrice(itemEntity.getQuantity() * pizzaSize.getPrice());
					cartItemRepository.saveAndFlush(itemEntity);
				});

				List<CartDto> allCarts = cartService.listAll();
				if (!allCarts.isEmpty()) {
					allCarts.forEach((cart) -> {
						cartService.refreshCartState(cart.getCartId());
					});
				}
			}

		} else {
			Error error = new Error("Pizza size has not been found!");
			throw new InstanceUndefinedException(error);
		}

		return returnValue;
	}

}
