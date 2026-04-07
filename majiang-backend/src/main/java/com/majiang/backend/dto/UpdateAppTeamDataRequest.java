package com.majiang.backend.dto;

import lombok.Data;

@Data
public class UpdateAppTeamDataRequest {

    // 可选：允许修改发起人/拥有者
    private Long userId;

    // 可选：允许修改小队名称
    private String teamName;
}

