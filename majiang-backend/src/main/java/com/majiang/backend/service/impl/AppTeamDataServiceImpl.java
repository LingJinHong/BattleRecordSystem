package com.majiang.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.majiang.backend.entity.AppTeamData;
import com.majiang.backend.mapper.AppTeamDataMapper;
import com.majiang.backend.service.AppTeamDataService;
import org.springframework.stereotype.Service;

@Service
public class AppTeamDataServiceImpl extends ServiceImpl<AppTeamDataMapper, AppTeamData> implements AppTeamDataService {
}

