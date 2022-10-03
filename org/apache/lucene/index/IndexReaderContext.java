package org.apache.lucene.index;

import java.util.List;

public abstract class IndexReaderContext
{
    public final CompositeReaderContext parent;
    public final boolean isTopLevel;
    public final int docBaseInParent;
    public final int ordInParent;
    
    IndexReaderContext(final CompositeReaderContext parent, final int ordInParent, final int docBaseInParent) {
        if (!(this instanceof CompositeReaderContext) && !(this instanceof LeafReaderContext)) {
            throw new Error("This class should never be extended by custom code!");
        }
        this.parent = parent;
        this.docBaseInParent = docBaseInParent;
        this.ordInParent = ordInParent;
        this.isTopLevel = (parent == null);
    }
    
    public abstract IndexReader reader();
    
    public abstract List<LeafReaderContext> leaves() throws UnsupportedOperationException;
    
    public abstract List<IndexReaderContext> children();
}
