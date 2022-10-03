package org.apache.lucene.index;

import org.apache.lucene.util.AttributeSource;
import java.util.Objects;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import java.io.IOException;

@Deprecated
public abstract class DocsAndPositionsEnum extends DocsEnum
{
    public static final int FLAG_OFFSETS = 1;
    public static final int FLAG_PAYLOADS = 2;
    @Deprecated
    public static final short OLD_NULL_SEMANTICS = 16384;
    
    protected DocsAndPositionsEnum() {
    }
    
    @Override
    public abstract int nextPosition() throws IOException;
    
    @Override
    public abstract int startOffset() throws IOException;
    
    @Override
    public abstract int endOffset() throws IOException;
    
    @Override
    public abstract BytesRef getPayload() throws IOException;
    
    static DocsAndPositionsEnum wrap(final PostingsEnum postings, final Bits liveDocs) {
        return new DocsAndPositionsEnumWrapper(postings, liveDocs);
    }
    
    static PostingsEnum unwrap(final DocsEnum docs) {
        if (docs instanceof DocsAndPositionsEnumWrapper) {
            return ((DocsAndPositionsEnumWrapper)docs).in;
        }
        if (docs == null) {
            return null;
        }
        throw new AssertionError();
    }
    
    static Bits unwrapliveDocs(final DocsEnum docs) {
        if (docs instanceof DocsAndPositionsEnumWrapper) {
            return ((DocsAndPositionsEnumWrapper)docs).liveDocs;
        }
        if (docs == null) {
            return null;
        }
        throw new AssertionError();
    }
    
    static class DocsAndPositionsEnumWrapper extends DocsAndPositionsEnum
    {
        final PostingsEnum in;
        final Bits liveDocs;
        
        DocsAndPositionsEnumWrapper(final PostingsEnum in, final Bits liveDocs) {
            this.in = Objects.requireNonNull(in);
            this.liveDocs = liveDocs;
        }
        
        private int doNext(int doc) throws IOException {
            while (doc != Integer.MAX_VALUE && this.liveDocs != null && !this.liveDocs.get(doc)) {
                doc = this.in.nextDoc();
            }
            return doc;
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
        public int freq() throws IOException {
            return this.in.freq();
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
        public int nextDoc() throws IOException {
            return this.doNext(this.in.nextDoc());
        }
        
        @Override
        public int advance(final int target) throws IOException {
            return this.doNext(this.in.advance(target));
        }
        
        @Override
        public long cost() {
            return this.in.cost();
        }
    }
}
