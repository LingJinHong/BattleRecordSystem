package com.majiang.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Value("${app.monitor.slow-request-ms:1000}")
    private long slowRequestThresholdMs;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String traceId = resolveTraceId(request);
        MDC.put("traceId", traceId);
        response.setHeader("X-Trace-Id", traceId);

        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long cost = System.currentTimeMillis() - start;
            String method = request.getMethod();
            String uri = request.getRequestURI();
            int status = response.getStatus();

            if (cost >= slowRequestThresholdMs) {
                log.warn("[SLOW_REQUEST] traceId={}, method={}, uri={}, status={}, costMs={}", traceId, method, uri, status, cost);
            } else {
                log.info("[REQUEST] traceId={}, method={}, uri={}, status={}, costMs={}", traceId, method, uri, status, cost);
            }
            MDC.remove("traceId");
        }
    }

    private String resolveTraceId(HttpServletRequest request) {
        String incomingTraceId = request.getHeader("X-Trace-Id");
        if (incomingTraceId != null && !incomingTraceId.isBlank()) {
            return incomingTraceId.trim();
        }
        return UUID.randomUUID().toString().replace("-", "");
    }
}
