package com.zerobase.reservation.dto.oauth2;

import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserInfo {

    private final Map<String, Object> response;

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
        this.response = (Map<String, Object>) attributes.get("response");

    }

    @Override
    public String getId() {
        if(response == null){
            return null;
        }
        return (String) response.get("id");
    }

    @Override
    public String getNickName() {
        if (response == null) {
            return null;
        }
        return (String) response.get("nickname");
    }

    @Override
    public String getImageUrl() {
        if (response == null) {
            return null;
        }
        return (String) response.get("profile_image");
    }
}
