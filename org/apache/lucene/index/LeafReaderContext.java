package org.apache.lucene.index;

import java.util.Collections;
import java.util.List;

public final class LeafReaderContext extends IndexReaderContext
{
    public final int ord;
    public final int docBase;
    private final LeafReader reader;
    private final List<LeafReaderContext> leaves;
    
    LeafReaderContext(final CompositeReaderContext parent, final LeafReader reader, final int ord, final int docBase, final int leafOrd, final int leafDocBase) {
        super(parent, ord, docBase);
        this.ord = leafOrd;
        this.docBase = leafDocBase;
        this.reader = reader;
        this.leaves = (this.isTopLevel ? Collections.singletonList(this) : null);
    }
    
    LeafReaderContext(final LeafReader leafReader) {
        this(null, leafReader, 0, 0, 0, 0);
    }
    
    @Override
    public List<LeafReaderContext> leaves() {
        if (!this.isTopLevel) {
            throw new UnsupportedOperationException("This is not a top-level context.");
        }
        assert this.leaves != null;
        return this.leaves;
    }
    
    @Override
    public List<IndexReaderContext> children() {
        return null;
    }
    
    @Override
    public LeafReader reader() {
        return this.reader;
    }
    
    @Override
    public String toString() {
        return "LeafReaderContext(" + this.reader + " docBase=" + this.docBase + " ord=" + this.ord + ")";
    }
}
