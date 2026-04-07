package com.majiang.backend.service.impl;

import com.majiang.backend.common.ApiException;
import com.majiang.backend.config.WechatMiniProgramProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WechatServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private WechatMiniProgramProperties wechatProperties;

    @InjectMocks
    private WechatServiceImpl wechatService;

    @BeforeEach
    void setUp() {
        when(wechatProperties.getAppid()).thenReturn("test-appid");
        when(wechatProperties.getSecret()).thenReturn("test-secret");
    }

    @Test
    void getOpenidByCode_shouldReturnOpenidWhenSuccess() {
        Map<String, Object> response = new HashMap<>();
        response.put("openid", "openid-123");

        when(restTemplate.getForObject(any(String.class), eq(Map.class), eq("test-appid"), eq("test-secret"), eq("code123")))
                .thenReturn(response);

        String openid = wechatService.getOpenidByCode("code123");

        assertEquals("openid-123", openid);
    }

    @Test
    void getOpenidByCode_shouldThrowWhenCodeEmpty() {
        assertThrows(ApiException.class, () -> wechatService.getOpenidByCode(" "));
    }

    @Test
    void getOpenidByCode_shouldThrowWhenWechatUnavailable() {
        when(restTemplate.getForObject(any(String.class), eq(Map.class), eq("test-appid"), eq("test-secret"), eq("code123")))
                .thenThrow(new RestClientException("timeout"));

        assertThrows(ApiException.class, () -> wechatService.getOpenidByCode("code123"));
    }

    @Test
    void getOpenidByCode_shouldThrowWhenCodeExpired() {
        Map<String, Object> response = new HashMap<>();
        response.put("errcode", "40029");
        response.put("errmsg", "invalid code");

        when(restTemplate.getForObject(any(String.class), eq(Map.class), eq("test-appid"), eq("test-secret"), eq("code123")))
                .thenReturn(response);

        assertThrows(ApiException.class, () -> wechatService.getOpenidByCode("code123"));
    }
}
