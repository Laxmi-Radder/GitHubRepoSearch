/**
 * 
 */
package com.informatica.github.searchrepo.config;

import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

/**
 * Responsible for creating all required Beans
 * @author Mahalakshmi
 *
 */
@Configuration
public class BeanConfiguration {
	/** github username */
	@Value("${github.username}")
	private String userName;
	/** github personal access token */
	@Value("${github.personal.access.token}")
	private String accessToken;
	
	/**
	 * Create  RestTempalte
	 * @return RestTemplate
	 */
	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	/**
	 * create common HttpHeaders
	 * @return HttpHeaders
	 */
	@Bean
	@Qualifier("requestHeaders")
	public HttpHeaders getHttpHeaders() {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		requestHeaders.set("Accept", "application/vnd.github.v3+json");
		requestHeaders.setBasicAuth(HttpHeaders.encodeBasicAuth(userName, accessToken, Charset.forName("UTF-8")));
		return requestHeaders;
	}
	
	/**
	 * create common HttpHeaders
	 * @return HttpHeaders
	 */
	@Bean
	@Qualifier("responseHeaders")
	public HttpHeaders getResponseHttpHeaders() {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		return responseHeaders;
	}
}
