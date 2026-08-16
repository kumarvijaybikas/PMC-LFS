package org.europepmc.funding.controller;

import org.europepmc.funding.exception.EuropePmcApiException;
import org.europepmc.funding.exception.InvalidSearchQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetail> handleNoResourceFound(NoResourceFoundException ex) {
        log.debug("Resource not found: {}", ex.getResourcePath());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                "The requested resource '/" + ex.getResourcePath() + "' was not found.");
        problem.setTitle("Resource Not Found");
        problem.setType(URI.create("/docs/errors#not-found"));
        problem.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(InvalidSearchQueryException.class)
    public ResponseEntity<ProblemDetail> handleInvalidSearchQuery(InvalidSearchQueryException ex) {
        log.warn("Invalid search query requested: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Invalid Search Parameters");
        problem.setType(URI.create("/docs/errors#invalid-query"));
        problem.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ProblemDetail> handleMissingParam(MissingServletRequestParameterException ex) {
        log.warn("Missing required parameter: {}", ex.getParameterName());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Required parameter '" + ex.getParameterName() + "' is missing.");
        problem.setTitle("Missing Required Parameter");
        problem.setType(URI.create("/docs/errors#missing-parameter"));
        problem.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("Parameter type mismatch: name={}, value={}", ex.getName(), ex.getValue());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Invalid value '" + ex.getValue() + "' for parameter '" + ex.getName() + "'.");
        problem.setTitle("Parameter Type Mismatch");
        problem.setType(URI.create("/docs/errors#type-mismatch"));
        problem.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(EuropePmcApiException.class)
    public ResponseEntity<ProblemDetail> handleExternalApiException(EuropePmcApiException ex) {
        log.error("External Europe PMC API error: status={}, message={}", ex.getStatusCode(), ex.getMessage());
        HttpStatus status = ex.getStatusCode() == 408 || ex.getStatusCode() == 504
                ? HttpStatus.GATEWAY_TIMEOUT
                : HttpStatus.BAD_GATEWAY;

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status,
                "Failed to communicate with external Europe PMC service: " + ex.getMessage());
        problem.setTitle("Upstream Service Error");
        problem.setType(URI.create("/docs/errors#upstream-error"));
        problem.setProperty("upstreamStatus", ex.getStatusCode());
        problem.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(status).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex) {
        log.error("Unhandled internal application error", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected internal error occurred: " + ex.getMessage());
        problem.setTitle("Internal Server Error");
        problem.setType(URI.create("/docs/errors#internal-error"));
        problem.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }
}
