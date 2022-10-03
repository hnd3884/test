package org.apache.lucene.index;

import java.io.IOException;
import java.util.Map;

public final class NoMergePolicy extends MergePolicy
{
    public static final MergePolicy INSTANCE;
    
    private NoMergePolicy() {
    }
    
    @Override
    public MergeSpecification findMerges(final MergeTrigger mergeTrigger, final SegmentInfos segmentInfos, final IndexWriter writer) {
        return null;
    }
    
    @Override
    public MergeSpecification findForcedMerges(final SegmentInfos segmentInfos, final int maxSegmentCount, final Map<SegmentCommitInfo, Boolean> segmentsToMerge, final IndexWriter writer) {
        return null;
    }
    
    @Override
    public MergeSpecification findForcedDeletesMerges(final SegmentInfos segmentInfos, final IndexWriter writer) {
        return null;
    }
    
    @Override
    public boolean useCompoundFile(final SegmentInfos segments, final SegmentCommitInfo newSegment, final IndexWriter writer) {
        return newSegment.info.getUseCompoundFile();
    }
    
    @Override
    protected long size(final SegmentCommitInfo info, final IndexWriter writer) throws IOException {
        return Long.MAX_VALUE;
    }
    
    @Override
    public double getNoCFSRatio() {
        return super.getNoCFSRatio();
    }
    
    @Override
    public void setMaxCFSSegmentSizeMB(final double v) {
        super.setMaxCFSSegmentSizeMB(v);
    }
    
    @Override
    public void setNoCFSRatio(final double noCFSRatio) {
        super.setNoCFSRatio(noCFSRatio);
    }
    
    @Override
    public String toString() {
        return "NoMergePolicy";
    }
    
    static {
        INSTANCE = new NoMergePolicy();
    }
}
