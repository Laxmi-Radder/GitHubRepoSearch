/**
 * 
 */
package com.informatica.github.searchrepo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * This holds Repo Owner details for now it has only 1 field
 *  but in future we could add more fields
 * @author Mahalakshmi
 *
 */
@Data
public class Owner {

	@JsonProperty
	private String login;
}
