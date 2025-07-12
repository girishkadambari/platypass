package com.platypass.platypass.domain.provider;

import com.platypass.platypass.domain.model.ChatCompletionRequest;
import com.platypass.platypass.domain.model.ChatCompletionResponse;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Interface for AI providers in the gateway system.
 * This interface follows the Interface Segregation Principle by defining
 * only the methods that are essential for AI provider functionality.
 */
public interface AIProvider {
    
    /**
     * Gets the name of the provider.
     * @return the provider name
     */
    String getProviderName();
    
    /**
     * Checks if this provider supports the given model.
     * @param model the model to check
     * @return true if the provider supports the model, false otherwise
     */
    boolean supportsModel(String model);
    
    /**
     * Gets the list of models supported by this provider.
     * @return list of supported models
     */
    List<String> getSupportedModels();
    
    /**
     * Performs a chat completion request.
     * @param request the chat completion request
     * @return a Mono containing the chat completion response
     */
    Mono<ChatCompletionResponse> chatCompletion(ChatCompletionRequest request);
    
    /**
     * Checks if the provider is available and healthy.
     * @return a Mono containing true if the provider is available, false otherwise
     */
    Mono<Boolean> isAvailable();
} 