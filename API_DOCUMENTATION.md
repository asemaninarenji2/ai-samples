# Spring AI OpenAI Application API Documentation

## Overview
This Spring Boot application demonstrates various Spring AI features including chat, memory, RAG (Retrieval Augmented Generation), streaming, and structured outputs.

## Base URL
```
http://localhost:8080
```

## Authentication
Most endpoints require a `username` header for conversation tracking and memory management.

## API Endpoints

### 1. Basic Chat API

#### Simple Chat
```http
GET /api/chat?message={message}
```
**Description**: Basic chat with OpenAI model
**Parameters**:
- `message` (String, required): User message

**Response**: String (AI response)

**Example**:
```bash
curl "http://localhost:8080/api/chat?message=Hello%20there"
```

---

#### Chat with Options
```http
GET /options/use?message={message}
```
**Description**: Chat using GPT-4 with specific options
**Parameters**:
- `message` (String, required): User message

**Response**: String (AI response)

**Example**:
```bash
curl "http://localhost:8080/options/use?message=Explain%20quantum%20computing"
```

---

#### Multi-Model Chat

##### OpenAI Chat
```http
GET /multi/openai/chat?message={message}
```
**Description**: Chat using OpenAI model
**Parameters**:
- `message` (String, required): User message

**Response**: String (AI response)

##### Ollama Chat
```http
GET /multi/ollama/chat?message={message}
```
**Description**: Chat using local Ollama model
**Parameters**:
- `message` (String, required): User message

**Response**: String (AI response)

---

#### Streaming Chat
```http
GET /stream/stream?message={message}
```
**Description**: Streaming chat response
**Parameters**:
- `message` (String, required): User message

**Response**: Flux<String> (Streaming AI response)

**Example**:
```bash
curl "http://localhost:8080/stream/stream?message=Tell%20me%20a%20story"
```

---

### 2. Memory & Conversation API

#### Default Conversation Memory
```http
GET /memory/default-conversation-id?message={message}
```
**Description**: Chat with default conversation memory
**Parameters**:
- `message` (String, required): User message

**Response**: String (AI response with memory)

#### Unique Conversation Memory
```http
GET /memory/unique-conversation-id?message={message}
```
**Headers**:
- `username` (String, required): Unique user identifier

**Parameters**:
- `message` (String, required): User message

**Response**: String (AI response with user-specific memory)

**Example**:
```bash
curl -H "username: john_doe" "http://localhost:8080/memory/unique-conversation-id?message=Remember%20my%20name"
```

---

### 3. RAG (Retrieval Augmented Generation) API

#### Manual Vector Search
```http
GET /rag/manual-search/data?message={message}
```
**Headers**:
- `username` (String, required): User identifier

**Parameters**:
- `message` (String, required): Search query

**Response**: String (AI response with retrieved context from vector store)

**Example**:
```bash
curl -H "username: testuser" "http://localhost:8080/rag/manual-search/data?message=What%20is%20HR?"
```

#### Advisor RAG Search
```http
GET /rag/advisor-search/data?message={message}
```
**Headers**:
- `username` (String, required): User identifier

**Parameters**:
- `message` (String, required): Search query

**Response**: String (AI response with automatic context retrieval)

#### Web Search RAG
```http
GET /rag/web-search/data?message={message}
```
**Headers**:
- `username` (String, required): User identifier

**Parameters**:
- `message` (String, required): Search query

**Response**: String (AI response with web search results via Tavily API)

#### Advanced RAG
```http
GET /rag/advisor-search/advanced-rag/data?message={message}
```
**Headers**:
- `username` (String, required): User identifier

**Parameters**:
- `message` (String, required): Search query

**Response**: String (AI response with advanced context and PII masking)

---

### 4. Prompt Engineering API

#### Prompt Stuffing
```http
GET /prompt-stuffing?message={message}
```
**Description**: Chat with system prompt template
**Parameters**:
- `message` (String, required): User message

**Response**: String (AI response with stuffed prompt)

#### Email Template
```http
GET /prompt/email?customerName={name}&customerMessage={message}
```
**Parameters**:
- `customerName` (String, required): Customer name
- `customerMessage` (String, required): Customer message

**Response**: String (Generated email response)

**Example**:
```bash
curl "http://localhost:8080/prompt/email?customerName=John&customerMessage=I%20need%20help"
```

#### System Role Chat
```http
GET /role/chat?message={message}
```
**Description**: Chat with system role configuration
**Parameters**:
- `message` (String, required): User message

