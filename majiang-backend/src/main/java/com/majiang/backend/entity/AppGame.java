package com.majiang.backend.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("game")
public class AppGame {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("team_id")
    private Long teamId;

    @TableField("creator_id")
    private Long creatorId;

    @TableField("status")
    private String status;

    @TableField("game_time")
    private LocalDateTime gameTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
