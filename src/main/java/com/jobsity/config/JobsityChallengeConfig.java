package com.jobsity.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class JobsityChallengeConfig {

    @Value("${kenect.api.url}")
    private String KENECT_LABS_URL;

    @Value("${kenect.api.auth.token}")
    private String KENECT_LABS_TOKEN;

    @Bean
    public WebClient webClient(){
        return WebClient.builder().baseUrl(KENECT_LABS_URL)
                .defaultHeader("Authorization", "Bearer " + KENECT_LABS_TOKEN)
                .filter(logRequest())
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return (clientRequest, next) -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers()
                    .forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return next.exchange(clientRequest);
        };
    }
}
