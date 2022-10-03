package org.apache.lucene.index;

public final class NoMergeScheduler extends MergeScheduler
{
    public static final MergeScheduler INSTANCE;
    
    private NoMergeScheduler() {
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public void merge(final IndexWriter writer, final MergeTrigger trigger, final boolean newMergesFound) {
    }
    
    public MergeScheduler clone() {
        return this;
    }
    
    static {
        INSTANCE = new NoMergeScheduler();
    }
}
