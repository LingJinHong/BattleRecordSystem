package com.majiang.backend.common;

/**
 * 业务异常：会被全局异常处理器转换为统一返回格式。
 */
public class ApiException extends RuntimeException {
    private final int code;

    public ApiException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

