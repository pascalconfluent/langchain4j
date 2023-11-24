package dev.langchain4j.data.document.parser;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentType;
import dev.langchain4j.data.document.Metadata;
import lombok.extern.slf4j.Slf4j;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.text.TextContentRenderer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static dev.langchain4j.data.document.Document.DOCUMENT_TYPE;

/**
 * Parses a markdown document.
 */
@Slf4j
public class MarkdownDocumentParser implements DocumentParser {

    private final Parser parser = Parser.builder().build();
    private final TextContentRenderer renderer = TextContentRenderer.builder().build();

    @Override
    public Document parse(InputStream inputStream) {

        try {
            final InputStreamReader reader = new InputStreamReader(inputStream);
            final Node document = parser.parseReader(reader);
            final String content = renderer.render(document);

            return Document.from(content, Metadata.from(DOCUMENT_TYPE, DocumentType.MD.toString()));
        } catch (IOException e) {
            log.error("Failed to parse markdown document", e);
            throw new RuntimeException(e);
        }
    }
}
