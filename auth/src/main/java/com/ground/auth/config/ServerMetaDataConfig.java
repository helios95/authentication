package com.ground.auth.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Getter
@Configuration
public class ServerMetaDataConfig {

    @Value("${yalooStore.front}")
    private String frontUrl;

    @Value("${yalooStore.shop}")
    private String shopUrl;

    @Value("${yalooStore.gateway}")
    private String gatewayUrl;

}
