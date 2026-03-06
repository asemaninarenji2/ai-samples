# Swagger/OpenAPI Documentation Setup

## Overview
The application now includes comprehensive Swagger/OpenAPI documentation for all REST endpoints.

## Access Swagger UI

### Swagger UI (Interactive Documentation)
```
http://localhost:8080/swagger-ui.html
```

### OpenAPI JSON Specification
```
http://localhost:8080/v3/api-docs
```

## What's Included

### 1. SpringDoc OpenAPI Dependencies
- `springdoc-openapi-starter-webmvc-ui:2.2.0`
- `spring-boot-starter-validation`

### 2. OpenAPI Configuration
Created `OpenApiConfig.java` with:
- Application metadata
- Server configurations
- License and contact information

### 3. Controller Documentation
Added comprehensive Swagger annotations to:

#### ChatController (`/api`)
- Basic chat endpoint
- Parameter descriptions and examples
- Response codes and descriptions

#### RAGController (`/rag`)
- Manual vector search
- Advisor-based RAG
- Web search RAG
- Advanced RAG with PII masking
- Header parameters for username tracking
- Detailed operation descriptions

#### MemoryChatClientController (`/memory`)
- Default conversation memory
- User-specific conversation memory
- Conversation tracking examples

#### StreamController (`/stream`)
- Streaming chat responses
- Real-time interaction documentation

#### StructuredOutputController (`/structured`)
- POJO responses
- Bean converter responses
- List and Map responses
- Object list responses
- Schema definitions for CountryCities model

## Swagger Annotations Used

### Class Level
- `@Tag` - Controller grouping and description
- `@RestController` - REST controller designation

### Method Level
- `@Operation` - Endpoint summary and description
- `@ApiResponses` - HTTP response codes
- `@Parameter` - Parameter descriptions and examples
- `@Schema` - Response schema definitions

### Examples
```java
@Operation(summary = "Simple chat with OpenAI", description = "Basic chat interaction with OpenAI model")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Successful response"),
    @ApiResponse(responseCode = "400", description = "Invalid message parameter")
})
public String chat(
    @Parameter(description = "User message to send to AI", required = true, example = "Hello, how are you?")
    @RequestParam("message") String message)
```

## Features

### 1. Interactive Testing
- Try endpoints directly from Swagger UI
- Automatic parameter filling with examples
- Response preview

### 2. API Documentation
- Clear endpoint descriptions
- Parameter requirements and examples
- Response schemas
- Error codes

### 3. Code Generation
- OpenAPI spec available for client generation
- Schema definitions for data models
- Standard REST API documentation

## Usage

### 1. Start the Application
```bash
./gradlew bootRun
```

### 2. Access Swagger UI
Open browser to: `http://localhost:8080/swagger-ui.html`

### 3. Explore APIs
- Browse different controller sections
- Try endpoints with sample data
- View response schemas

### 4. Download OpenAPI Spec
```bash
curl http://localhost:8080/v3/api-docs > openapi-spec.json
```

## Benefits

1. **Developer Experience**: Interactive API exploration
2. **Documentation**: Always up-to-date API docs
3. **Testing**: Built-in API testing interface
4. **Client Generation**: Generate SDKs from OpenAPI spec
5. **Standardization**: Follows OpenAPI 3.0 standards

## Next Steps

1. Add more detailed parameter validation
2. Include response examples
3. Add authentication documentation
4. Create API versioning strategy
5. Add custom error response schemas

The Swagger documentation is now fully integrated and provides comprehensive API coverage for all Spring AI endpoints.
