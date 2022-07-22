package com.cb.gulimall.product.decrypt;

public class YopClientException extends RuntimeException {
    private static final long serialVersionUID = -9085416005820812953L;

    /**
     * Constructs a new YopClientException with the specified detail message.
     *
     * @param message the detail error message.
     */
    public YopClientException(String message) {
        super(message);
    }

    /**
     * Constructs a new YopClientException with the specified detail message and the underlying cause.
     *
     * @param message the detail error message.
     * @param cause   the underlying cause of this exception.
     */
    public YopClientException(String message, Throwable cause) {
        super(message, cause);
    }

}

