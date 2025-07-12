package com.platypass.platypass.infrastructure.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Performance configuration for the AI Gateway using WebClient.
 * This provides reactive, non-blocking HTTP client with connection pooling.
 */
@Slf4j
@Configuration
public class PerformanceConfiguration {
    
    @Bean
    public WebClient webClient() {
        var httpClient = reactor.netty.http.client.HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000) // 5 seconds connection timeout
                .responseTimeout(Duration.ofSeconds(30)) // 30 seconds response timeout
                .doOnConnected(conn -> 
                    conn.addHandlerLast(new ReadTimeoutHandler(30, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(30, TimeUnit.SECONDS))
                );
        
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB max
                .build();
    }
} 