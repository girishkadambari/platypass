package com.platypass.platypass.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a chat completion response in the AI gateway system.
 * This domain model encapsulates the response from AI providers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatCompletionResponse {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("object")
    private String object;
    
    @JsonProperty("created")
    private Long created;
    
    @JsonProperty("model")
    private String model;
    
    @JsonProperty("choices")
    private List<Choice> choices;
    
    @JsonProperty("usage")
    private Usage usage;
    
    /**
     * Represents a choice in the chat completion response.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Choice {
        @JsonProperty("index")
        private Integer index;
        
        @JsonProperty("message")
        private ChatMessage message;
        
        @JsonProperty("finish_reason")
        private String finishReason;
        
        @JsonProperty("logprobs")
        private Object logprobs;
    }
    
    /**
     * Represents usage statistics in the chat completion response.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage {
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;
        
        @JsonProperty("completion_tokens")
        private Integer completionTokens;
        
        @JsonProperty("total_tokens")
        private Integer totalTokens;
    }
} 