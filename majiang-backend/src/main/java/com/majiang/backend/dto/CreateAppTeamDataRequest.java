package com.majiang.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAppTeamDataRequest {

    @NotBlank(message = "userId不能为空")
    private Long userId;

    @NotBlank(message = "teamName不能为空")
    private String teamName;
}

