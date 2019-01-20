package com.cs.ordermanagement.exception;

public class ClosedOrderBookException extends Exception {
	
	private String message;

	public ClosedOrderBookException(String message) {
		this.message=message;
	}
	
	public String getExceptionMessage() {
		return this.message;
	}

	private static final long serialVersionUID = -1833492150189964808L;

}
