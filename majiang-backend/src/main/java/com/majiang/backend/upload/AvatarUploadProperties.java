package com.majiang.backend.upload;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.upload")
public class AvatarUploadProperties {
    /**
     * 头像实际落地目录（例如：uploads/avatars）
     */
    private String avatarDir;

    /**
     * 返回给前端/写入数据库的访问前缀（例如：/files/avatars）
     */
    private String avatarPublicPrefix;

    /**
     * 单个头像最大大小（bytes）
     */
    private long maxAvatarSizeBytes;
}

