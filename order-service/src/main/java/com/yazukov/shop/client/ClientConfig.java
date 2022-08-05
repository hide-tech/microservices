package com.yazukov.shop.client;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
public class ClientConfig {

    @Bean
    public RequestInterceptor requestBearerTokenInterceptor(){
        return requestTemplate -> {
            JwtAuthenticationToken token = (JwtAuthenticationToken) SecurityContextHolder
                    .getContext().getAuthentication();
            requestTemplate.header("Authorization", "Bearer " + token.getToken().getTokenValue());
        };
    }
}
