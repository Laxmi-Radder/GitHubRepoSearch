/**
 * 
 */
package com.informatica.github.searchrepo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.informatica.github.searchrepo.exception.EmptyResponeException;
import com.informatica.github.searchrepo.model.SearchRepoResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * It calls GitHub Search repository API 
 * refer : https://docs.github.com/en/rest/reference/search#search-repositories
 * @author Mahalakshmi
 *
 */
@Service
@Slf4j
public class SearchRepoService {
	
	/** Language query param*/
	private static final String LANGUAGE_QUERY_PARAN_NAME = "q";
	
	/** LANGUAGE_QUERY_PARAM_VALUE	 */
	private static final String LANGUAGE_QUERY_PARAM_VALUE = "language:";
	
	/** PER_PAGE_QUERY_PARAM_NAME */
	private static final String PER_PAGE_QUERY_PARAM_NAME = "per_page";
	
	/** PAGE_QUERY_PARAM_NAME */
	private static final String PAGE_QUERY_PARAM_NAME = "page";

	/** rest temaplete used to call GitHub API */
	@Autowired
	private RestTemplate restTemplate;
	
	/** common headers with authentication details */
	@Autowired
	@Qualifier("requestHeaders")
	private HttpHeaders requestHeaders;
	
	@Autowired
	@Qualifier("responseHeaders")
	private HttpHeaders responseHeaders;
	
	/** GitHub Rest API URL */
	@Value("${github.search.repo.url}")
	private String gitHubUrl;
	
	/** per page records */
	@Value("${github.max.records.per.page:100}")
	private int maxRecordsPerPage;
	
	/**
	 * This will make call to GitHub Repository search API and constructs final response
	 * @param language
	 * @return
	 */
	public ResponseEntity<SearchRepoResponse> getGitHubRepo(String language, Integer recordsPerPage, Integer pageNumber) {
		
		UriComponentsBuilder url = UriComponentsBuilder.fromHttpUrl(gitHubUrl)
				.queryParam(LANGUAGE_QUERY_PARAN_NAME, LANGUAGE_QUERY_PARAM_VALUE+language);
		if (recordsPerPage != null && recordsPerPage > 0 ) {
			url.queryParam(PER_PAGE_QUERY_PARAM_NAME, recordsPerPage);
		}
		if (pageNumber != null && pageNumber > 0) {
			url.queryParam(PAGE_QUERY_PARAM_NAME, pageNumber);
		}
				
		log.debug("Calling GitHub API ", url.build().toString());
		
		ResponseEntity<SearchRepoResponse> response = restTemplate.exchange(url.build().toString(), HttpMethod.GET, new HttpEntity<>(null, requestHeaders), SearchRepoResponse.class);
		if (response != null) {
			// sometimes GitHub sends empty response headers which may cause issues while parsing response at client side
			return ResponseEntity.status(response.getStatusCode()).headers(responseHeaders).body(response.getBody());
		} else {
			throw new EmptyResponeException("Null response returned from GitHub");
		}
	}	
	
	
}
