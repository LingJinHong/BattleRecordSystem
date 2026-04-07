package com.majiang.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.majiang.backend.entity.AppUserBattle;
import com.majiang.backend.mapper.AppUserBattleMapper;
import com.majiang.backend.service.AppUserBattleService;
import org.springframework.stereotype.Service;

@Service
public class AppUserBattleServiceImpl extends ServiceImpl<AppUserBattleMapper, AppUserBattle> implements AppUserBattleService {
}

