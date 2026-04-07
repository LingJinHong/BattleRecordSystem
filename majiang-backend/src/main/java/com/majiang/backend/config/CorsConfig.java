package com.majiang.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 微信小程序侧会触发跨域请求与预检请求（OPTIONS）。
        registry.addMapping("/**")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Content-Disposition")
                // 小程序通常不需要带 Cookie/Authorization；这里允许全部来源。
                .allowedOriginPatterns("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}

