package org.apache.lucene.search.highlight;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import java.io.IOException;
import org.apache.lucene.analysis.miscellaneous.LimitTokenOffsetFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Fields;

public class TokenSources
{
    private TokenSources() {
    }
    
    public static TokenStream getTokenStream(final String field, final Fields tvFields, final String text, final Analyzer analyzer, final int maxStartOffset) throws IOException {
        TokenStream tokenStream = getTermVectorTokenStreamOrNull(field, tvFields, maxStartOffset);
        if (tokenStream != null) {
            return tokenStream;
        }
        tokenStream = analyzer.tokenStream(field, text);
        if (maxStartOffset >= 0 && maxStartOffset < text.length() - 1) {
            tokenStream = (TokenStream)new LimitTokenOffsetFilter(tokenStream, maxStartOffset);
        }
        return tokenStream;
    }
    
    public static TokenStream getTermVectorTokenStreamOrNull(final String field, final Fields tvFields, final int maxStartOffset) throws IOException {
        if (tvFields == null) {
            return null;
        }
        final Terms tvTerms = tvFields.terms(field);
        if (tvTerms == null || !tvTerms.hasOffsets()) {
            return null;
        }
        return new TokenStreamFromTermVector(tvTerms, maxStartOffset);
    }
    
    @Deprecated
    public static TokenStream getAnyTokenStream(final IndexReader reader, final int docId, final String field, final Document document, final Analyzer analyzer) throws IOException {
        TokenStream ts = null;
        final Fields vectors = reader.getTermVectors(docId);
        if (vectors != null) {
            final Terms vector = vectors.terms(field);
            if (vector != null) {
                ts = getTokenStream(vector);
            }
        }
        if (ts == null) {
            ts = getTokenStream(document, field, analyzer);
        }
        return ts;
    }
    
    @Deprecated
    public static TokenStream getAnyTokenStream(final IndexReader reader, final int docId, final String field, final Analyzer analyzer) throws IOException {
        TokenStream ts = null;
        final Fields vectors = reader.getTermVectors(docId);
        if (vectors != null) {
            final Terms vector = vectors.terms(field);
            if (vector != null) {
                ts = getTokenStream(vector);
            }
        }
        if (ts == null) {
            ts = getTokenStream(reader, docId, field, analyzer);
        }
        return ts;
    }
    
    @Deprecated
    public static TokenStream getTokenStream(final Terms vector, final boolean tokenPositionsGuaranteedContiguous) throws IOException {
        return getTokenStream(vector);
    }
    
    @Deprecated
    public static TokenStream getTokenStream(final Terms tpv) throws IOException {
        if (!tpv.hasOffsets()) {
            throw new IllegalArgumentException("Highlighting requires offsets from the TokenStream.");
        }
        return new TokenStreamFromTermVector(tpv, -1);
    }
    
    @Deprecated
    public static TokenStream getTokenStreamWithOffsets(final IndexReader reader, final int docId, final String field) throws IOException {
        final Fields vectors = reader.getTermVectors(docId);
        if (vectors == null) {
            return null;
        }
        final Terms vector = vectors.terms(field);
        if (vector == null) {
            return null;
        }
        if (!vector.hasOffsets()) {
            return null;
        }
        return getTokenStream(vector);
    }
    
    @Deprecated
    public static TokenStream getTokenStream(final IndexReader reader, final int docId, final String field, final Analyzer analyzer) throws IOException {
        final Document doc = reader.document(docId);
        return getTokenStream(doc, field, analyzer);
    }
    
    @Deprecated
    public static TokenStream getTokenStream(final Document doc, final String field, final Analyzer analyzer) {
        final String contents = doc.get(field);
        if (contents == null) {
            throw new IllegalArgumentException("Field " + field + " in document is not stored and cannot be analyzed");
        }
        return getTokenStream(field, contents, analyzer);
    }
    
    @Deprecated
    public static TokenStream getTokenStream(final String field, final String contents, final Analyzer analyzer) {
        return analyzer.tokenStream(field, contents);
    }
}
