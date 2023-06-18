package com.zerobase.reservation.dto.oauth2;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public abstract class OAuth2UserInfo {

    protected final Map<String, Object> attributes;

    public abstract String getId();

    public abstract String getNickName();

    public abstract String getImageUrl();


}
