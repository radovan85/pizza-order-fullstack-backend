package com.radovan.spring.controller;

import java.util.Optional;

import javax.security.auth.login.CredentialNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.UserDto;
import com.radovan.spring.entity.UserEntity;
import com.radovan.spring.exceptions.DataNotValidatedException;
import com.radovan.spring.service.CustomerService;
import com.radovan.spring.service.UserService;
import com.radovan.spring.utils.AuthenticationRequest;
import com.radovan.spring.utils.JwtUtil;
import com.radovan.spring.utils.RegistrationForm;

@RestController
public class MainController {

	@Autowired
	private UserService userService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private JwtUtil jwtTokenUtil;

	@Autowired
	private TempConverter tempConverter;

	@PostMapping(value = "/register")
	public ResponseEntity<String> registerUser(@Validated @RequestBody RegistrationForm form, Errors errors) {

		if (errors.hasErrors()) {
			Error error = new Error("The data has not been validated");
			throw new DataNotValidatedException(error);
		}

		customerService.storeCustomer(form);

		return new ResponseEntity<>("Registration completed!",HttpStatus.OK);
	}

	@PostMapping("/login")
	public ResponseEntity<UserDto> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest,
			Errors errors) throws Exception {

		Optional<Authentication> authOptional = userService.authenticateUser(authenticationRequest.getUsername(),
				authenticationRequest.getPassword());
		if (authOptional.isEmpty()) {
			throw new CredentialNotFoundException("Invalid username or password!");
		}

		UserDto userDto = userService.getUserByEmail(authenticationRequest.getUsername());
		final UserEntity userDetails = tempConverter.userDtoToEntity(userDto);

		final String jwt = jwtTokenUtil.generateToken(userDetails);

		UserDto authUser = tempConverter.userEntityToDto(userDetails);
		authUser.setAuthToken(jwt);

		return new ResponseEntity<>(authUser,HttpStatus.OK);

	}

}
