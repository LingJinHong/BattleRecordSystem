package com.majiang.backend.controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.majiang.backend.common.ApiResponse;
import com.majiang.backend.upload.AvatarStorageService;

@RestController
@RequestMapping("/api/user")
public class AvatarController {

    private final AvatarStorageService avatarStorageService;

    public AvatarController( AvatarStorageService avatarStorageService) {
        this.avatarStorageService = avatarStorageService;
    }

    @PostMapping("/upload-avatar")
    public ApiResponse<String> uploadAvatar(
            @RequestParam("avatar") MultipartFile avatarFile,
            @RequestParam("openid") String openid) {

        try {
            // 1. 校验文件
            if (avatarFile.isEmpty()) {
                return ApiResponse.fail(-1,"头像文件为空");
            }
            String avatarUrl = avatarStorageService.saveAvatar(avatarFile);
            return ApiResponse.ok(avatarUrl);
        } catch (Exception e) {
            return ApiResponse.fail(-1,"头像上传失败：" + e.getMessage());
        }
    }
}