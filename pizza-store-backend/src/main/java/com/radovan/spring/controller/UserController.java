package com.radovan.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.radovan.spring.dto.UserDto;
import com.radovan.spring.service.UserService;

@RestController
@RequestMapping(value = "/api/users")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping(value = "/currentUser")
	public ResponseEntity<UserDto> getCurrentUser() {
		UserDto currentUser = userService.getCurrentUser();
		return ResponseEntity.ok().body(currentUser);
	}
}
