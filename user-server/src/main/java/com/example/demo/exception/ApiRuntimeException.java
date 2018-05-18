package com.example.demo.exception;

import org.springframework.http.HttpStatus;

public class ApiRuntimeException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HttpStatus status;
	 
    public ApiRuntimeException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

	public HttpStatus getStatus() {
		return status;
	}
}
