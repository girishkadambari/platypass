package com.platypass.platypass.infrastructure.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platypass.platypass.domain.provider.AIProvider;
import com.platypass.platypass.domain.provider.ProviderConfig;
import com.platypass.platypass.domain.provider.RequestConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Factory for creating AI providers dynamically based on request configuration.
 * This follows the Factory pattern and allows creating providers on-demand.
 */
@Slf4j
@Component
public class DynamicProviderFactory {
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    public DynamicProviderFactory(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Creates a provider based on request configuration.
     * @param requestConfig the request configuration
     * @return the created AI provider
     */
    public AIProvider createProvider(RequestConfig requestConfig) {
        if (!requestConfig.isValid()) {
            throw new IllegalArgumentException("Invalid request configuration");
        }
        
        String provider = requestConfig.getProvider().toLowerCase();
        ProviderConfig providerConfig = buildProviderConfig(requestConfig);
        
        switch (provider) {
            case "openai":
                return new OpenAIProvider(providerConfig, webClient, objectMapper);
            case "anthropic":
                return new AnthropicProvider(providerConfig, webClient, objectMapper);
            case "google":
            case "google-gemini":
                return new GoogleGeminiProvider(providerConfig, webClient, objectMapper);
            default:
                throw new IllegalArgumentException("Unsupported provider: " + provider);
        }
    }
    
    /**
     * Builds a ProviderConfig from RequestConfig.
     * @param requestConfig the request configuration
     * @return the provider configuration
     */
    private ProviderConfig buildProviderConfig(RequestConfig requestConfig) {
        return ProviderConfig.builder()
                .name(requestConfig.getProvider())
                .baseUrl(requestConfig.getBaseUrl())
                .apiKey(requestConfig.getApiKey())
                .supportedModels(requestConfig.getSupportedModels())
                .timeoutSeconds(requestConfig.getTimeoutSeconds() != null ? requestConfig.getTimeoutSeconds() : 30)
                .maxRetries(requestConfig.getMaxRetries() != null ? requestConfig.getMaxRetries() : 3)
                .enabled(requestConfig.getEnabled() != null ? requestConfig.getEnabled() : true)
                .providerSpecificConfig(requestConfig.getProviderSpecificConfig())
                .build();
    }
    
    /**
     * Checks if a provider is supported.
     * @param provider the provider name
     * @return true if supported, false otherwise
     */
    public boolean isProviderSupported(String provider) {
        if (provider == null) {
            return false;
        }
        
        String providerLower = provider.toLowerCase();
        return providerLower.equals("openai") || 
               providerLower.equals("anthropic") || 
               providerLower.equals("google") || 
               providerLower.equals("google-gemini");
    }
} 