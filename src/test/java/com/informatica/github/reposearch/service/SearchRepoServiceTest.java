/**
 * 
 */
package com.informatica.github.reposearch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.informatica.github.searchrepo.exception.EmptyResponeException;
import com.informatica.github.searchrepo.model.Repository;
import com.informatica.github.searchrepo.model.SearchRepoResponse;
import com.informatica.github.searchrepo.service.SearchRepoService;

/**
 * Test SearchRepoService
 * @author Mahalakshmi
 *
 */
public class SearchRepoServiceTest {
	
	/** rest temaplete used to call GitHub API */
	@Mock
	private RestTemplate restTemplate;
	
	/** common headers with authentication details */
	@Mock
	private HttpHeaders requestHeaders;
	
	@Mock
	private HttpHeaders responseHeaders;
	
	private String gitHubUrl = "http://localhost:8090/";
	

	private int maxRecordsPerPage = 10;
	

	@InjectMocks
	SearchRepoService service = new SearchRepoService();
	

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		ReflectionTestUtils.setField(service, "maxRecordsPerPage", maxRecordsPerPage);
		ReflectionTestUtils.setField(service, "gitHubUrl", gitHubUrl);
	}
	@Test
	public void testGetGitHubRepoSucessResponse() {
		Mockito.when(restTemplate.exchange(Mockito.any(String.class), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), Mockito.any(Class.class))).thenReturn(getSucessResponse());
		ResponseEntity<SearchRepoResponse> response = service.getGitHubRepo("Java", 1, 1);
		assertNotNull(response, "Null Response");
		assertNotNull(response.getBody(), "Null Response body");
		SearchRepoResponse searchRepoResponse = (SearchRepoResponse) response.getBody();
		assertNotNull(searchRepoResponse.getRepositories(), "Null Repositories");
		assertFalse(searchRepoResponse.getRepositories().isEmpty(), "Empty Repositories");
		assertEquals(1, searchRepoResponse.getTotalCount(), "Total results count is more than 1");
		assertEquals(1, searchRepoResponse.getRepositories().size(), "More than one repository found");
		assertFalse(searchRepoResponse.isIncompleteResult(), "incompleteResults is true");
		MatcherAssert.assertThat("Http Status Code", response.getStatusCode(), Matchers.equalTo(HttpStatus.OK));
	}
	
	@Test
	public void testGetGitHubRepoThrowsException() {
		Mockito.when(restTemplate.exchange(Mockito.any(String.class), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), Mockito.any(Class.class))).thenReturn(null);
		Assertions.assertThrows(EmptyResponeException.class, () -> service.getGitHubRepo("Java", 1, 1));
	}
	
	private ResponseEntity<SearchRepoResponse> getSucessResponse() {
		SearchRepoResponse searchRepoResponse = new SearchRepoResponse();
		searchRepoResponse.setTotalCount(1);
		searchRepoResponse.setIncompleteResult(false);
		searchRepoResponse.addRepositorie(new Repository());
		return ResponseEntity.ok(searchRepoResponse);
		
	}
}
