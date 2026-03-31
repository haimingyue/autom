package com.atoms.backend.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WechatCode2SessionResponse(
        String openid,
        @JsonProperty("session_key")
        String sessionKey,
        String unionid,
        Integer errcode,
        String errmsg
) {

    public boolean hasError() {
        return errcode != null && errcode != 0;
    }
}
