package com.campus.trade.exception;

/**
 * 业务异常（触发事务回滚）
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
