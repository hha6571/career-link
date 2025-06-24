package com.career.careerlink.global.security.oauth.userinfo;

import java.util.Map;

public class GoogleUserInfo implements OAuth2UserInfo {
    private final Map<String,Object> a;
    public GoogleUserInfo(Map<String,Object> a){ this.a=a; }
    public String getProvider(){ return "GOOGLE"; }
    public String getProviderUserId(){ return (String)a.get("sub"); }
    public String getEmail(){ return (String)a.get("email"); }
    public String getName(){ return (String)a.get("name"); }
    public Map<String,Object> getRaw(){ return a; }
}