package com.majiang.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.majiang.backend.entity.AppTeamUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppTeamUserMapper extends BaseMapper<AppTeamUser> {
    // 可根据需要添加自定义方法
}
