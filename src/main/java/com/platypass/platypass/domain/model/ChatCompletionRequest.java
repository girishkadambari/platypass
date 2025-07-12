package com.platypass.platypass.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Represents a chat completion request in the AI gateway system.
 * This domain model encapsulates all the parameters needed for a chat completion.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatCompletionRequest {
    
    @JsonProperty("model")
    private String model;
    
    @JsonProperty("messages")
    private List<ChatMessage> messages;
    
    @JsonProperty("temperature")
    private Double temperature;
    
    @JsonProperty("top_p")
    private Double topP;
    
    @JsonProperty("n")
    private Integer n;
    
    @JsonProperty("stream")
    private Boolean stream;
    
    @JsonProperty("stop")
    private List<String> stop;
    
    @JsonProperty("max_tokens")
    private Integer maxTokens;
    
    @JsonProperty("presence_penalty")
    private Double presencePenalty;
    
    @JsonProperty("frequency_penalty")
    private Double frequencyPenalty;
    
    @JsonProperty("logit_bias")
    private Map<String, Integer> logitBias;
    
    @JsonProperty("user")
    private String user;
    
    @JsonProperty("tools")
    private List<Tool> tools;
    
    @JsonProperty("tool_choice")
    private Object toolChoice;
    
    @JsonProperty("response_format")
    private ResponseFormat responseFormat;
    
    /**
     * Represents a tool in the chat completion request.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tool {
        @JsonProperty("type")
        private String type;
        
        @JsonProperty("function")
        private Function function;
    }
    
    /**
     * Represents a function in a tool.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Function {
        @JsonProperty("name")
        private String name;
        
        @JsonProperty("description")
        private String description;
        
        @JsonProperty("parameters")
        private Object parameters;
    }
    
    /**
     * Represents the response format for the chat completion.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseFormat {
        @JsonProperty("type")
        private String type;
    }
} 