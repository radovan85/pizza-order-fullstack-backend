package com.radovan.spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.radovan.spring.dto.PizzaDto;
import com.radovan.spring.dto.PizzaSizeDto;
import com.radovan.spring.service.PizzaService;
import com.radovan.spring.service.PizzaSizeService;

@RestController
@RequestMapping(value = "/api/pizza")
public class PizzaController {

	@Autowired
	private PizzaService pizzaService;

	@Autowired
	private PizzaSizeService pizzaSizeService;

	@GetMapping(value = "/allPizzas")
	public ResponseEntity<List<PizzaDto>> getAllPizzas() {
		List<PizzaDto> allPizzas = pizzaService.listAll();
		return ResponseEntity.ok().body(allPizzas);
	}

	@GetMapping(value = "/allPizzaSizes")
	public ResponseEntity<List<PizzaSizeDto>> getAllPizzaSizes() {
		List<PizzaSizeDto> allPizzaSizes = pizzaSizeService.listAll();
		return ResponseEntity.ok().body(allPizzaSizes);
	}

	@GetMapping(value = "/pizzaDetails/{pizzaId}")
	public ResponseEntity<PizzaDto> getPizzaDetails(@PathVariable("pizzaId") Integer pizzaId) {
		PizzaDto pizza = pizzaService.getPizzaById(pizzaId);
		return ResponseEntity.ok().body(pizza);
	}

	@GetMapping(value = "/pizzaSizeDetails/{sizeId}")
	public ResponseEntity<PizzaSizeDto> getPizzaSizeDetails(@PathVariable("sizeId") Integer sizeId) {
		PizzaSizeDto pizzaSize = pizzaSizeService.getPizzaSizeById(sizeId);
		return ResponseEntity.ok().body(pizzaSize);
	}

	@GetMapping(value = "/allPizzaSizes/{pizzaId}")
	public ResponseEntity<List<PizzaSizeDto>> allPizzaSizesByPizza(@PathVariable("pizzaId") Integer pizzaId) {
		List<PizzaSizeDto> allPizzaSizes = pizzaSizeService.listAllByPizzaId(pizzaId);
		return ResponseEntity.ok().body(allPizzaSizes);
	}

}
