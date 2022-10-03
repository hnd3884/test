package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.SortingMergePolicy;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class EarlyTerminatingSortingCollector extends FilterCollector
{
    protected final Sort sort;
    protected final int numDocsToCollect;
    private final Sort mergePolicySort;
    private final AtomicBoolean terminatedEarly;
    
    public static boolean canEarlyTerminate(final Sort searchSort, final Sort mergePolicySort) {
        final SortField[] fields1 = searchSort.getSort();
        final SortField[] fields2 = mergePolicySort.getSort();
        return fields1.length <= fields2.length && Arrays.asList(fields1).equals(Arrays.asList(fields2).subList(0, fields1.length));
    }
    
    public EarlyTerminatingSortingCollector(final Collector in, final Sort sort, final int numDocsToCollect, final Sort mergePolicySort) {
        super(in);
        this.terminatedEarly = new AtomicBoolean(false);
        if (numDocsToCollect <= 0) {
            throw new IllegalArgumentException("numDocsToCollect must always be > 0, got " + numDocsToCollect);
        }
        if (!canEarlyTerminate(sort, mergePolicySort)) {
            throw new IllegalStateException("Cannot early terminate with sort order " + sort + " if segments are sorted with " + mergePolicySort);
        }
        this.sort = sort;
        this.numDocsToCollect = numDocsToCollect;
        this.mergePolicySort = mergePolicySort;
    }
    
    public LeafCollector getLeafCollector(final LeafReaderContext context) throws IOException {
        if (SortingMergePolicy.isSorted(context.reader(), this.mergePolicySort)) {
            return (LeafCollector)new FilterLeafCollector(super.getLeafCollector(context)) {
                private int numCollected;
                
                public void collect(final int doc) throws IOException {
                    super.collect(doc);
                    if (++this.numCollected >= EarlyTerminatingSortingCollector.this.numDocsToCollect) {
                        EarlyTerminatingSortingCollector.this.terminatedEarly.set(true);
                        throw new CollectionTerminatedException();
                    }
                }
            };
        }
        return super.getLeafCollector(context);
    }
    
    public boolean terminatedEarly() {
        return this.terminatedEarly.get();
    }
}
