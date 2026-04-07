package com.majiang.backend.service;

public interface WechatService {

    /**
     * 使用小程序登录 code 换取 openid。
     */
    String getOpenidByCode(String code);
}
