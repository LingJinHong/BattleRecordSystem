package com.majiang.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.majiang.backend.entity.AppFriendUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppFriendUserMapper extends BaseMapper<AppFriendUser> {
    // 可根据需要添加自定义方法
}
