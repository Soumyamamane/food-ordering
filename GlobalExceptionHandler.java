
package com.example.foodordering.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.foodordering.dto.ResponseStructure;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	// Id Not Found Exception
	@ExceptionHandler(IdNotFoundException.class)
	public ResponseEntity<ResponseStructure<String>> handleIdNotFoundException(IdNotFoundException exception) {

		ResponseStructure<String> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.NOT_FOUND.value());
		res.setMessage(exception.getMessage());
		res.setData("Failure");

		return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
	}

	// No Record Available Exception
	@ExceptionHandler(NoRecordAvailableException.class)
	public ResponseEntity<ResponseStructure<String>> handleNoRecordAvailableException(
			NoRecordAvailableException exception) {

		ResponseStructure<String> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.NOT_FOUND.value());
		res.setMessage(exception.getMessage());
		res.setData("Failure");

		return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
	}

	// Business Exception
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ResponseStructure<String>> handleBusinessException(BusinessException exception) {

		ResponseStructure<String> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.BAD_REQUEST.value());
		res.setMessage(exception.getMessage());
		res.setData("Failure");

		return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
	}
}
