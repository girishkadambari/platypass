package com.platypass.platypass.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platypass.platypass.domain.provider.RequestConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service to extract configuration from request headers.
 * This follows the Single Responsibility Principle by focusing only on header parsing.
 */
@Slf4j
@Service
public class RequestConfigExtractor {
    
    private static final String POWERED_BY = "portkey";
    private static final String CONFIG_HEADER = "x-" + POWERED_BY + "-config";
    private static final String PROVIDER_HEADER = "x-" + POWERED_BY + "-provider";
    private static final String AUTHORIZATION_HEADER = "authorization";
    
    private final ObjectMapper objectMapper;
    
    public RequestConfigExtractor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    /**
     * Extracts configuration from request headers.
     * @param headers the request headers
     * @return RequestConfig object
     */
    public RequestConfig extractConfig(Map<String, String> headers) {
        // Check if there's a JSON config in headers
        String configJson = headers.get(CONFIG_HEADER);
        if (configJson != null && !configJson.trim().isEmpty()) {
            return extractFromJsonConfig(configJson, headers);
        }
        
        // Extract from individual headers
        return extractFromIndividualHeaders(headers);
    }
    
    /**
     * Extracts configuration from JSON config header.
     * @param configJson the JSON configuration string
     * @param headers the request headers
     * @return RequestConfig object
     */
    private RequestConfig extractFromJsonConfig(String configJson, Map<String, String> headers) {
        try {
            Map<String, Object> config = objectMapper.readValue(configJson, Map.class);
            
            // Extract provider and API key
            String provider = (String) config.get("provider");
            String apiKey = (String) config.get("api_key");
            
            // If not in config, try headers
            if (provider == null) {
                provider = headers.get(PROVIDER_HEADER);
            }
            if (apiKey == null) {
                apiKey = extractApiKey(headers.get(AUTHORIZATION_HEADER));
            }
            
            return RequestConfig.builder()
                    .provider(provider)
                    .apiKey(apiKey)
                    .baseUrl((String) config.get("base_url"))
                    .supportedModels((List<String>) config.get("supported_models"))
                    .timeoutSeconds((Integer) config.get("timeout_seconds"))
                    .maxRetries((Integer) config.get("max_retries"))
                    .enabled((Boolean) config.get("enabled"))
                    .providerSpecificConfig(config)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error parsing JSON config: {}", e.getMessage());
            return extractFromIndividualHeaders(headers);
        }
    }
    
    /**
     * Extracts configuration from individual headers.
     * @param headers the request headers
     * @return RequestConfig object
     */
    private RequestConfig extractFromIndividualHeaders(Map<String, String> headers) {
        String provider = headers.get(PROVIDER_HEADER);
        String apiKey = extractApiKey(headers.get(AUTHORIZATION_HEADER));
        
        // Extract provider-specific configurations
        Map<String, Object> providerConfig = extractProviderSpecificConfig(headers);
        
        return RequestConfig.builder()
                .provider(provider)
                .apiKey(apiKey)
                .providerSpecificConfig(providerConfig)
                .build();
    }
    
    /**
     * Extracts API key from authorization header.
     * @param authorizationHeader the authorization header value
     * @return the API key
     */
    private String extractApiKey(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.trim().isEmpty()) {
            return null;
        }
        
        if (authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        
        return authorizationHeader;
    }
    
    /**
     * Extracts provider-specific configurations from headers.
     * @param headers the request headers
     * @return map of provider-specific configurations
     */
    private Map<String, Object> extractProviderSpecificConfig(Map<String, String> headers) {
        Map<String, Object> config = new HashMap<>();
        
        // Extract all x-portkey-* headers
        headers.forEach((key, value) -> {
            if (key.startsWith("x-" + POWERED_BY + "-") && !key.equals(PROVIDER_HEADER)) {
                String configKey = key.substring(("x-" + POWERED_BY + "-").length());
                config.put(configKey, value);
            }
        });
        
        return config;
    }
    
    /**
     * Gets the default base URL for a provider.
     * @param provider the provider name
     * @return the default base URL
     */
    public String getDefaultBaseUrl(String provider) {
        if (provider == null) {
            return null;
        }
        
        switch (provider.toLowerCase()) {
            case "openai":
                return "https://api.openai.com";
            case "anthropic":
                return "https://api.anthropic.com";
            case "google":
            case "google-gemini":
                return "https://generativelanguage.googleapis.com";
            default:
                return null;
        }
    }
    
    /**
     * Gets the default supported models for a provider.
     * @param provider the provider name
     * @return list of supported models
     */
    public List<String> getDefaultSupportedModels(String provider) {
        if (provider == null) {
            return List.of();
        }
        
        switch (provider.toLowerCase()) {
            case "openai":
                return List.of("gpt-4", "gpt-4-turbo", "gpt-3.5-turbo", "gpt-3.5-turbo-16k");
            case "anthropic":
                return List.of("claude-3-opus-20240229", "claude-3-sonnet-20240229", "claude-3-haiku-20240307");
            case "google":
            case "google-gemini":
                return List.of("gemini-pro", "gemini-pro-vision");
            default:
                return List.of();
        }
    }
} 