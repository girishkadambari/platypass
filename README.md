# Platypass AI Gateway - Java Spring Boot Implementation

A Java Spring Boot implementation of an AI Gateway that supports multiple AI providers (OpenAI, Anthropic Claude, Google Gemini) with dynamic provider creation based on request headers.

## Features

- **Dynamic Provider Creation**: Create AI providers on-demand based on request headers
- **Multiple AI Providers**: Support for OpenAI, Anthropic Claude, and Google Gemini
- **Reactive Programming**: Built with Spring WebFlux and WebClient for non-blocking I/O
- **Clean Architecture**: Follows SOLID principles with domain, application, infrastructure, and presentation layers
- **Health Checks**: Built-in health monitoring endpoints
- **No Database Required**: Stateless design that doesn't require a database

## Architecture

```
platypass/
├── domain/           # Domain models and interfaces
├── application/      # Application services and use cases
├── infrastructure/   # External integrations and providers
└── presentation/     # REST controllers and web layer
```

## Quick Start

### 1. Build and Run

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

### 2. Test the Gateway

#### OpenAI Example
```bash
curl -X POST http://localhost:8080/v1/chat/completions \
  -H "Content-Type: application/json" \
  -H "x-api-key: YOUR_OPENAI_API_KEY" \
  -H "x-platypass-provider: openai" \
  -d '{
    "model": "gpt-3.5-turbo",
    "messages": [
      {"role": "user", "content": "Hello!"}
    ]
  }'
```

#### Anthropic Claude Example
```bash
curl -X POST http://localhost:8080/v1/chat/completions \
  -H "Content-Type: application/json" \
  -H "x-api-key: YOUR_ANTHROPIC_API_KEY" \
  -H "x-platypass-provider: anthropic" \
  -d '{
    "model": "claude-3-5-sonnet-20241022",
    "messages": [
      {"role": "user", "content": "Hello!"}
    ]
  }'
```

#### Google Gemini Example
```bash
curl -X POST http://localhost:8080/v1/chat/completions \
  -H "Content-Type: application/json" \
  -H "x-api-key: YOUR_GOOGLE_API_KEY" \
  -H "x-platypass-provider: google" \
  -d '{
    "model": "gemini-pro",
    "messages": [
      {"role": "user", "content": "Hello!"}
    ]
  }'
```

## API Endpoints

### Chat Completions
- **POST** `/v1/chat/completions` - Create chat completions

### Health Check
- **GET** `/actuator/health` - Application health status
- **GET** `/v1/health` - Simple health check

### Models
- **GET** `/v1/models` - Get available models
- **GET** `/v1/models/{model}/supported` - Check if model is supported

## Request Headers

The gateway uses the following headers for configuration:

| Header | Description | Example |
|--------|-------------|---------|
| `x-platypass-provider` | AI provider to use | `openai`, `anthropic`, `google` |
| `x-api-key` | API key for the provider | `sk-...` |
| `Authorization` | Alternative way to provide API key | `Bearer sk-...` |

## Supported Providers

### OpenAI
- **Base URL**: `https://api.openai.com`
- **Models**: `gpt-4`, `gpt-4-turbo`, `gpt-3.5-turbo`, `gpt-3.5-turbo-16k`
- **Provider Name**: `openai`

### Anthropic Claude
- **Base URL**: `https://api.anthropic.com`
- **Models**: `claude-3-opus-20240229`, `claude-3-sonnet-20240229`, `claude-3-haiku-20240307`
- **Provider Name**: `anthropic`

### Google Gemini
- **Base URL**: `https://generativelanguage.googleapis.com`
- **Models**: `gemini-pro`, `gemini-pro-vision`
- **Provider Name**: `google` or `google-gemini`

## Configuration

### Application Properties
```properties
# Server configuration
server.port=8080

# Logging
logging.level.com.platypass=INFO

# WebClient timeout
spring.webflux.client.timeout=30s
```

### Custom Provider Configuration
You can also use JSON configuration in headers:

```bash
curl -X POST http://localhost:8080/v1/chat/completions \
  -H "Content-Type: application/json" \
  -H "x-platypass-config: {\"provider\":\"openai\",\"api_key\":\"sk-...\",\"base_url\":\"https://api.openai.com\"}" \
  -d '{
    "model": "gpt-3.5-turbo",
    "messages": [{"role": "user", "content": "Hello!"}]
  }'
```

## Development

### Project Structure
```
src/main/java/com/platypass/platypass/
├── domain/
│   ├── model/           # Domain models
│   └── provider/        # Provider interfaces
├── application/
│   └── service/         # Application services
├── infrastructure/
│   ├── config/          # Configuration classes
│   └── provider/        # Provider implementations
└── presentation/
    └── controller/      # REST controllers
```

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
    return new NewProvider(providerConfig, webClient, objectMapper);
```

3. **Update RequestConfigExtractor**:
```java
case "new-provider":
    return "https://api.newprovider.com";
```

## Comparison with Node.js Implementation

| Feature | Node.js (Portkey) | Java (Platypass) |
|---------|-------------------|-------------------|
| Header Prefix | `x-portkey-` | `x-platypass-` |
| Architecture | Monolithic | Clean Architecture |
| Programming Model | Async/Await | Reactive (WebFlux) |
| Provider Selection | Dynamic | Dynamic |
| API Key Support | Multiple formats | Multiple formats |

## Performance

- **Non-blocking I/O**: Uses WebClient for reactive HTTP calls
- **Connection Pooling**: Efficient connection management
- **Timeout Handling**: Configurable timeouts for all providers
- **Error Handling**: Comprehensive error handling with fallbacks

## Monitoring

- **Health Checks**: Built-in Spring Boot Actuator health endpoints
- **Logging**: Structured logging with SLF4J
- **Metrics**: Spring Boot Actuator metrics (if enabled)

## Troubleshooting

### Common Issues

1. **Provider not found**: Ensure the provider name is correct in headers
2. **API key issues**: Check that the API key is valid and has proper permissions
3. **Network issues**: Verify internet connectivity and firewall settings
4. **Timeout errors**: Increase timeout values in configuration

### Debug Mode

Enable debug logging:
```properties
logging.level.com.platypass=DEBUG
```

## License

This project is licensed under the MIT License. 