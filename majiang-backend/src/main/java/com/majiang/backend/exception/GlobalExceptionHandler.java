package com.majiang.backend.exception;

import com.majiang.backend.common.ApiException;
import com.majiang.backend.common.ApiResponse;
import com.majiang.backend.common.ApiResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<?> handleApiException(ApiException ex, HttpServletRequest request) {
        log.warn("Business exception: uri={}, code={}, msg={}",
                request.getRequestURI(), ex.getCode(), ex.getMessage(), ex);
        return ApiResponse.fail(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<?> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String msg = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(GlobalExceptionHandler::formatFieldError)
                .collect(Collectors.joining("; "));
        if (msg == null || msg.isBlank()) {
            msg = "参数校验失败";
        }
        log.warn("Validation exception: uri={}, msg={}", request.getRequestURI(), msg, ex);
        return ApiResponse.fail(ApiResultCode.BAD_REQUEST, msg);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<?> handleOtherException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception: uri={}", request.getRequestURI(), ex);
        return ApiResponse.fail(ApiResultCode.INTERNAL_ERROR, "服务器内部错误");
    }

    private static String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }
}

