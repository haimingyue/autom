package com.atoms.backend.auth.service;

import com.atoms.backend.auth.dto.WechatCode2SessionResponse;
import com.atoms.backend.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class WechatAuthClient {

    private final RestClient restClient;
    private final String code2sessionUrl;
    private final String appId;
    private final String appSecret;

    public WechatAuthClient(
            @Value("${app.wechat.code2session-url}") String code2sessionUrl,
            @Value("${app.wechat.app-id}") String appId,
            @Value("${app.wechat.app-secret}") String appSecret
    ) {
        this.restClient = RestClient.builder().build();
        this.code2sessionUrl = code2sessionUrl;
        this.appId = appId;
        this.appSecret = appSecret;
    }

    public WechatCode2SessionResponse exchangeCode(String code) {
        if (!StringUtils.hasText(appId) || !StringUtils.hasText(appSecret)) {
            throw new BusinessException(50001, "微信登录配置缺失");
        }

        String url = UriComponentsBuilder.fromUriString(code2sessionUrl)
                .queryParam("appid", appId)
                .queryParam("secret", appSecret)
                .queryParam("js_code", code)
                .queryParam("grant_type", "authorization_code")
                .build(true)
                .toUriString();

        WechatCode2SessionResponse response = restClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(WechatCode2SessionResponse.class);

        if (response == null) {
            throw new BusinessException(50001, "微信身份换取失败");
        }
        if (response.hasError()) {
            throw new BusinessException(40101, "微信登录失败: " + response.errmsg());
        }
        if (!StringUtils.hasText(response.openid()) || !StringUtils.hasText(response.sessionKey())) {
            throw new BusinessException(50001, "微信身份换取结果不完整");
        }
        return response;
    }
}
