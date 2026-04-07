package com.majiang.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.majiang.backend.entity.AppUserData;
import com.majiang.backend.mapper.AppUserDataMapper;
import com.majiang.backend.service.AppUserDataService;
import org.springframework.stereotype.Service;

@Service
public class AppUserDataServiceImpl extends ServiceImpl<AppUserDataMapper, AppUserData> implements AppUserDataService {
}

