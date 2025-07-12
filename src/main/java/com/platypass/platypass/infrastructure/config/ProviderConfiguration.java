package com.platypass.platypass.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platypass.platypass.domain.provider.AIProvider;
import com.platypass.platypass.domain.provider.ProviderConfig;
import com.platypass.platypass.infrastructure.provider.AnthropicProvider;
import com.platypass.platypass.infrastructure.provider.GoogleGeminiProvider;
import com.platypass.platypass.infrastructure.provider.OpenAIProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration class for AI providers.
 * This follows the Configuration pattern and Dependency Injection principles.
 */
@Slf4j
@Configuration
public class ProviderConfiguration {
    
    @Value("${ai.openai.api-key:}")
    private String openaiApiKey;
    
    @Value("${ai.openai.base-url:https://api.openai.com}")
    private String openaiBaseUrl;
    
    @Value("${ai.anthropic.api-key:}")
    private String anthropicApiKey;
    
    @Value("${ai.anthropic.base-url:https://api.anthropic.com}")
    private String anthropicBaseUrl;
    
    @Value("${ai.google.api-key:}")
    private String googleApiKey;
    
    @Value("${ai.google.base-url:https://generativelanguage.googleapis.com}")
    private String googleBaseUrl;
    
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
    
    @Bean
    public ProviderConfig openaiConfig() {
        return ProviderConfig.builder()
                .name("openai")
                .baseUrl(openaiBaseUrl)
                .apiKey(openaiApiKey)
                .supportedModels(Arrays.asList("gpt-4", "gpt-4-turbo", "gpt-3.5-turbo", "gpt-3.5-turbo-16k"))
                .timeoutSeconds(30)
                .maxRetries(3)
                .enabled(!openaiApiKey.isEmpty())
                .build();
    }
    
    @Bean
    public ProviderConfig anthropicConfig() {
        return ProviderConfig.builder()
                .name("anthropic")
                .baseUrl(anthropicBaseUrl)
                .apiKey(anthropicApiKey)
                .supportedModels(Arrays.asList("claude-3-opus-20240229", "claude-3-sonnet-20240229", "claude-3-haiku-20240307"))
                .timeoutSeconds(30)
                .maxRetries(3)
                .enabled(!anthropicApiKey.isEmpty())
                .build();
    }
    
    @Bean
    public ProviderConfig googleConfig() {
        return ProviderConfig.builder()
                .name("google-gemini")
                .baseUrl(googleBaseUrl)
                .apiKey(googleApiKey)
                .supportedModels(Arrays.asList("gemini-pro", "gemini-pro-vision"))
                .timeoutSeconds(30)
                .maxRetries(3)
                .enabled(!googleApiKey.isEmpty())
                .build();
    }
    
    @Bean
    public List<AIProvider> aiProviders(
            WebClient webClient,
            ObjectMapper objectMapper,
            ProviderConfig openaiConfig,
            ProviderConfig anthropicConfig,
            ProviderConfig googleConfig) {
        
        List<AIProvider> providers = new java.util.ArrayList<>();
        
        if (openaiConfig.isValid()) {
            providers.add(new OpenAIProvider(openaiConfig, webClient, objectMapper));
            log.info("OpenAI provider configured");
        }
        
        if (anthropicConfig.isValid()) {
            providers.add(new AnthropicProvider(anthropicConfig, webClient, objectMapper));
            log.info("Anthropic provider configured");
        }
        
        if (googleConfig.isValid()) {
            providers.add(new GoogleGeminiProvider(googleConfig, webClient, objectMapper));
            log.info("Google Gemini provider configured");
        }
        
        log.info("Configured {} AI providers", providers.size());
        return providers;
    }
} 