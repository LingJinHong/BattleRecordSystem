package com.majiang.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.majiang.backend.entity.AppTeamUser;
import com.majiang.backend.mapper.AppTeamUserMapper;
import com.majiang.backend.service.AppTeamUserService;
import org.springframework.stereotype.Service;

@Service
public class AppTeamUserServiceImpl extends ServiceImpl<AppTeamUserMapper, AppTeamUser>  implements AppTeamUserService {

}
