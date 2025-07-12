package com.platypass.platypass.domain.provider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Configuration class for AI providers.
 * This follows the Single Responsibility Principle by focusing only on provider configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderConfig {
    
    private String name;
    private String baseUrl;
    private String apiKey;
    private List<String> supportedModels;
    private Integer timeoutSeconds;
    private Integer maxRetries;
    private Boolean enabled;
    private Map<String, Object> providerSpecificConfig;
    
    /**
     * Validates the configuration.
     * @return true if the configuration is valid, false otherwise
     */
    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
               baseUrl != null && !baseUrl.trim().isEmpty() &&
               apiKey != null && !apiKey.trim().isEmpty() &&
               enabled != null && enabled;
    }
} 