package org.apache.lucene.index;

import java.util.Objects;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.AttributeSource;
import java.util.Iterator;
import java.io.IOException;
import org.apache.lucene.util.Bits;

public class FilterLeafReader extends LeafReader
{
    protected final LeafReader in;
    
    public static LeafReader unwrap(LeafReader reader) {
        while (reader instanceof FilterLeafReader) {
            reader = ((FilterLeafReader)reader).in;
        }
        return reader;
    }
    
    public FilterLeafReader(final LeafReader in) {
        if (in == null) {
            throw new NullPointerException("incoming LeafReader cannot be null");
        }
        (this.in = in).registerParentReader(this);
    }
    
    @Override
    public void addCoreClosedListener(final CoreClosedListener listener) {
        this.in.addCoreClosedListener(CoreClosedListenerWrapper.wrap(listener, this.getCoreCacheKey(), this.in.getCoreCacheKey()));
    }
    
    @Override
    public void removeCoreClosedListener(final CoreClosedListener listener) {
        this.in.removeCoreClosedListener(CoreClosedListenerWrapper.wrap(listener, this.getCoreCacheKey(), this.in.getCoreCacheKey()));
    }
    
    @Override
    public Bits getLiveDocs() {
        this.ensureOpen();
        return this.in.getLiveDocs();
    }
    
    @Override
    public FieldInfos getFieldInfos() {
        return this.in.getFieldInfos();
    }
    
    @Override
    public Fields getTermVectors(final int docID) throws IOException {
        this.ensureOpen();
        return this.in.getTermVectors(docID);
    }
    
    @Override
    public int numDocs() {
        return this.in.numDocs();
    }
    
    @Override
    public int maxDoc() {
        return this.in.maxDoc();
    }
    
    @Override
    public void document(final int docID, final StoredFieldVisitor visitor) throws IOException {
        this.ensureOpen();
        this.in.document(docID, visitor);
    }
    
    @Override
    protected void doClose() throws IOException {
        this.in.close();
    }
    
    @Override
    public Fields fields() throws IOException {
        this.ensureOpen();
        return this.in.fields();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder("FilterLeafReader(");
        buffer.append(this.in);
        buffer.append(')');
        return buffer.toString();
    }
    
    @Override
    public NumericDocValues getNumericDocValues(final String field) throws IOException {
        this.ensureOpen();
        return this.in.getNumericDocValues(field);
    }
    
    @Override
    public BinaryDocValues getBinaryDocValues(final String field) throws IOException {
        this.ensureOpen();
        return this.in.getBinaryDocValues(field);
    }
    
    @Override
    public SortedDocValues getSortedDocValues(final String field) throws IOException {
        this.ensureOpen();
        return this.in.getSortedDocValues(field);
    }
    
    @Override
    public SortedNumericDocValues getSortedNumericDocValues(final String field) throws IOException {
        this.ensureOpen();
        return this.in.getSortedNumericDocValues(field);
    }
    
    @Override
    public SortedSetDocValues getSortedSetDocValues(final String field) throws IOException {
        this.ensureOpen();
        return this.in.getSortedSetDocValues(field);
    }
    
    @Override
    public NumericDocValues getNormValues(final String field) throws IOException {
        this.ensureOpen();
        return this.in.getNormValues(field);
    }
    
    @Override
    public Bits getDocsWithField(final String field) throws IOException {
        this.ensureOpen();
        return this.in.getDocsWithField(field);
    }
    
    @Override
    public void checkIntegrity() throws IOException {
        this.ensureOpen();
        this.in.checkIntegrity();
    }
    
    public LeafReader getDelegate() {
        return this.in;
    }
    
    public static class FilterFields extends Fields
    {
        protected final Fields in;
        
        public FilterFields(final Fields in) {
            if (in == null) {
                throw new NullPointerException("incoming Fields cannot be null");
            }
            this.in = in;
        }
        
        @Override
        public Iterator<String> iterator() {
            return this.in.iterator();
        }
        
        @Override
        public Terms terms(final String field) throws IOException {
            return this.in.terms(field);
        }
        
        @Override
        public int size() {
            return this.in.size();
        }
    }
    
    public static class FilterTerms extends Terms
    {
        protected final Terms in;
        
        public FilterTerms(final Terms in) {
            if (in == null) {
                throw new NullPointerException("incoming Terms cannot be null");
            }
            this.in = in;
        }
        
        @Override
        public TermsEnum iterator() throws IOException {
            return this.in.iterator();
        }
        
        @Override
        public long size() throws IOException {
            return this.in.size();
        }
        
