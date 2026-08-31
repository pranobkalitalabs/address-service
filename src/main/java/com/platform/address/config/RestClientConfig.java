package com.platform.address.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${app.uk-address.postcodes-api-url:https://api.postcodes.io}")
    private String postcodesApiUrl;

    @Value("${app.uk-address.timeout-ms:3000}")
    private int timeoutMs;

    @Bean
    public RestClient postcodesRestClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeoutMs);
        requestFactory.setReadTimeout(timeoutMs);

        return RestClient.builder()
                .baseUrl(postcodesApiUrl)
                .requestFactory(requestFactory)
                .build();
    }
}
