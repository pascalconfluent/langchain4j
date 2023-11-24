package dev.langchain4j.data.document;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import dev.langchain4j.data.document.source.StringSource;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

import static dev.langchain4j.data.document.DocumentLoaderUtils.parserFor;
import static dev.langchain4j.internal.Exceptions.illegalArgument;
import static dev.langchain4j.internal.ValidationUtils.ensureNotBlank;
import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;

/**
 * Loads a document from the specified file, detecting document type automatically.
 */
@Slf4j
public class GCPBucketFileLoader {

    /**
     * Loads a document from the specified file, detecting document type automatically.
     *
     * @param bucket       Bucket to load from
     * @param documentPath path to the file
     * @return document
     * @throws IllegalArgumentException if specified path is not a file or does not exist
     */
    public static Document loadDocument(Bucket bucket, String documentPath) {
        ensureNotNull(bucket, "bucket");
        ensureNotBlank(documentPath, "documentPath");

        final Blob blob = bucket.get(documentPath);
        if (blob == null || !blob.exists()) {
            log.error("Blob {} does not exist", documentPath);
            throw illegalArgument("%s does not exists in bucket %s", documentPath, bucket.getName());
        }


        return loadDocument(blob);
    }

    /**
     * Loads a document from the specified file, detecting document type automatically.
     *
     * @param blob Blob to load
     * @return document
     * @throws IllegalArgumentException if specified path is not a file or does not exist
     */
    public static Document loadDocument(Blob blob) {
        ensureNotNull(blob, "blob");

        if (blob.isDirectory()) {
            log.error("Blob {} is a directory", blob.getName());
            throw illegalArgument("%s is not a file", blob.getName());
        }

        final DocumentType documentType = DocumentType.of(blob.getName());
        return loadDocument(blob, documentType);
    }

    /**
     * Loads a document from the specified file.
     *
     * @param blob         Blob to load
     * @param documentType type of the document
     * @return document
     * @throws IllegalArgumentException if specified path is not a file
     */
    public static Document loadDocument(Blob blob, DocumentType documentType) {
        ensureNotNull(blob, "blob");

        if (blob.isDirectory()) {
            log.error("Blob {} is a directory", blob.getName());
            throw illegalArgument("%s is not a file", blob.getName());
        }

        final String content = new String(blob.getContent());
        return DocumentLoaderUtils.load(new StringSource(content), parserFor(documentType));
    }

}
