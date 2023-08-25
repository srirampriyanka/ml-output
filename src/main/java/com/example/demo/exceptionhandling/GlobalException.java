package com.example.demo.exceptionhandling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalException {

	@ExceptionHandler
	public ResponseEntity<ErrorObject> handleIncorrectInputException(IncorrectInputException ex) {
		ErrorObject error = new ErrorObject(HttpStatus.BAD_REQUEST.value(), ex.getMessage(),
				System.currentTimeMillis());
		return new ResponseEntity<ErrorObject>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler
	public ResponseEntity<ErrorObject> handleCorrupedJsonException(CorruptedJsonException ex) {
		ErrorObject error = new ErrorObject(HttpStatus.BAD_REQUEST.value(), ex.getMessage(),
				System.currentTimeMillis());
		return new ResponseEntity<ErrorObject>(error, HttpStatus.BAD_REQUEST);
	}
}
