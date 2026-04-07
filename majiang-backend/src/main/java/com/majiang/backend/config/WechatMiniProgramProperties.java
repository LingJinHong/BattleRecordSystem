package com.majiang.backend.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "app.wechat")
public class WechatMiniProgramProperties {

    @NotBlank(message = "app.wechat.appid 不能为空")
    private String appid;

    @NotBlank(message = "app.wechat.secret 不能为空")
    private String secret;
}
