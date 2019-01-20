package com.cs.ordermanagement.exception;

public class OrderManagementException extends Exception {
	
	private String message;

	
	public OrderManagementException(Throwable cause) {
		this.message=cause.getMessage();
		
	}
	
	public OrderManagementException(String message) {
		this.message=message;
	}

	public String getExceptionMessage() {
		return this.message;
	}

	private static final long serialVersionUID = 1L;

}
