# Platypass AI Gateway

A scalable Spring Boot implementation of an AI Gateway with support for multiple AI providers including OpenAI, Anthropic Claude, and Google Gemini. This gateway acts as a proxy that extracts authorization keys and provider information from user requests, similar to the Node.js Portkey gateway.

## Architecture Overview

This implementation follows SOLID principles and clean architecture patterns:

### Domain Layer
- **Models**: `ChatMessage`, `ChatCompletionRequest`, `ChatCompletionResponse`
- **Interfaces**: `AIProvider`, `ProviderFactory`
- **Configuration**: `RequestConfig`, `ProviderConfig`

### Application Layer
- **Services**: `ChatCompletionService`, `ProviderRegistryService`, `RequestConfigExtractor`
- Business logic and orchestration

### Infrastructure Layer
- **Providers**: `OpenAIProvider`, `AnthropicProvider`, `GoogleGeminiProvider`
- **Configuration**: `ProviderConfiguration`, `ProviderRegistrationService`, `DynamicProviderFactory`

### Presentation Layer
- **Controllers**: `ChatCompletionController`
- REST API endpoints

## Features

- **Dynamic Provider Creation**: Creates providers on-demand based on request headers
- **Multi-Provider Support**: OpenAI, Anthropic Claude, Google Gemini
- **Header-Based Configuration**: Extracts API keys and provider info from request headers
- **Failover Mechanism**: Automatic fallback to alternative providers
- **Async Processing**: Non-blocking request handling
- **Configuration Management**: Environment-based and header-based configuration
- **Health Monitoring**: Provider availability checks
- **Scalable Architecture**: Easy to add new providers

## Dynamic Configuration

The gateway supports dynamic configuration through request headers, allowing users to provide their own API keys and provider information:

### Header-Based Configuration

#### 1. Simple Provider Selection
```bash
curl -X POST http://localhost:8080/v1/chat/completions \
  -H "Content-Type: application/json" \
  -H "x-portkey-provider: openai" \
  -H "Authorization: Bearer YOUR_OPENAI_API_KEY" \
  -d '{
    "model": "gpt-4",
    "messages": [{"role": "user", "content": "Hello"}]
  }'
```

#### 2. JSON Configuration
```bash
curl -X POST http://localhost:8080/v1/chat/completions \
  -H "Content-Type: application/json" \
  -H "x-portkey-config: {\"provider\":\"openai\",\"api_key\":\"YOUR_KEY\",\"base_url\":\"https://api.openai.com\"}" \
  -d '{
    "model": "gpt-4",
    "messages": [{"role": "user", "content": "Hello"}]
  }'
```

#### 3. Provider-Specific Headers
```bash
# OpenAI with organization
curl -X POST http://localhost:8080/v1/chat/completions \
  -H "Content-Type: application/json" \
  -H "x-portkey-provider: openai" \
  -H "x-portkey-openai-organization: org-123" \
  -H "Authorization: Bearer YOUR_OPENAI_API_KEY" \
  -d '{"model": "gpt-4", "messages": [{"role": "user", "content": "Hello"}]}'

# Anthropic with version
curl -X POST http://localhost:8080/v1/chat/completions \
  -H "Content-Type: application/json" \
  -H "x-portkey-provider: anthropic" \
  -H "x-portkey-anthropic-version: 2023-06-01" \
  -H "Authorization: Bearer YOUR_ANTHROPIC_API_KEY" \
  -d '{"model": "claude-3-sonnet-20240229", "messages": [{"role": "user", "content": "Hello"}]}'
```

### Supported Headers

| Header | Description | Example |
|--------|-------------|---------|
| `x-portkey-provider` | Provider name | `openai`, `anthropic`, `google-gemini` |
| `Authorization` | API key | `Bearer YOUR_API_KEY` |
| `x-portkey-config` | JSON configuration | `{"provider":"openai","api_key":"..."}` |
| `x-portkey-openai-organization` | OpenAI organization | `org-123` |
| `x-portkey-openai-project` | OpenAI project | `proj-456` |
| `x-portkey-anthropic-version` | Anthropic API version | `2023-06-01` |
| `x-portkey-vertex-project-id` | Google Vertex project | `my-project-123` |

## API Endpoints

### Chat Completions
```
POST /v1/chat/completions
```

Request Body:
```json
{
  "model": "gpt-4",
  "messages": [
    {
      "role": "user",
      "content": "Hello, how are you?"
    }
  ],
  "temperature": 0.7,
  "max_tokens": 100
}
```

