package com.majiang.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateAppGameRequest {

    @NotNull(message = "teamId不能为空")
    private Long teamId;

    @NotNull(message = "userId不能为空")
    private Long userId;

    @NotEmpty(message = "userIds不能为空")
    private List<@NotNull(message = "userIds中存在空userId") Long> userIds;
}
