package com.informatica.github.searchrepo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * It contains complete repository information.
 * @author Mahalakshmi
 *
 */
@Data
public class Repository {

    /** Repository ID */
	@JsonProperty(value = "id")
    private String id;
    
    /** Repository Name */
	@JsonProperty
    private String name;
    
    /** Repository URL */
	@JsonProperty
    private String url;
    
    /** Owner of Repository */
	@JsonProperty
    private Owner owner;
}
