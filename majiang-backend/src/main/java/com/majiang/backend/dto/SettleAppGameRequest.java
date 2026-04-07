package com.majiang.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SettleAppGameRequest {

    @NotNull(message = "gameId不能为空")
    private Long gameId;

    @NotEmpty(message = "results不能为空")
    private List<@Valid PlayerAmountDTO> results;

    private Integer venueFee;

    @Data
    public static class PlayerAmountDTO {
        @NotNull(message = "userId不能为空")
        private Long userId;

        @NotNull(message = "amount不能为空")
        private BigDecimal amount;
    }
}
