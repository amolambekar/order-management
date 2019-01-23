package com.cs.ordermanagement.exception;

public class OrderBookManagementException extends Exception {
	
	private String message;

	
	public OrderBookManagementException(Throwable cause) {
		this.message=cause.getMessage();
		
	}
	
	public OrderBookManagementException(String message) {
		this.message=message;
	}

	public String getExceptionMessage() {
		return this.message;
	}

	private static final long serialVersionUID = 1L;

}
