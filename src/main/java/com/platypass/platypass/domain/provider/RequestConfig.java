package com.platypass.platypass.domain.provider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Configuration extracted from request headers.
 * This follows the Single Responsibility Principle by focusing only on request configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestConfig {
    
    private String provider;
    private String apiKey;
    private String baseUrl;
    private List<String> supportedModels;
    private Integer timeoutSeconds;
    private Integer maxRetries;
    private Boolean enabled;
    
    // Provider-specific configurations
    private Map<String, Object> providerSpecificConfig;
    
    /**
     * Validates the configuration.
     * @return true if the configuration is valid, false otherwise
     */
    public boolean isValid() {
        return provider != null && !provider.trim().isEmpty() &&
               apiKey != null && !apiKey.trim().isEmpty();
    }
    
    /**
     * Converts to ProviderConfig for internal use.
     * @return ProviderConfig object
     */
    public ProviderConfig toProviderConfig() {
        return ProviderConfig.builder()
                .name(provider)
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .supportedModels(supportedModels)
                .timeoutSeconds(timeoutSeconds)
                .maxRetries(maxRetries)
                .enabled(enabled != null ? enabled : true)
                .build();
    }
} 