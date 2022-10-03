package org.apache.lucene.facet.taxonomy.writercache;

import org.apache.lucene.facet.taxonomy.FacetLabel;

public abstract class LabelToOrdinal
{
    protected int counter;
    public static final int INVALID_ORDINAL = -2;
    
    public int getMaxOrdinal() {
        return this.counter;
    }
    
    public int getNextOrdinal() {
        return this.counter++;
    }
    
    public abstract void addLabel(final FacetLabel p0, final int p1);
    
    public abstract int getOrdinal(final FacetLabel p0);
}
