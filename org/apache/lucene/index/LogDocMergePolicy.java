package org.apache.lucene.index;

import java.io.IOException;

public class LogDocMergePolicy extends LogMergePolicy
{
    public static final int DEFAULT_MIN_MERGE_DOCS = 1000;
    
    public LogDocMergePolicy() {
        this.minMergeSize = 1000L;
        this.maxMergeSize = Long.MAX_VALUE;
        this.maxMergeSizeForForcedMerge = Long.MAX_VALUE;
    }
    
    @Override
    protected long size(final SegmentCommitInfo info, final IndexWriter writer) throws IOException {
        return this.sizeDocs(info, writer);
    }
    
    public void setMinMergeDocs(final int minMergeDocs) {
        this.minMergeSize = minMergeDocs;
    }
    
    public int getMinMergeDocs() {
        return (int)this.minMergeSize;
    }
}
