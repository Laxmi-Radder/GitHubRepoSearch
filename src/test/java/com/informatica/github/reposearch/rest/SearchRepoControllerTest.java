/**
 * 
 */
package com.informatica.github.reposearch.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import com.informatica.github.searchrepo.model.ErrorResponse;
import com.informatica.github.searchrepo.model.Repository;
import com.informatica.github.searchrepo.model.SearchRepoResponse;
import com.informatica.github.searchrepo.rest.SearchRepoController;
import com.informatica.github.searchrepo.service.SearchRepoService;

/**
 * Test SearchRepoController
 * @author Mahalakshmi
 *
 */
public class SearchRepoControllerTest {
	
	@Mock
	private SearchRepoService service;
	
	@InjectMocks
	private SearchRepoController controller = new SearchRepoController();
	
	private int maxRecordsPerPage = 10;
	
	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		ReflectionTestUtils.setField(controller, "maxRecordsPerPage", maxRecordsPerPage);
	}
	
	@Test
	public void testSearchGitHubRepoValidateLanguage() {
		ResponseEntity<?> response = controller.searchGitHubRepo(null, 10, 10);
		assertNotNull(response, "Null Response");
		assertNotNull(response.getBody(), "Null Response body");
		ErrorResponse errorResponse = (ErrorResponse) response.getBody();
		assertNotNull(errorResponse.getMessage(), "Error Message is null");
		assertNotNull(errorResponse.getErrors(), "Null Erros list");
		assertEquals(1, errorResponse.getErrors().size(), "Error count not equals to 1");
		assertEquals(SearchRepoController.LANGUAGE_PARAMETER_VALUE_IS_MANDATORY, errorResponse.getErrors().get(0), "Error message not matching");
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	public void testSearchGitHubRepoValidateRecordsParam() {
		ResponseEntity<?> response = controller.searchGitHubRepo("Java", 13, null);
		assertNotNull(response, "Null Response");
		assertNotNull(response.getBody(), "Null Response body");
		ErrorResponse errorResponse = (ErrorResponse) response.getBody();
		assertNotNull(errorResponse.getMessage(),"Error Message is null");
		assertNotNull(errorResponse.getErrors());
		assertEquals(1, errorResponse.getErrors().size(), "Error count not equals to 1");
		assertEquals(SearchRepoController.RECORDS_PER_PAGE_VALUE_SHOULD_NOT_EXCEED+maxRecordsPerPage, errorResponse.getErrors().get(0), "Error message not matching");
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	
	@Test
	public void testSearchGitHubRepoValidateTotalRecors() {
		ResponseEntity<?> response = controller.searchGitHubRepo("Java", 10, 10001);
		assertNotNull(response, "Null Response");
		assertNotNull(response.getBody(), "Null Response body");
		ErrorResponse errorResponse = (ErrorResponse) response.getBody();
		assertNotNull(errorResponse.getMessage(), "Error Message is null");
		assertNotNull(errorResponse.getErrors(), "Errors list is null");
		assertEquals(1, errorResponse.getErrors().size(), "Error count not equals to 1");
		assertEquals(SearchRepoController.ONLY_THE_FIRST_1000_SEARCH_RESULTS_ARE_AVAILABLE_FROM_GIT_HUB, errorResponse.getErrors().get(0), "Error message not matching");
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	public void testSearchGitHubRepoValidateMultipleErrors() {
		ResponseEntity<?> response = controller.searchGitHubRepo("Java", 13, 1001);
		assertNotNull(response, "Null Response");
		assertNotNull(response.getBody(), "Null Response body");
		ErrorResponse errorResponse = (ErrorResponse) response.getBody();
		assertNotNull(errorResponse.getMessage(), "Error Message is null");
		assertNotNull(errorResponse.getErrors(), "Errors List is null");
		assertEquals(2, errorResponse.getErrors().size(), "Error count not equals to 2");
		assertTrue(errorResponse.getErrors().contains(SearchRepoController.ONLY_THE_FIRST_1000_SEARCH_RESULTS_ARE_AVAILABLE_FROM_GIT_HUB));
		assertTrue(errorResponse.getErrors().contains(SearchRepoController.RECORDS_PER_PAGE_VALUE_SHOULD_NOT_EXCEED+maxRecordsPerPage));
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	public void testSearchGitHubRepoSuccessResponse() {
		Mockito.when(service.getGitHubRepo(Mockito.any(String.class), Mockito.any(Integer.class), Mockito.any(Integer.class))).thenReturn(getSucessResponse());
		ResponseEntity<?> response = controller.searchGitHubRepo("Java", 1, 10);
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
	
	
	private ResponseEntity<SearchRepoResponse> getSucessResponse() {
		SearchRepoResponse searchRepoResponse = new SearchRepoResponse();
		searchRepoResponse.setTotalCount(1);
		searchRepoResponse.setIncompleteResult(false);
		searchRepoResponse.addRepositorie(new Repository());
		return ResponseEntity.ok(searchRepoResponse);
		
	}
	

}
