package com.majiang.backend.service.impl;

import com.majiang.backend.common.ApiException;
import com.majiang.backend.common.ApiResultCode;
import com.majiang.backend.config.WechatMiniProgramProperties;
import com.majiang.backend.service.WechatService;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WechatServiceImpl implements WechatService {

    private final RestTemplate restTemplate;
    private final WechatMiniProgramProperties wechatProperties;
    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(WechatServiceImpl.class);
    @Override
    public String getOpenidByCode(String code) {
        if (!StringUtils.hasText(code)) {
            throw new ApiException(ApiResultCode.BAD_REQUEST, "code不能为空");
        }

        String appid = StringUtils.trimWhitespace(wechatProperties.getAppid());
        String secret = StringUtils.trimWhitespace(wechatProperties.getSecret());
        if (!StringUtils.hasText(appid) || !StringUtils.hasText(secret)) {
            throw new ApiException(ApiResultCode.INTERNAL_ERROR, "微信配置缺失，请检查 WECHAT_APPID/WECHAT_SECRET");
        }

        logger.info("appid: {}", appid);
        logger.info("secret: {}", secret);
        logger.info("code: {}", code);
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid={appid}&secret={secret}&js_code={code}&grant_type=authorization_code";
        Map<String, Object> response;
        try {
            String raw = restTemplate.getForObject(
                    url,
                    String.class,
                    Map.of(
                            "appid", appid,
                            "secret", secret,
                            "code", code
                    )
            );
            if (!StringUtils.hasText(raw)) {
                throw new ApiException(ApiResultCode.BAD_REQUEST, "微信返回为空");
            }
            response = objectMapper.readValue(raw, new TypeReference<Map<String, Object>>() {});
        } catch (ApiException ex) {
            throw ex;
        } catch (RestClientException ex) {
            throw new ApiException(ApiResultCode.INTERNAL_ERROR, "微信服务暂时不可用，请稍后重试");
        } catch (Exception ex) {
            throw new ApiException(ApiResultCode.INTERNAL_ERROR, "微信返回解析失败");
        }

        String openid = response == null ? null : String.valueOf(response.get("openid"));
        if (StringUtils.hasText(openid) && !"null".equals(openid)) {
            return openid;
        }

        String errCode = response == null ? null : String.valueOf(response.get("errcode"));
        String errMsg = response == null ? null : String.valueOf(response.get("errmsg"));
        if ("40029".equals(errCode)) {
            throw new ApiException(ApiResultCode.BAD_REQUEST, "code无效或已过期");
        }
        if ("45011".equals(errCode)) {
            throw new ApiException(ApiResultCode.BAD_REQUEST, "请求过于频繁，请稍后再试");
        }
        throw new ApiException(
                ApiResultCode.BAD_REQUEST,
                StringUtils.hasText(errMsg) && !"null".equals(errMsg) ? errMsg : "获取openid失败"
        );
    }
}
