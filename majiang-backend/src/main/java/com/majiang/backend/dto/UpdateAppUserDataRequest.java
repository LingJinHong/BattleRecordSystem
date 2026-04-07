package com.majiang.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateAppUserDataRequest {

    // 可选：允许修改归属 openid
    private String openid;

    // 可选：头像链接（后端会下载并落盘）
    private String avatar;

    @NotBlank(message = "title不能为空")
    private String title;

    @NotBlank(message = "content不能为空")
    private String content;
}

