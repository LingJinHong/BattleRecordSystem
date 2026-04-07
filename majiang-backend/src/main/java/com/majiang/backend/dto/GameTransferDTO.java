package com.majiang.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class GameTransferDTO {
    private Long fromUserId;
    private Long toUserId;
    private BigDecimal amount;
}
