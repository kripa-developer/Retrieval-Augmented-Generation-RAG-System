# Nexus AI - RAG Backend

A Retrieval-Augmented Generation (RAG) system built with **Spring Boot** and **Spring AI**. This backend processes PDF documents, stores them in a Vector Store, and provides an intelligent chat interface powered by OpenAI.

## 🚀 Features
- **PDF Ingestion**: Automatic parsing and tokenization of PDF files.
- **RAG Architecture**: Uses embeddings to retrieve context for grounded AI responses.
- **Vector Store**: In-memory `SimpleVectorStore` for fast, simple setup.
- **Dynamic Uploads**: REST endpoint for uploading and indexing new documents on the fly.
- **Spring AI 1.0.0-M1**: Leverages the latest fluent APIs and modern HTTP clients.

## 🛠️ Setup

### Prerequisites
- Java 17+
- Maven
- OpenAI API Key

### Configuration
1. Open `src/main/resources/application.properties`.
2. Provide your API key: `spring.ai.openai.api-key=sk-...`

### Corporate Proxy Note
If you are behind a corporate proxy, this project is configured with a `custom-settings.xml` and `AiConfig.java` using `JdkClientHttpRequestFactory` to handle authentication handshakes.

## 🏃 Running the App
Run the following command from the root directory:

```bash
mvn spring-boot:run -s custom-settings.xml
```

The server will start on **http://localhost:8080**.

## 📡 API Endpoints
- `POST /upload`: Upload a PDF file to the knowledge base.
- `GET /ask?question=...`: Ask a question based on the uploaded documents.
