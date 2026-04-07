package com.majiang.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAppUserDataRequest {

    @NotBlank(message = "openid不能为空")
    private String openid;

    // 前端传入的头像链接（后端会下载并落盘）
    private String avatarUrl;

    @NotBlank(message = "title不能为空")
    private String title;

    @NotBlank(message = "content不能为空")
    private String content;
}

