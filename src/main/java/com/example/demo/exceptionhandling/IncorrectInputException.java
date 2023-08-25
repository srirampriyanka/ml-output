package com.example.demo.exceptionhandling;

public class IncorrectInputException extends RuntimeException {
	public IncorrectInputException(String message) {
		super(message);
	}
}
