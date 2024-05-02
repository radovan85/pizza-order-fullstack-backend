package com.radovan.spring.exceptions;

import javax.management.RuntimeErrorException;

public class OperationNotAllowedException extends RuntimeErrorException {

	public OperationNotAllowedException(Error e) {
		super(e);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
