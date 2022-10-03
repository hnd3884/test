package org.apache.lucene.index;

import java.io.IOException;

public class LogByteSizeMergePolicy extends LogMergePolicy
{
    public static final double DEFAULT_MIN_MERGE_MB = 1.6;
    public static final double DEFAULT_MAX_MERGE_MB = 2048.0;
    public static final double DEFAULT_MAX_MERGE_MB_FOR_FORCED_MERGE = 9.223372036854776E18;
    
    public LogByteSizeMergePolicy() {
        this.minMergeSize = 1677721L;
        this.maxMergeSize = 2147483648L;
        this.maxMergeSizeForForcedMerge = Long.MAX_VALUE;
    }
    
    @Override
    protected long size(final SegmentCommitInfo info, final IndexWriter writer) throws IOException {
        return this.sizeBytes(info, writer);
    }
    
    public void setMaxMergeMB(final double mb) {
        this.maxMergeSize = (long)(mb * 1024.0 * 1024.0);
    }
    
    public double getMaxMergeMB() {
        return this.maxMergeSize / 1024.0 / 1024.0;
    }
    
    public void setMaxMergeMBForForcedMerge(final double mb) {
        this.maxMergeSizeForForcedMerge = (long)(mb * 1024.0 * 1024.0);
    }
    
    public double getMaxMergeMBForForcedMerge() {
        return this.maxMergeSizeForForcedMerge / 1024.0 / 1024.0;
    }
    
    public void setMinMergeMB(final double mb) {
        this.minMergeSize = (long)(mb * 1024.0 * 1024.0);
    }
    
    public double getMinMergeMB() {
        return this.minMergeSize / 1024.0 / 1024.0;
    }
}
