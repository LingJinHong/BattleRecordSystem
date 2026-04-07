package com.majiang.backend.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("game_player")
public class AppGamePlayer {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("game_id")
    private Long gameId;

    @TableField("user_id")
    private Long userId;

    @TableField("score")
    private BigDecimal score;
}