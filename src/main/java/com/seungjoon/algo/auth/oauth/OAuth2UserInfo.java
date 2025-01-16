package com.seungjoon.algo.auth.oauth;

public interface OAuth2UserInfo {

    String getProvider();
    String getProviderId();
    String getEmail();
    String getName();
    String getImageUrl();
}
