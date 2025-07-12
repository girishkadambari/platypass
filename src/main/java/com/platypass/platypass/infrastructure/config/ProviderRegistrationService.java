package com.platypass.platypass.infrastructure.config;

import com.platypass.platypass.application.service.ProviderRegistryService;
import com.platypass.platypass.domain.provider.AIProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Service to register AI providers with the registry on application startup.
 * This follows the Command Line Runner pattern for initialization.
 */
@Slf4j
@Component
public class ProviderRegistrationService implements CommandLineRunner {
    
    private final ProviderRegistryService providerRegistry;
    private final List<AIProvider> providers;
    
    public ProviderRegistrationService(ProviderRegistryService providerRegistry, List<AIProvider> providers) {
        this.providerRegistry = providerRegistry;
        this.providers = providers;
    }
    
    @Override
    public void run(String... args) throws Exception {
        log.info("Registering {} AI providers", providers.size());
        
        for (AIProvider provider : providers) {
            providerRegistry.registerProvider(provider);
        }
        
        log.info("Successfully registered {} providers", providerRegistry.getProviderCount());
        
        // Log available models
        providers.forEach(provider -> {
            log.info("Provider {} supports models: {}", 
                    provider.getProviderName(), 
                    String.join(", ", provider.getSupportedModels()));
        });
    }
} 