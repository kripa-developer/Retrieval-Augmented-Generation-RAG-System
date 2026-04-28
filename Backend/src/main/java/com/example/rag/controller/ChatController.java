package com.example.rag.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
public class ChatController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public ChatController(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
    }

    @GetMapping("/ask")
    public String ask(@RequestParam(value = "question", defaultValue = "What is this document about?") String question) {
        List<Document> similarDocuments = vectorStore.similaritySearch(
            SearchRequest.query(question).withTopK(3)
        );

        String documentsContent = similarDocuments.stream()
                .map(Document::getContent)
                .collect(Collectors.joining("\n\n"));

        return chatClient.prompt()
                .system(s -> s.text("You are a helpful assistant. Use the following pieces of retrieved context to answer the question.\nIf you don't know the answer, just say that you don't know.\n\nContext:\n{context}")
                        .param("context", documentsContent))
                .user(question)
                .call()
                .content();
    }
}
