package org.europepmc.funding.exception;

public class EuropePmcApiException extends RuntimeException {

    private final int statusCode;

    public EuropePmcApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public EuropePmcApiException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