**Response**: String (AI response with role context)

---

### 5. Structured Output API

#### POJO Response
```http
GET /structured/pojo?message={message}
```
**Parameters**:
- `message` (String, required): User message

**Response**: CountryCities (Structured object)

#### Bean Converter
```http
GET /structured/bean?message={message}
```
**Parameters**:
- `message` (String, required): User message

**Response**: CountryCities (Bean object)

#### List Response
```http
GET /structured/list?message={message}
```
**Parameters**:
- `message` (String, required): User message

**Response**: List<String> (String list)

#### Map Response
```http
GET /structured/map?message={message}
```
**Parameters**:
- `message` (String, required): User message

**Response**: Map<String, Object> (Key-value map)

#### Object List
```http
GET /structured/object-list?message={message}
```
**Parameters**:
- `message` (String, required): User message

**Response**: List<CountryCities> (Object list)

---

### 6. Advisor API

#### Chat with Advisors
```http
GET /advisor/advice?message={message}
```
**Description**: Chat with multiple advisors (logging, safety, audit)
**Parameters**:
- `message` (String, required): User message

**Response**: String (AI response with advisor processing)

**Advisors Applied**:
- **SimpleLoggerAdvisor**: Logs chat interactions
- **SafeGuardAdvisor**: Prevents sensitive information leakage
- **TokenUsageAuditAdvisor**: Tracks token usage

---

### 7. Health & Infrastructure API

#### Database Connection Test
```http
GET /test-postgres
```
**Description**: Test PostgreSQL connection
**Response**: String (Connection status)

#### Qdrant Health Check
```http
GET /api/health/qdrant
```
**Description**: Check Qdrant vector database health
**Response**: String (Health status)

#### Create Qdrant Collection
```http
POST /api/health/qdrant/collection
```
**Description**: Create Qdrant collection for vector storage
**Response**: String (Collection creation status)

---

## Configuration

### Environment Variables
- `OPENAI_API_KEY`: OpenAI API key (required for OpenAI endpoints)
- `TAVILY_API_KEY`: Tavily API key (required for web search RAG)

### Database Configuration
- **Qdrant**: Vector database for RAG functionality (localhost:6333/6334)
- **PostgreSQL**: Optional secondary database (localhost:5432)
- **H2**: In-memory database for chat memory

### Application Properties
Key configuration in `application.yml`:
```yaml
loader:
  pdf-data-loader: true  # Enable PDF loading into vector store
  random-data-loader: false # Disable sample data loading

spring:
  ai:
    vectorstore:
      qdrant:
        collection-name: eazybytes
        embedding-dimension: 1536
```

## Data Models

### CountryCities
```java
public class CountryCities {
    private String country;
    private List<String> cities;
    // getters and setters
}
```

## Error Handling

All endpoints return appropriate HTTP status codes:
- `200 OK`: Successful request
- `400 Bad Request`: Invalid parameters
- `503 Service Unavailable`: External service (Qdrant, OpenAI) unavailable

## Usage Examples

### Complete RAG Workflow
```bash
# 1. Load PDF data (enabled by default)
# 2. Query the loaded data
curl -H "username: researcher" \
     "http://localhost:8080/rag/manual-search/data?message=What%20are%20the%20HR%20policies?"

# 3. Use web search for current information
curl -H "username: researcher" \
     "http://localhost:8080/rag/web-search/data?message=Latest%20HR%20trends%202024"
```

### Memory Conversation
```bash
# Start conversation
curl -H "username: alice" \
     "http://localhost:8080/memory/unique-conversation-id?message=My%20name%20is%20Alice"

# Continue with context
curl -H "username: alice" \
     "http://localhost:8080/memory/unique-conversation-id?message=Do%20you%20remember%20my%20name?"
```

### Structured Output
```bash
# Get country and cities as structured data
curl "http://localhost:8080/structured/pojo?message=Tell%20me%20about%20France%20and%20its%20major%20cities"
```

## Dependencies

- **Spring Boot 3.5.6**
- **Spring AI 1.1.2**
- **Qdrant Vector Database**
- **OpenAI API**
- **Tika Document Reader**
- **Lombok**

## Notes

- All chat responses are context-aware when using memory endpoints
- RAG endpoints require Qdrant to be running
- PDF loading is enabled and will process `hr.pdf` on startup
- Web search requires Tavily API configuration
- The application supports both OpenAI and Ollama models
