package com.majiang.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.majiang.backend.entity.AppGame;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppGameMapper extends BaseMapper<AppGame> {
    // 可根据需要添加自定义方法
}