        @Override
        public long getSumTotalTermFreq() throws IOException {
            return this.in.getSumTotalTermFreq();
        }
        
        @Override
        public long getSumDocFreq() throws IOException {
            return this.in.getSumDocFreq();
        }
        
        @Override
        public int getDocCount() throws IOException {
            return this.in.getDocCount();
        }
        
        @Override
        public boolean hasFreqs() {
            return this.in.hasFreqs();
        }
        
        @Override
        public boolean hasOffsets() {
            return this.in.hasOffsets();
        }
        
        @Override
        public boolean hasPositions() {
            return this.in.hasPositions();
        }
        
        @Override
        public boolean hasPayloads() {
            return this.in.hasPayloads();
        }
        
        @Override
        public Object getStats() throws IOException {
            return this.in.getStats();
        }
    }
    
    public static class FilterTermsEnum extends TermsEnum
    {
        protected final TermsEnum in;
        
        public FilterTermsEnum(final TermsEnum in) {
            if (in == null) {
                throw new NullPointerException("incoming TermsEnum cannot be null");
            }
            this.in = in;
        }
        
        @Override
        public AttributeSource attributes() {
            return this.in.attributes();
        }
        
        @Override
        public SeekStatus seekCeil(final BytesRef text) throws IOException {
            return this.in.seekCeil(text);
        }
        
        @Override
        public void seekExact(final long ord) throws IOException {
            this.in.seekExact(ord);
        }
        
        @Override
        public BytesRef next() throws IOException {
            return this.in.next();
        }
        
        @Override
        public BytesRef term() throws IOException {
            return this.in.term();
        }
        
        @Override
        public long ord() throws IOException {
            return this.in.ord();
        }
        
        @Override
        public int docFreq() throws IOException {
            return this.in.docFreq();
        }
        
        @Override
        public long totalTermFreq() throws IOException {
            return this.in.totalTermFreq();
        }
        
        @Override
        public PostingsEnum postings(final PostingsEnum reuse, final int flags) throws IOException {
            return this.in.postings(reuse, flags);
        }
    }
    
    public static class FilterPostingsEnum extends PostingsEnum
    {
        protected final PostingsEnum in;
        
        public FilterPostingsEnum(final PostingsEnum in) {
            if (in == null) {
                throw new NullPointerException("incoming PostingsEnum cannot be null");
            }
            this.in = in;
        }
        
        @Override
        public AttributeSource attributes() {
            return this.in.attributes();
        }
        
        @Override
        public int docID() {
            return this.in.docID();
        }
        
        @Override
        public int freq() throws IOException {
            return this.in.freq();
        }
        
        @Override
        public int nextDoc() throws IOException {
            return this.in.nextDoc();
        }
        
        @Override
        public int advance(final int target) throws IOException {
            return this.in.advance(target);
        }
        
        @Override
        public int nextPosition() throws IOException {
            return this.in.nextPosition();
        }
        
        @Override
        public int startOffset() throws IOException {
            return this.in.startOffset();
        }
        
        @Override
        public int endOffset() throws IOException {
            return this.in.endOffset();
        }
        
        @Override
        public BytesRef getPayload() throws IOException {
            return this.in.getPayload();
        }
        
        @Override
        public long cost() {
            return this.in.cost();
        }
    }
    
    private static class CoreClosedListenerWrapper implements CoreClosedListener
    {
        private final CoreClosedListener in;
        private final Object thisCoreKey;
        private final Object inCoreKey;
        
        public static CoreClosedListener wrap(final CoreClosedListener listener, final Object thisCoreKey, final Object inCoreKey) {
            if (thisCoreKey == inCoreKey) {
                return listener;
            }
            return new CoreClosedListenerWrapper(listener, thisCoreKey, inCoreKey);
        }
        
        private CoreClosedListenerWrapper(final CoreClosedListener in, final Object thisCoreKey, final Object inCoreKey) {
            this.in = in;
            this.thisCoreKey = thisCoreKey;
            this.inCoreKey = inCoreKey;
        }
        
        @Override
        public void onClose(final Object ownerCoreCacheKey) throws IOException {
            assert this.inCoreKey == ownerCoreCacheKey;
            this.in.onClose(this.thisCoreKey);
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == null || obj.getClass() != CoreClosedListenerWrapper.class) {
                return false;
            }
            final CoreClosedListenerWrapper that = (CoreClosedListenerWrapper)obj;
            return this.in.equals(that.in) && this.thisCoreKey == that.thisCoreKey;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.getClass(), this.in, this.thisCoreKey);
        }
    }
}
