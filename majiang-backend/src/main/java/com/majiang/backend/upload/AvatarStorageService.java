package com.majiang.backend.upload;

import com.majiang.backend.common.ApiException;
import com.majiang.backend.common.ApiResultCode;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class AvatarStorageService {

    private static final Set<String> ALLOWED_EXT = Set.of("png", "jpg", "jpeg", "gif", "webp");

    private final AvatarUploadProperties properties;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public AvatarStorageService(AvatarUploadProperties properties) {
        this.properties = properties;
    }

    /**
     * 保存头像文件到磁盘，返回可访问 URL（并写入数据库 avatar_url）。
     */
    public String saveAvatar(MultipartFile avatarFile) {
        if (avatarFile == null || avatarFile.isEmpty()) {
            return null;
        }
        if (avatarFile.getSize() > properties.getMaxAvatarSizeBytes()) {
            throw new ApiException(ApiResultCode.BAD_REQUEST, "头像文件过大");
        }

        String originalName = avatarFile.getOriginalFilename();
        String ext = extractExt(originalName);
        if (!ALLOWED_EXT.contains(ext)) {
            throw new ApiException(ApiResultCode.BAD_REQUEST, "不支持的头像格式");
        }

        String filename = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        Path dir = Path.of(properties.getAvatarDir()).toAbsolutePath().normalize();
        Path target = dir.resolve(filename);

        try {
            Files.createDirectories(dir);
            avatarFile.transferTo(target);
        } catch (IOException e) {
            throw new ApiException(ApiResultCode.INTERNAL_ERROR, "头像保存失败");
        }

        String publicPrefix = properties.getAvatarPublicPrefix();
        // 例如：/files/avatars/<filename>
        return publicPrefix + "/" + filename;
    }

    /**
     * 根据头像链接下载图片到磁盘，返回可访问 URL（并写入数据库 avatar_url）。
     */
    public String saveAvatarFromUrl(String avatarUrl) {
        if (!StringUtils.hasText(avatarUrl)) {
            return null;
        }
        URI uri = parseUri(avatarUrl);
        String scheme = uri.getScheme();
        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            throw new ApiException(ApiResultCode.BAD_REQUEST, "头像链接必须是http/https");
        }

        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(15))
                .header("User-Agent", "majiang-backend-avatar-downloader/1.0")
                .GET()
                .build();
        HttpResponse<byte[]> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        } catch (Exception e) {
            throw new ApiException(ApiResultCode.BAD_REQUEST, "头像下载失败");
        }

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new ApiException(ApiResultCode.BAD_REQUEST, "头像下载失败");
        }
        byte[] bytes = response.body();
        if (bytes == null || bytes.length == 0) {
            throw new ApiException(ApiResultCode.BAD_REQUEST, "头像文件为空");
        }
        if (bytes.length > properties.getMaxAvatarSizeBytes()) {
            throw new ApiException(ApiResultCode.BAD_REQUEST, "头像文件过大");
        }

        String ext = detectExt(response.headers().firstValue("Content-Type").orElse(null), uri.getPath());
        if (!ALLOWED_EXT.contains(ext)) {
            throw new ApiException(ApiResultCode.BAD_REQUEST, "不支持的头像格式");
        }
        String filename = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        Path dir = Path.of(properties.getAvatarDir()).toAbsolutePath().normalize();
        Path target = dir.resolve(filename);
        try {
            Files.createDirectories(dir);
            Files.copy(new ByteArrayInputStream(bytes), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ApiException(ApiResultCode.INTERNAL_ERROR, "头像保存失败");
        }
        return properties.getAvatarPublicPrefix() + "/" + filename;
    }

    private static String extractExt(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            return "png";
        }
        int idx = originalFilename.lastIndexOf('.');
        if (idx < 0 || idx == originalFilename.length() - 1) {
            return "png";
        }
        return originalFilename.substring(idx + 1).toLowerCase(Locale.ROOT);
    }

    private static URI parseUri(String avatarUrl) {
        try {
            return new URI(avatarUrl.trim());
        } catch (URISyntaxException e) {
            throw new ApiException(ApiResultCode.BAD_REQUEST, "头像链接格式不正确");
        }
    }

    private static String detectExt(String contentType, String path) {
        if (StringUtils.hasText(contentType)) {
            String ct = contentType.toLowerCase(Locale.ROOT);
            if (ct.contains("image/png")) return "png";
            if (ct.contains("image/jpeg") || ct.contains("image/jpg")) return "jpg";
            if (ct.contains("image/gif")) return "gif";
            if (ct.contains("image/webp")) return "webp";
        }
        return extractExt(path);
    }
}

