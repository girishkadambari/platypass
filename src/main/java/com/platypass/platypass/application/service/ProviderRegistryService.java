package com.platypass.platypass.application.service;

import com.platypass.platypass.domain.provider.AIProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service for managing AI providers.
 * This follows the Dependency Inversion Principle by depending on abstractions (AIProvider interface)
 * rather than concrete implementations.
 */
@Slf4j
@Service
public class ProviderRegistryService {
    
    private final Map<String, AIProvider> providers = new ConcurrentHashMap<>();
    
    /**
     * Registers a provider with the registry.
     * @param provider the provider to register
     */
    public void registerProvider(AIProvider provider) {
        providers.put(provider.getProviderName(), provider);
        log.info("Registered provider: {}", provider.getProviderName());
    }
    
    /**
     * Gets a provider by name.
     * @param providerName the name of the provider
     * @return the provider, or null if not found
     */
    public AIProvider getProvider(String providerName) {
        return providers.get(providerName);
    }
    
    /**
     * Gets all registered providers.
     * @return list of all providers
     */
    public List<AIProvider> getAllProviders() {
        return List.copyOf(providers.values());
    }
    
    /**
     * Gets providers that support a specific model.
     * @param model the model to check
     * @return list of providers that support the model
     */
    public List<AIProvider> getProvidersForModel(String model) {
        return providers.values().stream()
                .filter(provider -> provider.supportsModel(model))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets the first available provider for a model.
     * @param model the model to check
     * @return the first available provider, or null if none available
     */
    public AIProvider getFirstAvailableProviderForModel(String model) {
        return providers.values().stream()
                .filter(provider -> provider.supportsModel(model))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Checks if any provider supports a model.
     * @param model the model to check
     * @return true if any provider supports the model, false otherwise
     */
    public boolean isModelSupported(String model) {
        return providers.values().stream()
                .anyMatch(provider -> provider.supportsModel(model));
    }
    
    /**
     * Gets the number of registered providers.
     * @return the number of providers
     */
    public int getProviderCount() {
        return providers.size();
    }
} 