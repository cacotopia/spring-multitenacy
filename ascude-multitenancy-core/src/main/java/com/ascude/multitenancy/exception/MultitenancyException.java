package com.ascude.multitenancy.exception;

/**
 * 多租户异常基类
 */
public class MultitenancyException extends RuntimeException {

    public MultitenancyException(String message) {
        super(message);
    }

    public MultitenancyException(String message, Throwable cause) {
        super(message, cause);
    }
}
