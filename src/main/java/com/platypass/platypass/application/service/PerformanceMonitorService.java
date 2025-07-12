package com.platypass.platypass.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service for monitoring performance metrics.
 * This tracks request counts, response times, and provider performance.
 */
@Slf4j
@Service
public class PerformanceMonitorService {
    
    private final Map<String, AtomicLong> requestCounts = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> totalResponseTimes = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> errorCounts = new ConcurrentHashMap<>();
    
    /**
     * Records a successful request.
     * @param provider the provider name
     * @param responseTimeMs the response time in milliseconds
     */
    public void recordSuccess(String provider, long responseTimeMs) {
        requestCounts.computeIfAbsent(provider, k -> new AtomicLong(0)).incrementAndGet();
        totalResponseTimes.computeIfAbsent(provider, k -> new AtomicLong(0)).addAndGet(responseTimeMs);
        
        log.info("Provider {}: Request completed in {}ms", provider, responseTimeMs);
    }
    
    /**
     * Records a failed request.
     * @param provider the provider name
     * @param error the error message
     */
    public void recordError(String provider, String error) {
        errorCounts.computeIfAbsent(provider, k -> new AtomicLong(0)).incrementAndGet();
        
        log.error("Provider {}: Request failed - {}", provider, error);
    }
    
    /**
     * Gets performance statistics for a provider.
     * @param provider the provider name
     * @return performance statistics
     */
    public ProviderStats getProviderStats(String provider) {
        long requests = requestCounts.getOrDefault(provider, new AtomicLong(0)).get();
        long errors = errorCounts.getOrDefault(provider, new AtomicLong(0)).get();
        long totalTime = totalResponseTimes.getOrDefault(provider, new AtomicLong(0)).get();
        
        double avgResponseTime = requests > 0 ? (double) totalTime / requests : 0.0;
        double errorRate = requests > 0 ? (double) errors / requests : 0.0;
        
        return ProviderStats.builder()
                .provider(provider)
                .totalRequests(requests)
                .totalErrors(errors)
                .averageResponseTimeMs(avgResponseTime)
                .errorRate(errorRate)
                .build();
    }
    
    /**
     * Gets performance statistics for all providers.
     * @return map of provider statistics
     */
    public Map<String, ProviderStats> getAllProviderStats() {
        Map<String, ProviderStats> stats = new ConcurrentHashMap<>();
        
        requestCounts.keySet().forEach(provider -> 
            stats.put(provider, getProviderStats(provider))
        );
        
        return stats;
    }
    
    /**
     * Resets all performance metrics.
     */
    public void resetMetrics() {
        requestCounts.clear();
        totalResponseTimes.clear();
        errorCounts.clear();
        log.info("Performance metrics reset");
    }
    
    /**
     * Performance statistics for a provider.
     */
    public static class ProviderStats {
        private String provider;
        private long totalRequests;
        private long totalErrors;
        private double averageResponseTimeMs;
        private double errorRate;
        
        // Builder pattern
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private ProviderStats stats = new ProviderStats();
            
            public Builder provider(String provider) {
                stats.provider = provider;
                return this;
            }
            
            public Builder totalRequests(long totalRequests) {
                stats.totalRequests = totalRequests;
                return this;
            }
            
            public Builder totalErrors(long totalErrors) {
                stats.totalErrors = totalErrors;
                return this;
            }
            
            public Builder averageResponseTimeMs(double averageResponseTimeMs) {
                stats.averageResponseTimeMs = averageResponseTimeMs;
                return this;
            }
            
            public Builder errorRate(double errorRate) {
                stats.errorRate = errorRate;
                return this;
            }
            
            public ProviderStats build() {
                return stats;
            }
        }
        
        // Getters
        public String getProvider() { return provider; }
        public long getTotalRequests() { return totalRequests; }
        public long getTotalErrors() { return totalErrors; }
        public double getAverageResponseTimeMs() { return averageResponseTimeMs; }
        public double getErrorRate() { return errorRate; }
    }
} 