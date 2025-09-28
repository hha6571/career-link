package com.career.careerlink.global.security.oauth.userinfo;

import java.util.Map;

public interface OAuth2UserInfo {
    String getProvider();
    String getProviderUserId();
    String getEmail();
    String getName();
    Map<String,Object> getRaw();
}
