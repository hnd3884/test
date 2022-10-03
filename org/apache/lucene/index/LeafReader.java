package org.apache.lucene.index;

import org.apache.lucene.util.Bits;
import java.io.IOException;

public abstract class LeafReader extends IndexReader
{
    private final LeafReaderContext readerContext;
    
    protected LeafReader() {
        this.readerContext = new LeafReaderContext(this);
    }
    
    @Override
    public final LeafReaderContext getContext() {
        this.ensureOpen();
        return this.readerContext;
    }
    
    protected static void addCoreClosedListenerAsReaderClosedListener(final IndexReader reader, final CoreClosedListener listener) {
        reader.addReaderClosedListener(new CoreClosedListenerWrapper(listener));
    }
    
    protected static void removeCoreClosedListenerAsReaderClosedListener(final IndexReader reader, final CoreClosedListener listener) {
        reader.removeReaderClosedListener(new CoreClosedListenerWrapper(listener));
    }
    
    public abstract void addCoreClosedListener(final CoreClosedListener p0);
    
    public abstract void removeCoreClosedListener(final CoreClosedListener p0);
    
    public abstract Fields fields() throws IOException;
    
    @Override
    public final int docFreq(final Term term) throws IOException {
        final Terms terms = this.terms(term.field());
        if (terms == null) {
            return 0;
        }
        final TermsEnum termsEnum = terms.iterator();
        if (termsEnum.seekExact(term.bytes())) {
            return termsEnum.docFreq();
        }
        return 0;
    }
    
    @Override
    public final long totalTermFreq(final Term term) throws IOException {
        final Terms terms = this.terms(term.field());
        if (terms == null) {
            return 0L;
        }
        final TermsEnum termsEnum = terms.iterator();
        if (termsEnum.seekExact(term.bytes())) {
            return termsEnum.totalTermFreq();
        }
        return 0L;
    }
    
    @Override
    public final long getSumDocFreq(final String field) throws IOException {
        final Terms terms = this.terms(field);
        if (terms == null) {
            return 0L;
        }
        return terms.getSumDocFreq();
    }
    
    @Override
    public final int getDocCount(final String field) throws IOException {
        final Terms terms = this.terms(field);
        if (terms == null) {
            return 0;
        }
        return terms.getDocCount();
    }
    
    @Override
    public final long getSumTotalTermFreq(final String field) throws IOException {
        final Terms terms = this.terms(field);
        if (terms == null) {
            return 0L;
        }
        return terms.getSumTotalTermFreq();
    }
    
    public final Terms terms(final String field) throws IOException {
        return this.fields().terms(field);
    }
    
    public final PostingsEnum postings(final Term term, final int flags) throws IOException {
        assert term.field() != null;
        assert term.bytes() != null;
        final Terms terms = this.terms(term.field());
        if (terms != null) {
            final TermsEnum termsEnum = terms.iterator();
            if (termsEnum.seekExact(term.bytes())) {
                return termsEnum.postings(null, flags);
            }
        }
        return null;
    }
    
    public final PostingsEnum postings(final Term term) throws IOException {
        return this.postings(term, 8);
    }
    
    public abstract NumericDocValues getNumericDocValues(final String p0) throws IOException;
    
    public abstract BinaryDocValues getBinaryDocValues(final String p0) throws IOException;
    
    public abstract SortedDocValues getSortedDocValues(final String p0) throws IOException;
    
    public abstract SortedNumericDocValues getSortedNumericDocValues(final String p0) throws IOException;
    
    public abstract SortedSetDocValues getSortedSetDocValues(final String p0) throws IOException;
    
    public abstract Bits getDocsWithField(final String p0) throws IOException;
    
    public abstract NumericDocValues getNormValues(final String p0) throws IOException;
    
    public abstract FieldInfos getFieldInfos();
    
    public abstract Bits getLiveDocs();
    
    public abstract void checkIntegrity() throws IOException;
    
    @Deprecated
    public final DocsEnum termDocsEnum(final Term term) throws IOException {
        assert term.field() != null;
        assert term.bytes() != null;
        final Terms terms = this.terms(term.field());
        if (terms != null) {
            final TermsEnum termsEnum = terms.iterator();
            if (termsEnum.seekExact(term.bytes())) {
                return termsEnum.docs(this.getLiveDocs(), null);
            }
        }
        return null;
    }
    
    @Deprecated
    public final DocsAndPositionsEnum termPositionsEnum(final Term term) throws IOException {
        assert term.field() != null;
        assert term.bytes() != null;
        final Terms terms = this.terms(term.field());
        if (terms != null) {
            final TermsEnum termsEnum = terms.iterator();
            if (termsEnum.seekExact(term.bytes())) {
                return termsEnum.docsAndPositions(this.getLiveDocs(), null);
            }
        }
        return null;
    }
    
    private static class CoreClosedListenerWrapper implements ReaderClosedListener
    {
        private final CoreClosedListener listener;
        
        CoreClosedListenerWrapper(final CoreClosedListener listener) {
            this.listener = listener;
        }
        
        @Override
        public void onClose(final IndexReader reader) throws IOException {
            this.listener.onClose(reader.getCoreCacheKey());
        }
        
        @Override
        public int hashCode() {
            return this.listener.hashCode();
        }
        
        @Override
        public boolean equals(final Object other) {
            return other instanceof CoreClosedListenerWrapper && this.listener.equals(((CoreClosedListenerWrapper)other).listener);
        }
    }
    
    public interface CoreClosedListener
    {
        void onClose(final Object p0) throws IOException;
    }
}