### Model Information
```
GET /v1/models
GET /v1/models/{model}/supported
```

### Health Check
```
GET /v1/health
```

## Configuration

### Environment Variables (Optional)
Set these for fallback configuration when headers are not provided:

```bash
# OpenAI
export OPENAI_API_KEY=your_openai_api_key

# Anthropic
export ANTHROPIC_API_KEY=your_anthropic_api_key

# Google Gemini
export GOOGLE_API_KEY=your_google_api_key
```

Or configure in `application.properties`:

```properties
ai.openai.api-key=your_openai_api_key
ai.anthropic.api-key=your_anthropic_api_key
ai.google.api-key=your_google_api_key
```

## Supported Models

### OpenAI
- gpt-4
- gpt-4-turbo
- gpt-3.5-turbo
- gpt-3.5-turbo-16k

### Anthropic Claude
- claude-3-opus-20240229
- claude-3-sonnet-20240229
- claude-3-haiku-20240307

### Google Gemini
- gemini-pro
- gemini-pro-vision

## Running the Application

1. **Build the project**:
   ```bash
   ./gradlew build
   ```

2. **Run the application**:
   ```bash
   ./gradlew bootRun
   ```

3. **Test with dynamic configuration**:
   ```bash
   curl -X POST http://localhost:8080/v1/chat/completions \
     -H "Content-Type: application/json" \
     -H "x-portkey-provider: openai" \
     -H "Authorization: Bearer YOUR_OPENAI_API_KEY" \
     -d '{
       "model": "gpt-4",
       "messages": [
         {
           "role": "user",
           "content": "Hello, how are you?"
         }
       ]
     }'
   ```

4. **Access the web interface**:
   Open http://localhost:8080 in your browser

## Design Principles

### SOLID Principles
- **Single Responsibility**: Each class has one reason to change
- **Open/Closed**: Open for extension, closed for modification
- **Liskov Substitution**: Providers can be substituted without breaking functionality
- **Interface Segregation**: Clients depend only on interfaces they use
- **Dependency Inversion**: High-level modules don't depend on low-level modules

### Clean Architecture
- **Domain Layer**: Core business logic and entities
- **Application Layer**: Use cases and business rules
- **Infrastructure Layer**: External concerns (APIs, databases)
- **Presentation Layer**: User interface and controllers

### Design Patterns
- **Strategy Pattern**: Different AI providers implement the same interface
- **Factory Pattern**: Dynamic provider creation and configuration
- **Registry Pattern**: Centralized provider management
- **Command Pattern**: Async request handling

## Dynamic Provider Creation

The gateway creates providers dynamically based on request headers:

1. **Header Extraction**: `RequestConfigExtractor` extracts configuration from headers
2. **Provider Creation**: `DynamicProviderFactory` creates providers on-demand
3. **Request Processing**: `ChatCompletionService` orchestrates the request

### Flow
```
Request Headers → RequestConfigExtractor → DynamicProviderFactory → AIProvider → Response
```

## Extending the Gateway

### Adding a New Provider

1. **Create Provider Implementation**:
   ```java
   @Component
   public class NewProvider implements AIProvider {
       // Implementation
   }
   ```

2. **Add to DynamicProviderFactory**:
   ```java
   case "new-provider":
       return new NewProvider(providerConfig, restTemplate, objectMapper);
   ```

3. **Add Default Configuration**:
   ```java
   case "new-provider":
       return "https://api.newprovider.com";
   ```

## Error Handling

The gateway includes comprehensive error handling:
- **Provider Failures**: Automatic fallback to alternative providers
- **Invalid Requests**: Proper HTTP status codes and error messages
- **Configuration Errors**: Graceful degradation when providers are unavailable
- **Network Issues**: Timeout and retry mechanisms
- **Header Parsing**: Fallback to registered providers when headers are invalid

## Monitoring and Logging

- **Structured Logging**: All operations are logged with appropriate levels
- **Provider Health**: Regular availability checks
- **Request Tracking**: Request/response correlation
- **Performance Metrics**: Response times and throughput
- **Dynamic Provider Logging**: Logs when providers are created dynamically

## Future Enhancements

- **Caching**: Response caching for improved performance
- **Rate Limiting**: Per-provider rate limiting
- **Authentication**: API key validation and security
- **Streaming**: Support for streaming responses
- **Metrics**: Prometheus metrics integration
- **Circuit Breaker**: Resilience patterns for provider failures
- **Plugin System**: Dynamic provider loading 