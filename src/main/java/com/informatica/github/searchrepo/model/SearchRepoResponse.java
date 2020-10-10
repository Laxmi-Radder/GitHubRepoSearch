/**
 * 
 */
package com.informatica.github.searchrepo.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * This class contains final output of GitHub Repository search
 * @author Mahalakshmi
 */
@Data
@JsonIgnoreProperties
public class SearchRepoResponse {
	/** Total number of records in GitHub */
	@JsonProperty("total_count")
	private int totalCount;
	
	/** To indicate incomplete Results because of timeout */
	@JsonProperty("incomplete_results")
	private boolean incompleteResult;

	/** List repositories from GitHub */
	@JsonProperty("items")
	private List<Repository> repositories;
	
	/**
	 * We can append Repository
	 * @param repos
	 */
	public void addRepositories(List<Repository> repos) {
		if (repositories == null) {
			repositories = new ArrayList<>();
		}
		this.repositories.addAll(repos);
	}
	
	/**
	 * We can append Repository
	 * @param repo
	 */
	public void addRepositorie(Repository repo) {
		if (repositories == null) {
			repositories = new ArrayList<>();
		}
		this.repositories.add(repo);
	}


	
}
