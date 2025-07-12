package com.platypass.platypass.application.service;

import com.platypass.platypass.domain.model.ChatCompletionRequest;
import com.platypass.platypass.domain.model.ChatCompletionResponse;
import com.platypass.platypass.domain.provider.AIProvider;
import com.platypass.platypass.domain.provider.RequestConfig;
import com.platypass.platypass.infrastructure.provider.DynamicProviderFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * Service for handling chat completion requests.
 * This follows the Single Responsibility Principle by focusing only on chat completion logic.
 */
@Slf4j
@Service
public class ChatCompletionService {
    
    private final ProviderRegistryService providerRegistry;
    private final RequestConfigExtractor configExtractor;
    private final DynamicProviderFactory dynamicProviderFactory;
    
    public ChatCompletionService(ProviderRegistryService providerRegistry, 
                               RequestConfigExtractor configExtractor,
                               DynamicProviderFactory dynamicProviderFactory) {
        this.providerRegistry = providerRegistry;
        this.configExtractor = configExtractor;
        this.dynamicProviderFactory = dynamicProviderFactory;
    }
    
    /**
     * Performs a chat completion request with dynamic provider creation.
     * @param request the chat completion request
     * @param headers the request headers
     * @return a Mono containing the response
     */
    public Mono<ChatCompletionResponse> chatCompletion(ChatCompletionRequest request, Map<String, String> headers) {
        String model = request.getModel();
        
        if (model == null || model.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Model is required for chat completion"));
        }
        
        // Extract configuration from headers
        RequestConfig requestConfig = configExtractor.extractConfig(headers);
        
        // If we have a provider specified in headers, use dynamic provider creation
        if (requestConfig.getProvider() != null && !requestConfig.getProvider().trim().isEmpty()) {
            return chatCompletionWithDynamicProvider(request, requestConfig);
        } else {
            // Fall back to registered providers
            return chatCompletionWithRegisteredProviders(request);
        }
    }
    
    /**
     * Performs chat completion using a dynamically created provider.
     * @param request the chat completion request
     * @param requestConfig the request configuration
     * @return a Mono containing the response
     */
    private Mono<ChatCompletionResponse> chatCompletionWithDynamicProvider(ChatCompletionRequest request, RequestConfig requestConfig) {
        try {
            // Set default values if not provided
            if (requestConfig.getBaseUrl() == null) {
                requestConfig.setBaseUrl(configExtractor.getDefaultBaseUrl(requestConfig.getProvider()));
            }
            if (requestConfig.getSupportedModels() == null) {
                requestConfig.setSupportedModels(configExtractor.getDefaultSupportedModels(requestConfig.getProvider()));
            }
            
            // Create provider dynamically
            AIProvider provider = dynamicProviderFactory.createProvider(requestConfig);
            
            log.info("Using dynamic provider {} for model {}", provider.getProviderName(), request.getModel());
            
            return provider.chatCompletion(request)
                    .doOnError(throwable -> log.error("Error with dynamic provider {}: {}", provider.getProviderName(), throwable.getMessage()))
                    .onErrorMap(throwable -> new RuntimeException("Failed to get response from " + provider.getProviderName(), throwable));
                    
        } catch (Exception e) {
            log.error("Error creating dynamic provider: {}", e.getMessage());
            return Mono.error(e);
        }
    }
    
    /**
     * Performs chat completion using registered providers.
     * @param request the chat completion request
     * @return a Mono containing the response
     */
    private Mono<ChatCompletionResponse> chatCompletionWithRegisteredProviders(ChatCompletionRequest request) {
        String model = request.getModel();
        List<AIProvider> availableProviders = providerRegistry.getProvidersForModel(model);
        
        if (availableProviders.isEmpty()) {
            return Mono.error(new IllegalArgumentException("No provider available for model: " + model));
        }
        
        // Try the first available provider
        AIProvider provider = availableProviders.get(0);
        log.info("Using registered provider {} for model {}", provider.getProviderName(), model);
        
        return provider.chatCompletion(request)
                .doOnError(throwable -> log.error("Error with provider {}: {}", provider.getProviderName(), throwable.getMessage()))
                .onErrorResume(throwable -> {
                    // If there are other providers available, try them
                    if (availableProviders.size() > 1) {
                        return tryOtherProviders(availableProviders, request, 1);
                    }
                    return Mono.error(new RuntimeException("All providers failed for model: " + model, throwable));
                });
    }
    
    /**
     * Tries other providers if the first one fails.
     * @param providers the list of providers
     * @param request the chat completion request
     * @param startIndex the index to start from
     * @return a Mono containing the response from a successful provider
     */
    private Mono<ChatCompletionResponse> tryOtherProviders(List<AIProvider> providers, 
                                                          ChatCompletionRequest request, 
                                                          int startIndex) {
        if (startIndex >= providers.size()) {
            return Mono.error(new RuntimeException("No providers available"));
        }
        
        AIProvider provider = providers.get(startIndex);
        log.info("Trying provider {} for model {}", provider.getProviderName(), request.getModel());
        
        return provider.chatCompletion(request)
                .doOnError(throwable -> log.error("Error with provider {}: {}", provider.getProviderName(), throwable.getMessage()))
                .onErrorResume(throwable -> {
                    if (startIndex + 1 < providers.size()) {
                        return tryOtherProviders(providers, request, startIndex + 1);
                    }
                    return Mono.error(new RuntimeException("All providers failed", throwable));
                });
    }
    
    /**
     * Checks if a model is supported.
     * @param model the model to check
     * @return true if the model is supported, false otherwise
     */
    public boolean isModelSupported(String model) {
        return providerRegistry.isModelSupported(model);
    }
    
    /**
     * Gets available models.
     * @return list of available models
     */
    public List<String> getAvailableModels() {
        return providerRegistry.getAllProviders().stream()
                .flatMap(provider -> provider.getSupportedModels().stream())
                .distinct()
                .toList();
    }
} 