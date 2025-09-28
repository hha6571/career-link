package com.career.careerlink.global.security.oauth.userinfo;

import java.util.Map;

@SuppressWarnings("unchecked")
public class KakaoUserInfo implements OAuth2UserInfo {
    private final Map<String,Object> a;
    public KakaoUserInfo(Map<String,Object> a){ this.a=a; }
    public String getProvider(){ return "KAKAO"; }
    public String getProviderUserId(){ return String.valueOf(a.get("id")); }
    public String getEmail(){
        var acc = (Map<String,Object>) a.get("kakao_account");
        return acc == null ? null : (String) acc.get("email");
    }
    public String getName(){
        var acc = (Map<String,Object>) a.get("kakao_account");
        var prof = acc == null ? null : (Map<String,Object>) acc.get("profile");
        return prof == null ? null : (String) prof.get("nickname");
    }
    public Map<String,Object> getRaw(){ return a; }
}