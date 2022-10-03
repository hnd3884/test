package org.apache.lucene.index;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CompositeReaderContext extends IndexReaderContext
{
    private final List<IndexReaderContext> children;
    private final List<LeafReaderContext> leaves;
    private final CompositeReader reader;
    
    static CompositeReaderContext create(final CompositeReader reader) {
        return new Builder(reader).build();
    }
    
    CompositeReaderContext(final CompositeReaderContext parent, final CompositeReader reader, final int ordInParent, final int docbaseInParent, final List<IndexReaderContext> children) {
        this(parent, reader, ordInParent, docbaseInParent, children, null);
    }
    
    CompositeReaderContext(final CompositeReader reader, final List<IndexReaderContext> children, final List<LeafReaderContext> leaves) {
        this(null, reader, 0, 0, children, leaves);
    }
    
    private CompositeReaderContext(final CompositeReaderContext parent, final CompositeReader reader, final int ordInParent, final int docbaseInParent, final List<IndexReaderContext> children, final List<LeafReaderContext> leaves) {
        super(parent, ordInParent, docbaseInParent);
        this.children = Collections.unmodifiableList((List<? extends IndexReaderContext>)children);
        this.leaves = ((leaves == null) ? null : Collections.unmodifiableList((List<? extends LeafReaderContext>)leaves));
        this.reader = reader;
    }
    
    @Override
    public List<LeafReaderContext> leaves() throws UnsupportedOperationException {
        if (!this.isTopLevel) {
            throw new UnsupportedOperationException("This is not a top-level context.");
        }
        assert this.leaves != null;
        return this.leaves;
    }
    
    @Override
    public List<IndexReaderContext> children() {
        return this.children;
    }
    
    @Override
    public CompositeReader reader() {
        return this.reader;
    }
    
    private static final class Builder
    {
        private final CompositeReader reader;
        private final List<LeafReaderContext> leaves;
        private int leafDocBase;
        
        public Builder(final CompositeReader reader) {
            this.leaves = new ArrayList<LeafReaderContext>();
            this.leafDocBase = 0;
            this.reader = reader;
        }
        
        public CompositeReaderContext build() {
            return (CompositeReaderContext)this.build(null, this.reader, 0, 0);
        }
        
        private IndexReaderContext build(final CompositeReaderContext parent, final IndexReader reader, final int ord, final int docBase) {
            if (reader instanceof LeafReader) {
                final LeafReader ar = (LeafReader)reader;
                final LeafReaderContext atomic = new LeafReaderContext(parent, ar, ord, docBase, this.leaves.size(), this.leafDocBase);
                this.leaves.add(atomic);
                this.leafDocBase += reader.maxDoc();
                return atomic;
            }
            final CompositeReader cr = (CompositeReader)reader;
            final List<? extends IndexReader> sequentialSubReaders = cr.getSequentialSubReaders();
            final List<IndexReaderContext> children = Arrays.asList(new IndexReaderContext[sequentialSubReaders.size()]);
            CompositeReaderContext newParent;
            if (parent == null) {
                newParent = new CompositeReaderContext(cr, children, this.leaves);
            }
            else {
                newParent = new CompositeReaderContext(parent, cr, ord, docBase, children);
            }
            int newDocBase = 0;
            for (int i = 0, c = sequentialSubReaders.size(); i < c; ++i) {
                final IndexReader r = (IndexReader)sequentialSubReaders.get(i);
                children.set(i, this.build(newParent, r, i, newDocBase));
                newDocBase += r.maxDoc();
            }
            assert newDocBase == cr.maxDoc();
            return newParent;
        }
    }
}
