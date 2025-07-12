package com.platypass.platypass.domain.provider;

/**
 * Factory interface for creating AI providers.
 * This follows the Open/Closed Principle by allowing new providers
 * to be added without modifying existing code.
 */
public interface ProviderFactory {
    
    /**
     * Creates an AI provider based on the configuration.
     * @param config the provider configuration
     * @return the created AI provider
     */
    AIProvider createProvider(ProviderConfig config);
    
    /**
     * Checks if this factory can create a provider for the given configuration.
     * @param config the provider configuration
     * @return true if this factory can create the provider, false otherwise
     */
    boolean canCreate(ProviderConfig config);
} 