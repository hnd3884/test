package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.util.BitSet;
import org.apache.lucene.util.BitDocIdSet;
import org.apache.lucene.util.FixedBitSet;
import org.apache.lucene.util.Bits;

public abstract class DocValuesDocIdSet extends DocIdSet
{
    protected final int maxDoc;
    protected final Bits acceptDocs;
    
    public DocValuesDocIdSet(final int maxDoc, final Bits acceptDocs) {
        this.maxDoc = maxDoc;
        this.acceptDocs = acceptDocs;
    }
    
    protected abstract boolean matchDoc(final int p0);
    
    @Override
    public long ramBytesUsed() {
        return 0L;
    }
    
    @Override
    public final Bits bits() {
        return (this.acceptDocs == null) ? new Bits() {
            @Override
            public boolean get(final int docid) {
                return DocValuesDocIdSet.this.matchDoc(docid);
            }
            
            @Override
            public int length() {
                return DocValuesDocIdSet.this.maxDoc;
            }
        } : new Bits() {
            @Override
            public boolean get(final int docid) {
                return DocValuesDocIdSet.this.acceptDocs.get(docid) && DocValuesDocIdSet.this.matchDoc(docid);
            }
            
            @Override
            public int length() {
                return DocValuesDocIdSet.this.maxDoc;
            }
        };
    }
    
    @Override
    public final DocIdSetIterator iterator() throws IOException {
        if (this.acceptDocs == null) {
            return new DocIdSetIterator() {
                private int doc = -1;
                
                @Override
                public int docID() {
                    return this.doc;
                }
                
                @Override
                public int nextDoc() {
                    do {
                        ++this.doc;
                        if (this.doc >= DocValuesDocIdSet.this.maxDoc) {
                            return this.doc = Integer.MAX_VALUE;
                        }
                    } while (!DocValuesDocIdSet.this.matchDoc(this.doc));
                    return this.doc;
                }
                
                @Override
                public int advance(final int target) {
                    this.doc = target;
                    while (this.doc < DocValuesDocIdSet.this.maxDoc) {
                        if (DocValuesDocIdSet.this.matchDoc(this.doc)) {
                            return this.doc;
                        }
                        ++this.doc;
                    }
                    return this.doc = Integer.MAX_VALUE;
                }
                
                @Override
                public long cost() {
                    return DocValuesDocIdSet.this.maxDoc;
                }
            };
        }
        if (this.acceptDocs instanceof FixedBitSet) {
            return new FilteredDocIdSetIterator(new BitDocIdSet((BitSet)this.acceptDocs).iterator()) {
                @Override
                protected boolean match(final int doc) {
                    return DocValuesDocIdSet.this.matchDoc(doc);
                }
            };
        }
        return new DocIdSetIterator() {
            private int doc = -1;
            
            @Override
            public int docID() {
                return this.doc;
            }
            
            @Override
            public int nextDoc() {
                return this.advance(this.doc + 1);
            }
            
            @Override
            public int advance(final int target) {
                this.doc = target;
                while (this.doc < DocValuesDocIdSet.this.maxDoc) {
                    if (DocValuesDocIdSet.this.acceptDocs.get(this.doc) && DocValuesDocIdSet.this.matchDoc(this.doc)) {
                        return this.doc;
                    }
                    ++this.doc;
                }
                return this.doc = Integer.MAX_VALUE;
            }
            
            @Override
            public long cost() {
                return DocValuesDocIdSet.this.maxDoc;
            }
        };
    }
}
