package com.platypass.platypass.presentation.controller;

import com.platypass.platypass.application.service.PerformanceMonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for performance monitoring endpoints.
 * This provides metrics and statistics about the gateway performance.
 */
@Slf4j
@RestController
@RequestMapping("/v1/performance")
public class PerformanceController {
    
    private final PerformanceMonitorService performanceMonitor;
    
    public PerformanceController(PerformanceMonitorService performanceMonitor) {
        this.performanceMonitor = performanceMonitor;
    }
    
    /**
     * GET endpoint for all provider statistics.
     * @return map of provider statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, PerformanceMonitorService.ProviderStats>> getAllStats() {
        Map<String, PerformanceMonitorService.ProviderStats> stats = performanceMonitor.getAllProviderStats();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * GET endpoint for specific provider statistics.
     * @param provider the provider name
     * @return provider statistics
     */
    @GetMapping("/stats/{provider}")
    public ResponseEntity<PerformanceMonitorService.ProviderStats> getProviderStats(@PathVariable String provider) {
        PerformanceMonitorService.ProviderStats stats = performanceMonitor.getProviderStats(provider);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * POST endpoint to reset all metrics.
     * @return success message
     */
    @PostMapping("/reset")
    public ResponseEntity<String> resetMetrics() {
        performanceMonitor.resetMetrics();
        return ResponseEntity.ok("Performance metrics reset successfully");
    }
} 