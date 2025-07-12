package com.platypass.platypass.infrastructure.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platypass.platypass.domain.model.ChatCompletionRequest;
import com.platypass.platypass.domain.model.ChatCompletionResponse;
import com.platypass.platypass.domain.provider.AIProvider;
import com.platypass.platypass.domain.provider.ProviderConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * OpenAI provider implementation using WebClient.
 * This follows the Single Responsibility Principle by focusing only on OpenAI-specific logic.
 */
@Slf4j
public class OpenAIProvider implements AIProvider {
    
    private final ProviderConfig config;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    public OpenAIProvider(ProviderConfig config, WebClient webClient, ObjectMapper objectMapper) {
        this.config = config;
        this.objectMapper = objectMapper;
        this.webClient = webClient.mutate()
                .baseUrl(config.getBaseUrl())
                .defaultHeader("Authorization", "Bearer " + config.getApiKey())
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
    
    @Override
    public String getProviderName() {
        return "openai";
    }
    
    @Override
    public boolean supportsModel(String model) {
        return config.getSupportedModels().contains(model);
    }
    
    @Override
    public List<String> getSupportedModels() {
        return config.getSupportedModels();
    }
    
    @Override
    public Mono<ChatCompletionResponse> chatCompletion(ChatCompletionRequest request) {
        return webClient.post()
                .uri("/v1/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChatCompletionResponse.class)
                .doOnSuccess(response -> log.info("OpenAI request completed successfully"))
                .doOnError(error -> log.error("OpenAI request failed: {}", error.getMessage()))
                .onErrorMap(throwable -> new RuntimeException("Failed to get response from OpenAI", throwable));
    }
    
    @Override
    public Mono<Boolean> isAvailable() {
        return webClient.get()
                .uri("/v1/models")
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode().is2xxSuccessful())
                .onErrorReturn(false)
                .doOnError(error -> log.error("Error checking OpenAI availability: {}", error.getMessage()));
    }
} 