package com.radovan.spring.exceptions;

import javax.management.RuntimeErrorException;

public class ExistingPizzaSizeException extends RuntimeErrorException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExistingPizzaSizeException(Error e) {
		super(e);
		// TODO Auto-generated constructor stub
	}

}
