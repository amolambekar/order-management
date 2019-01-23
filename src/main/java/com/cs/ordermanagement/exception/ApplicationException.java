package com.cs.ordermanagement.exception;

public abstract class ApplicationException extends Exception {

	private static final long serialVersionUID = 5205546394006637782L;
	private String message;
	
	public ApplicationException(String message) {
		this.message=message;
	}
	
	public String getMessage() {
		return message;
	}

}
