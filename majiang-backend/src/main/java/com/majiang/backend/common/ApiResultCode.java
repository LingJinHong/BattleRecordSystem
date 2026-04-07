package com.majiang.backend.common;

/**
 * 统一返回码：code + msg
 */
public final class ApiResultCode {
    private ApiResultCode() {}

    public static final int SUCCESS = 0;
    public static final int BAD_REQUEST = 40000;
    public static final int NOT_FOUND = 40400;
    public static final int INTERNAL_ERROR = 50000;
}

