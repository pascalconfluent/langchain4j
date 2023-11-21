package dev.langchain4j.store.embedding;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Percentage.withPercentage;

/**
 * A minimum set of tests that each implementation of {@link EmbeddingStore} must pass.
 */
public abstract class EmbeddingStoreIT extends EmbeddingStoreWithoutMetadataIT {

    @Test
    void should_add_embedding_with_segment_with_metadata() {

        TextSegment segment = TextSegment.from("hello", Metadata.from("test-key", "test-value"));
        Embedding embedding = embeddingModel().embed(segment.text()).content();

        String id = embeddingStore().add(embedding, segment);
        assertThat(id).isNotBlank();

        awaitUntilPersisted();

        List<EmbeddingMatch<TextSegment>> relevant = embeddingStore().findRelevant(embedding, 10);
        assertThat(relevant).hasSize(1);

        EmbeddingMatch<TextSegment> match = relevant.get(0);
        assertThat(match.score()).isCloseTo(1, withPercentage(1));
        assertThat(match.embeddingId()).isEqualTo(id);
        assertThat(match.embedding()).isEqualTo(embedding);
        assertThat(match.embedded()).isEqualTo(segment);
    }


    @Test
    void should_update_embedding() {
        String id;
        {
            TextSegment segment = TextSegment.from("hello", Metadata.from("test-key", "test-value"));
            Embedding embedding = embeddingModel().embed(segment.text()).content();

            id = embeddingStore().add(embedding, segment);
            assertThat(id).isNotBlank();

            awaitUntilPersisted();

            List<EmbeddingMatch<TextSegment>> relevant = embeddingStore().findRelevant(embedding, 10);
            assertThat(relevant).hasSize(1);

            EmbeddingMatch<TextSegment> match = relevant.get(0);
            assertThat(match.score()).isCloseTo(1, withPercentage(1));
            assertThat(match.embeddingId()).isEqualTo(id);
            assertThat(match.embedding()).isEqualTo(embedding);
            assertThat(match.embedded()).isEqualTo(segment);
        }

        TextSegment updatedSegment = TextSegment.from("Hello World", Metadata.from("test-key", "test-updated-value"));
        Embedding updatedEmbedding = embeddingModel().embed(updatedSegment.text()).content();
        embeddingStore().update(id, updatedEmbedding, updatedSegment);

        awaitUntilPersisted();

        List<EmbeddingMatch<TextSegment>> updatedRelevant = embeddingStore().findRelevant(updatedEmbedding, 10);
        assertThat(updatedRelevant).hasSize(1);

        EmbeddingMatch<TextSegment> match = updatedRelevant.get(0);
        assertThat(match.score()).isCloseTo(1, withPercentage(1));
        assertThat(match.embeddingId()).isEqualTo(id);
        assertThat(match.embedding()).isEqualTo(updatedEmbedding);
        assertThat(match.embedded()).isEqualTo(updatedSegment);
        assertThat(match.embedded().metadata("test-key")).isEqualTo("test-updated-value");
    }

    @Test
    void should_delete_embedding() {
        TextSegment segment = TextSegment.from("hello", Metadata.from("test-key", "test-value"));
        Embedding embedding = embeddingModel().embed(segment.text()).content();

        TextSegment worldSegment = TextSegment.from("world", Metadata.from("test-key", "test-value"));
        Embedding worldEmbedding = embeddingModel().embed(worldSegment.text()).content();

        String id = embeddingStore().add(embedding, segment);
        assertThat(id).isNotBlank();

        String worldId = embeddingStore().add(worldEmbedding, worldSegment);
        assertThat(worldId).isNotBlank();

        awaitUntilPersisted();

        List<EmbeddingMatch<TextSegment>> relevant = embeddingStore().findRelevant(embedding, 10);
        assertThat(relevant).hasSize(1);

        EmbeddingMatch<TextSegment> match = relevant.get(0);
        assertThat(match.score()).isCloseTo(1, withPercentage(1));
        assertThat(match.embeddingId()).isEqualTo(id);
        assertThat(match.embedding()).isEqualTo(embedding);
        assertThat(match.embedded()).isEqualTo(segment);


        embeddingStore().delete(id);

        List<EmbeddingMatch<TextSegment>> updatedRelevant = embeddingStore().findRelevant(embedding, 10);
        assertThat(updatedRelevant).hasSize(0);
    }

}
