package com.example.rag.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataLoadingService {

    private static final Logger logger = LoggerFactory.getLogger(DataLoadingService.class);

    @Value("classpath:/data/sample.pdf")
    private Resource pdfResource;

    private final VectorStore vectorStore;

    public DataLoadingService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    public void init() {
        if (pdfResource.exists()) {
            loadDocument(pdfResource);
        } else {
            logger.warn("No default sample.pdf found in src/main/resources/data.");
        }
    }

    public void loadDocument(Resource resource) {
        try {
            logger.info("Loading document: {}", resource.getFilename());
            PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(
                    resource,
                    PdfDocumentReaderConfig.builder()
                            .withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder()
                                    .withNumberOfBottomTextLinesToDelete(0)
                                    .withNumberOfTopPagesToSkipBeforeDelete(0)
                                    .build())
                            .withPagesPerDocument(1)
                            .build());

            TokenTextSplitter textSplitter = new TokenTextSplitter();
            List<Document> splitDocuments = textSplitter.apply(pdfReader.get());
            
            vectorStore.accept(splitDocuments);
            logger.info("Document {} loaded into vector store successfully!", resource.getFilename());
        } catch (Exception e) {
            logger.error("Error loading document: {}", e.getMessage());
            throw new RuntimeException("Failed to load document", e);
        }
    }
}
