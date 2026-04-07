package com.majiang.backend.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAppUserBattleRequest {

    @NotBlank(message = "userId不能为空")
    private Long userId;

    @NotBlank(message = "incomeExpenseType不能为空")
    private String incomeExpenseType;

    @NotNull(message = "score不能为空")
    private Integer score;
    
    private Integer venueFee;

    private Long teamId;

    private LocalDate matchDate;

    private String remark;
}

