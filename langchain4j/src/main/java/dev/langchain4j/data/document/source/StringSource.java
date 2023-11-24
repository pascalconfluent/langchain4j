package dev.langchain4j.data.document.source;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSource;
import dev.langchain4j.data.document.Metadata;
import lombok.AllArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@AllArgsConstructor
public class StringSource implements DocumentSource {

    private final String content;

    @Override
    public InputStream inputStream() throws IOException {
        return new ByteArrayInputStream(content.getBytes());
    }

    @Override
    public Metadata metadata() {
        return Metadata.from(Document.CONTENT_LENGTH, content.length());
    }
}
