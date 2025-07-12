package com.platypass.platypass.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a chat message in the AI gateway system.
 * This is a domain model that follows the Single Responsibility Principle.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    
    @JsonProperty("role")
    private String role;
    
    @JsonProperty("content")
    private String content;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("tool_calls")
    private List<ToolCall> toolCalls;
    
    @JsonProperty("function_call")
    private FunctionCall functionCall;
    
    /**
     * Represents a tool call in a chat message.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToolCall {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("type")
        private String type;
        
        @JsonProperty("function")
        private Function function;
    }
    
    /**
     * Represents a function call in a chat message.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FunctionCall {
        @JsonProperty("name")
        private String name;
        
        @JsonProperty("arguments")
        private String arguments;
    }
    
    /**
     * Represents a function in a tool call.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Function {
        @JsonProperty("name")
        private String name;
        
        @JsonProperty("arguments")
        private String arguments;
    }
} 