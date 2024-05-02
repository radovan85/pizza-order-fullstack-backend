package com.radovan.spring.controller;

import javax.security.auth.login.CredentialNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.radovan.spring.exceptions.DataNotValidatedException;
import com.radovan.spring.exceptions.ExistingEmailException;
import com.radovan.spring.exceptions.ExistingPizzaSizeException;
import com.radovan.spring.exceptions.InstanceUndefinedException;
import com.radovan.spring.exceptions.InvalidCartException;
import com.radovan.spring.exceptions.InvalidUserException;
import com.radovan.spring.exceptions.OperationNotAllowedException;
import com.radovan.spring.exceptions.SuspendedUserException;

@RestControllerAdvice
public class ErrorsController {

	@ExceptionHandler(DataNotValidatedException.class)
	public ResponseEntity<String> handleDataNotValidatedException(Error error) {
		return new ResponseEntity<String>(error.getMessage(), HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(ExistingEmailException.class)
	public ResponseEntity<String> handleExistingEmailException(Error error) {
		return new ResponseEntity<>(error.getMessage(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(InstanceUndefinedException.class)
	public ResponseEntity<String> handleInstanceUndefinedException(Error error) {
		return new ResponseEntity<String>(error.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler(InvalidUserException.class)
	public ResponseEntity<String> handleInvalidUserException(Error error) {
		return new ResponseEntity<String>(error.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(SuspendedUserException.class)
	public ResponseEntity<String> handleSuspendedUserException(Error error) {
		return new ResponseEntity<String>(error.getMessage(), HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS);
	}

	@ExceptionHandler(ExistingPizzaSizeException.class)
	public ResponseEntity<String> handleExistingPizzaSizeException(Error error) {
		return new ResponseEntity<String>(error.getMessage(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(OperationNotAllowedException.class)
	public ResponseEntity<String> handleOperationNotAllowedException(Error error) {
		return new ResponseEntity<String>(error.getMessage(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(InvalidCartException.class)
	public ResponseEntity<String> handleInvalidCartException(Error error) {
		return new ResponseEntity<String>(error.getMessage(), HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(CredentialNotFoundException.class)
	public ResponseEntity<String> handleCredentialNotFoundException(CredentialNotFoundException exc) {
		return new ResponseEntity<String>(exc.getMessage(), HttpStatus.NOT_ACCEPTABLE);
	}

}
