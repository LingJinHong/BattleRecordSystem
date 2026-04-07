package com.majiang.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.majiang.backend.entity.AppFriendUser;
import com.majiang.backend.mapper.AppFriendUserMapper;
import com.majiang.backend.service.AppFriendUserService;
import org.springframework.stereotype.Service;

@Service
public class AppFriendUserServiceImpl extends ServiceImpl<AppFriendUserMapper, AppFriendUser> implements AppFriendUserService {

}
