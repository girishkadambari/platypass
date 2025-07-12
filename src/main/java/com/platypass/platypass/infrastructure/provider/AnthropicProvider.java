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
 * Anthropic provider implementation using WebClient.
 * This follows the Single Responsibility Principle by focusing only on Anthropic-specific logic.
 */
@Slf4j
public class AnthropicProvider implements AIProvider {
    
    private final ProviderConfig config;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    public AnthropicProvider(ProviderConfig config, WebClient webClient, ObjectMapper objectMapper) {
        this.config = config;
        this.objectMapper = objectMapper;
        this.webClient = webClient.mutate()
                .baseUrl(config.getBaseUrl())
                .defaultHeader("x-api-key", config.getApiKey())
                .defaultHeader("anthropic-version", "2023-06-01")
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
    
    @Override
    public String getProviderName() {
        return "anthropic";
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
        WebClient.RequestHeadersSpec<?> requestSpec = webClient.post()
                .uri("/v1/messages")
                .bodyValue(request);
        
        // Add Anthropic-specific headers if available
        if (config.getProviderSpecificConfig() != null) {
            Object anthropicBeta = config.getProviderSpecificConfig().get("anthropic_beta");
            if (anthropicBeta != null) {
                requestSpec = requestSpec.header("anthropic-beta", anthropicBeta.toString());
            }
            
            Object anthropicVersion = config.getProviderSpecificConfig().get("anthropic_version");
            if (anthropicVersion != null) {
                requestSpec = requestSpec.header("anthropic-version", anthropicVersion.toString());
            }
        }
        
        return requestSpec
                .retrieve()
                .bodyToMono(ChatCompletionResponse.class)
                .doOnSuccess(response -> log.info("Anthropic request completed successfully"))
                .doOnError(error -> log.error("Anthropic request failed: {}", error.getMessage()))
                .onErrorMap(throwable -> new RuntimeException("Failed to get response from Anthropic", throwable));
    }
    
    @Override
    public Mono<Boolean> isAvailable() {
        return webClient.get()
                .uri("/v1/models")
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode().is2xxSuccessful())
                .onErrorReturn(false)
                .doOnError(error -> log.error("Error checking Anthropic availability: {}", error.getMessage()));
    }
} 