/**
 * 
 */
package com.informatica.github.searchrepo.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * to send error response to user
 * @author Mahalakshmi
 *
 */
@Data
public class ErrorResponse {
	/** current timestamp */
	private LocalDateTime timestamp = LocalDateTime.now();
	/** message to user */
	private String message;
	/** list of errors to user */
	private List<String> errors;
	
	public void addError(String error) {
		if (errors == null) {
			errors = new ArrayList<>();
		}
		errors.add(error);
	}
	

}
