package com.frank.tools.controller;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class FallbackController {

	private static final Logger log = LoggerFactory.getLogger(FallbackController.class);

	@RequestMapping(value = "/**", method = {
			RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
			RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.HEAD,
			RequestMethod.OPTIONS })
	public ResponseEntity<Map<String, String>> handleFallback(
			HttpServletRequest request,
			@RequestBody(required = false) byte[] body) {
		logRequest(request, body);
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(Map.of("message", "404 Not Found"));
	}

	private void logRequest(HttpServletRequest request, byte[] body) {
		String headersText = Collections.list(request.getHeaderNames())
				.stream()
				.map(name -> "    " + name + ": " + request.getHeader(name))
				.collect(Collectors.joining("\n"));

		String bodyText = body != null && body.length > 0
				? new String(body, StandardCharsets.UTF_8)
				: "";

		String url = request.getRequestURL().toString();
		if (request.getQueryString() != null) {
			url += "?" + request.getQueryString();
		}

		log.info("""
				Unmatched request:
				  method: {}
				  url: {}
				  uri: {}
				  query: {}
				  remoteAddr: {}
				  contentType: {}
				  headers:
				{}
				  body: {}""",
				request.getMethod(),
				url,
				request.getRequestURI(),
				request.getQueryString(),
				request.getRemoteAddr(),
				request.getContentType(),
				headersText.isEmpty() ? "    (none)" : headersText,
				bodyText);
	}

}
