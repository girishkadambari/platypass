package com.platypass.platypass.presentation.controller;

import com.platypass.platypass.application.service.ChatCompletionService;
import com.platypass.platypass.domain.model.ChatCompletionRequest;
import com.platypass.platypass.domain.model.ChatCompletionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * REST controller for chat completion endpoints.
 * This follows the Single Responsibility Principle by focusing only on HTTP request handling.
 */
@Slf4j
@RestController
@RequestMapping("/v1")
public class ChatCompletionController {
    
    private final ChatCompletionService chatCompletionService;
    
    public ChatCompletionController(ChatCompletionService chatCompletionService) {
        this.chatCompletionService = chatCompletionService;
    }
    
    /**
     * POST endpoint for chat completions.
     * @param request the chat completion request
     * @param httpRequest the HTTP request for extracting headers
     * @return the chat completion response
     */
    @PostMapping("/chat/completions")
    public Mono<ResponseEntity<ChatCompletionResponse>> chatCompletion(
            @RequestBody ChatCompletionRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("Received chat completion request for model: {}", request.getModel());
        
        // Extract headers from the request
        Map<String, String> headers = extractHeaders(httpRequest);
        
        return chatCompletionService.chatCompletion(request, headers)
                .map(ResponseEntity::ok)
                .doOnError(throwable -> log.error("Error processing chat completion request: {}", throwable.getMessage()))
                .onErrorResume(throwable -> {
                    if (throwable instanceof IllegalArgumentException) {
                        return Mono.just(ResponseEntity.badRequest().build());
                    }
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }
    
    /**
     * GET endpoint to check if a model is supported.
     * @param model the model to check
     * @return true if the model is supported, false otherwise
     */
    @GetMapping("/models/{model}/supported")
    public ResponseEntity<Boolean> isModelSupported(@PathVariable String model) {
        boolean supported = chatCompletionService.isModelSupported(model);
        return ResponseEntity.ok(supported);
    }
    
    /**
     * GET endpoint to get available models.
     * @return list of available models
     */
    @GetMapping("/models")
    public ResponseEntity<List<String>> getAvailableModels() {
        List<String> models = chatCompletionService.getAvailableModels();
        return ResponseEntity.ok(models);
    }
    
    /**
     * GET endpoint for health check.
     * @return health status
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AI Gateway is running!");
    }
    
    /**
     * Extracts headers from the HTTP request.
     * @param request the HTTP request
     * @return map of headers
     */
    private Map<String, String> extractHeaders(HttpServletRequest request) {
        Map<String, String> headers = new java.util.HashMap<>();
        
        Collections.list(request.getHeaderNames()).forEach(headerName -> {
            String headerValue = request.getHeader(headerName);
            if (headerValue != null) {
                headers.put(headerName.toLowerCase(), headerValue);
            }
        });
        
        return headers;
    }
} 