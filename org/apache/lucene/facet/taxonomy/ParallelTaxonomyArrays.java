package org.apache.lucene.facet.taxonomy;

public abstract class ParallelTaxonomyArrays
{
    public abstract int[] parents();
    
    public abstract int[] children();
    
    public abstract int[] siblings();
}
