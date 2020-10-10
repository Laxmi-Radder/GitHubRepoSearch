/**
 * 
 */
package com.informatica.github.searchrepo.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.informatica.github.searchrepo.exception.EmptyResponeException;
import com.informatica.github.searchrepo.model.ErrorResponse;
import com.informatica.github.searchrepo.model.SearchRepoResponse;
import com.informatica.github.searchrepo.service.SearchRepoService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahalakshmi
 *
 */
@RestController
@Slf4j
public class SearchRepoController {
	
	private static final String BAD_REQUEST = "Bad Request";

	//constants for error message
	public static final String ONLY_THE_FIRST_1000_SEARCH_RESULTS_ARE_AVAILABLE_FROM_GIT_HUB = "Only the first 1000 search results are available from GitHub";

	public static final String RECORDS_PER_PAGE_VALUE_SHOULD_NOT_EXCEED = "records_per_page value should not exceed ";

	public static final String LANGUAGE_PARAMETER_VALUE_IS_MANDATORY = "language parameter value is mandatory";

	@Autowired
	private SearchRepoService service;
	
	/** Per page records */
	@Value("${github.max.records.per.page:100}")
	private int maxRecordsPerPage;
	
	@ApiOperation(value = "Get list of repositories in GitHub ", response = SearchRepoResponse.class, tags = "search-repo-controller")
	@ApiResponses(value = { 
	            @ApiResponse(code = 200, message = "Success|OK"),
	            @ApiResponse(code = 204, message = "Null or Empty Response from GitHub"),
	            @ApiResponse(code = 400, message = BAD_REQUEST), 
	            @ApiResponse(code = 401, message = "Not Authorized"),
	            @ApiResponse(code = 403, message = "Forbidden"),
	            @ApiResponse(code = 404, message = "Not Found")})
	@RequestMapping(value = "/search/repositories", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
	public ResponseEntity<?> searchGitHubRepo(@RequestParam(name = "language") String language,
						@RequestParam(name = "records_per_page", required = false) Integer recordsPerPage,
						@RequestParam(name = "page", required = false) Integer page) {
		log.info("Processing request for language:" + language+ (recordsPerPage==null ? "" : " recordsPerPage :"+recordsPerPage)+(page==null ? "" : " page number :"+page) );
		//validate request
		ResponseEntity<?> validationResponse = validateRequest(language, recordsPerPage, page);
		if (validationResponse != null) {
			return validationResponse;
		}
		return service.getGitHubRepo(language, recordsPerPage, page);
	}

	/**
	 * Validate request params
	 * @param language
	 * @param numberOfRecordsPerPage
	 * @param page
	 * @return error response
	 */
	private ResponseEntity<ErrorResponse> validateRequest(String language, Integer numberOfRecordsPerPage, Integer page) {
		List<String> errorMessages = new ArrayList<>();
		if (StringUtils.isEmpty(language)) {
			//GitHub will not allow so
			errorMessages.add(LANGUAGE_PARAMETER_VALUE_IS_MANDATORY);
		}
		if (numberOfRecordsPerPage != null && numberOfRecordsPerPage > maxRecordsPerPage ) {
			//GitHub will not allow so
			errorMessages.add(RECORDS_PER_PAGE_VALUE_SHOULD_NOT_EXCEED+maxRecordsPerPage);
		}
		if (numberOfRecordsPerPage != null && page != null && (numberOfRecordsPerPage * page) > 1000 ) {
			//GitHub will not allow so
			errorMessages.add(ONLY_THE_FIRST_1000_SEARCH_RESULTS_ARE_AVAILABLE_FROM_GIT_HUB);
		}
		if (errorMessages.isEmpty()) {
			log.debug("Validation success");
			// no errors so no error response
			return null;
		}
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setMessage(BAD_REQUEST);
		errorResponse.setErrors(errorMessages);
		log.error("Request validation falied with error {} ",errorMessages);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	/**
	 * When HttpClientErrorException is thrown, construct the error response
	 * with same Status code as in exception
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = HttpClientErrorException.class)
	public ResponseEntity<ErrorResponse> getErrorResponse(HttpClientErrorException ex) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setMessage(ex.getMessage());
		errorResponse.addError(ex.getResponseBodyAsString());
		log.error("Failed with error {} ", ex);
		return new ResponseEntity<>(errorResponse, ex.getStatusCode());
	}
	/**
	 * EmptyResponeException is thrown, construct empty response body with NO_CONTENT
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = EmptyResponeException.class)
	public ResponseEntity<ErrorResponse> getErrorResponse(EmptyResponeException ex) {
		log.error("Failed with message {} ", ex.getMessage());
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	/**
	 * Any other Exceptions should send error response with status INTERNAL_SERVER_ERROR
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<ErrorResponse> getErrorResponse(Exception ex) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setMessage(ex.getMessage());
		log.error("Failed with error {} ", ex);
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
