package com.zerobase.reservation.dto.oauth2;

import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

    private final Map<String, Object> profile;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        this.profile = (Map<String, Object>) account.get("profile");
    }


    @Override
    public String getId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getNickName() {
        if (profile == null) {
            return null;
        }
        return (String) profile.get("nickname");
    }

    @Override
    public String getImageUrl() {
        if (profile == null) {
            return null;
        }
        return (String) profile.get("thumbnail_image_url");
    }
}
