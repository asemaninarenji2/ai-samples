package com.mainapp.openai.rag.custom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.postretrieval.document.DocumentPostProcessor;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;


import java.util.List;
@Slf4j
public class PIIMaskingDocumentPostProcessor implements DocumentPostProcessor {
    @Override
    public List<Document> process(Query query, List<Document> documents) {
        Assert.notNull(query, "query can't be null!");
        Assert.notNull(documents, "documents can't be null!");
        Assert.notEmpty(documents, "documents can't be empty!");
        if (CollectionUtils.isEmpty(documents)) {
            return documents;
        }
        log.info("Processing {} and masking sensitive data! ", query.text());
        return documents.stream().map(document -> {
            String text = document.getText() != null ? document.getText() : "";
            String maskedText = text.replaceAll("\\b\\d-\\d{3}-\\d{4}\\b", "X-XXX-XXXX");
            document.mutate().text(maskedText).metadata("pii_masked", true).build();
         return document;
        }).toList();

    }
}
