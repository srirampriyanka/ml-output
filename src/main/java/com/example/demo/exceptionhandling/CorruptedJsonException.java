package com.example.demo.exceptionhandling;

public class CorruptedJsonException extends RuntimeException {
	public CorruptedJsonException(String message) {
		super(message);
	}
}