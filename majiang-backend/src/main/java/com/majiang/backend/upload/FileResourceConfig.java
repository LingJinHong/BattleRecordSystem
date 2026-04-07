package com.majiang.backend.upload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class FileResourceConfig implements WebMvcConfigurer {

    private final AvatarUploadProperties properties;

    @Autowired
    public FileResourceConfig(AvatarUploadProperties properties) {
        this.properties = properties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将文件系统目录映射到 HTTP 路径，便于前端通过返回的 URL 直接访问头像图片。
        // 例如：/files/avatars/xxx.jpg -> <avatarDir>/xxx.jpg
        String publicPrefix = properties.getAvatarPublicPrefix();
        if (publicPrefix == null || publicPrefix.isBlank()) {
            return;
        }
        String location = "file:" + Path.of(properties.getAvatarDir()).toAbsolutePath().normalize().toString() + "/";
        registry.addResourceHandler(publicPrefix + "/**").addResourceLocations(location);
    }
}

